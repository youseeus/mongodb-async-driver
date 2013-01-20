/*
 * Copyright 2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import java.util.List;

import com.allanbank.mongodb.MongoCursorControl;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.StreamCallback;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.connection.message.GetMore;
import com.allanbank.mongodb.connection.message.KillCursors;
import com.allanbank.mongodb.connection.message.Query;
import com.allanbank.mongodb.connection.message.Reply;
import com.allanbank.mongodb.error.ReplyException;

/**
 * Callback to convert a {@link Query} {@link Reply} into a series of callback
 * for each document received.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
/* package */final class QueryStreamingCallback extends
        AbstractValidatingReplyCallback implements MongoCursorControl {

    /** The server the original request was sent to. */
    private volatile String myAddress;

    /** The requested batch size. */
    private int myBatchSize;

    /** The original query. */
    private final Client myClient;

    /**
     * Flag to indictate that the stream has been closed.
     */
    private volatile boolean myClosed = false;

    /** The name of the collection the query was originally created on. */
    private final String myCollectionName;

    /** The original query. */
    private long myCursorId = 0;

    /** The name of the database the query was originally created on. */
    private final String myDatabaseName;

    /** The callback to forward the returned documents to. */
    private final StreamCallback<Document> myForwardCallback;

    /**
     * The maximum number of document to return from the cursor. Zero or
     * negative means all.
     */
    private int myLimit = 0;

    /** The original query that started the cursor, if known. */
    private final Query myOriginalQuery;

    /** The last reply. */
    private volatile Reply myReply;

    /**
     * Flag to shutdown this iterator gracefully without closing the cursor on
     * the server.
     */
    private boolean myShutdown = false;

    /**
     * Create a new QueryCallback from a cursor document.
     * 
     * @param client
     *            The client interface to the server.
     * @param cursorDocument
     *            The original query.
     * @param results
     *            The callback to update with each document.
     * 
     * @see MongoIteratorImpl#asDocument()
     */
    public QueryStreamingCallback(final Client client,
            final Document cursorDocument,
            final StreamCallback<Document> results) {

        final String ns = cursorDocument.get(StringElement.class, "ns")
                .getValue();
        String db = ns;
        String collection = ns;
        final int index = ns.indexOf('.');
        if (0 < index) {
            db = ns.substring(0, index);
            collection = ns.substring(index + 1);
        }

        myOriginalQuery = null;
        myClient = client;
        myDatabaseName = db;
        myCollectionName = collection;
        myForwardCallback = results;
        myCursorId = cursorDocument.get(NumericElement.class, "$cursor_id")
                .getLongValue();
        myLimit = cursorDocument.get(NumericElement.class, "$limit")
                .getIntValue();
        myBatchSize = cursorDocument.get(NumericElement.class, "$batch_size")
                .getIntValue();
        myAddress = cursorDocument.get(StringElement.class, "$server")
                .getValue();
    }

    /**
     * Create a new QueryCallback.
     * 
     * @param client
     *            The client interface to the server.
     * @param originalQuery
     *            The original query.
     * @param results
     *            The callback to update with each document.
     */
    public QueryStreamingCallback(final Client client,
            final Query originalQuery, final StreamCallback<Document> results) {

        myClient = client;
        myDatabaseName = originalQuery.getDatabaseName();
        myCollectionName = originalQuery.getCollectionName();
        myBatchSize = originalQuery.getBatchSize();
        myOriginalQuery = originalQuery;
        myForwardCallback = results;
        myLimit = originalQuery.getLimit();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the current state of the stream as a document.
     * </p>
     */
    @Override
    public Document asDocument() {
        final long cursorId = myCursorId;

        if (cursorId != 0) {
            final DocumentBuilder b = BuilderFactory.start();
            b.add("ns", myDatabaseName + "." + myCollectionName);
            b.add("$cursor_id", cursorId);
            b.add("$server", myAddress);
            b.add("$limit", myLimit);
            b.add("$batch_size", myBatchSize);

            return b.build();
        }
        return null;
    }

    /**
     * Overridden to close the iterator and send a {@link KillCursors} for the
     * open cursor, if any.
     */
    @Override
    public void close() {
        synchronized (myForwardCallback) {
            myClosed = true;
            sendKill();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward the error the the user.
     * </p>
     */
    @Override
    public void exception(final Throwable thrown) {
        try {
            synchronized (myForwardCallback) {
                myForwardCallback.exception(thrown);
            }
        }
        finally {
            close();
        }
    }

    /**
     * Returns the server the original request was sent to.
     * 
     * @return The server the original request was sent to.
     */
    public String getAddress() {
        return myAddress;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to set the batch size.
     * </p>
     */
    @Override
    public int getBatchSize() {
        return myBatchSize;
    }

    /**
     * Restarts the stream by sending a request for the next batch of documents.
     * 
     * @throws MongoDbException
     *             On a failure to send the request for more document.
     */
    public void restart() {
        sendRequest();
    }

    /**
     * Sets the value of the server the original request was sent to.
     * 
     * @param address
     *            The new value for the server the original request was sent to.
     */
    public void setAddress(final String address) {
        myAddress = address;
        // For races make sure that the push has the server name.
        if (myReply != null) {
            final Reply reply = myReply;
            myReply = null;
            push(reply);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to get the batch size.
     * </p>
     */
    @Override
    public void setBatchSize(final int batchSize) {
        myBatchSize = batchSize;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to stop requesting more batches of documents.
     * </p>
     */
    @Override
    public void stop() {
        myShutdown = true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to add the {@link Query} to the exception.
     * </p>
     * 
     * @see AbstractReplyCallback#asError(Reply, int, int, String)
     */
    @Override
    protected MongoDbException asError(final Reply reply, final int okValue,
            final int errorNumber, final String errorMessage) {
        return new ReplyException(okValue, errorNumber, errorMessage,
                myOriginalQuery, reply);
    }

    /**
     * Returns the client value.
     * 
     * @return The client value.
     */
    protected Client getClient() {
        return myClient;
    }

    /**
     * Returns the collection name.
     * 
     * @return The collection name.
     */
    protected String getCollectionName() {
        return myCollectionName;
    }

    /**
     * Returns the cursor Id value.
     * 
     * @return The cursor Id value.
     */
    protected long getCursorId() {
        return myCursorId;
    }

    /**
     * Returns the database name value.
     * 
     * @return The database name value.
     */
    protected String getDatabaseName() {
        return myDatabaseName;
    }

    /**
     * Returns the limit value.
     * 
     * @return The limit value.
     */
    protected int getLimit() {
        return myLimit;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to push the documents to the application's callback.
     * </p>
     * 
     * @see AbstractReplyCallback#convert(Reply)
     */
    @Override
    protected void handle(final Reply reply) throws MongoDbException {
        myReply = reply;
        if (myAddress != null) {
            push(reply);
        }
    }

    /**
     * Loads more documents. This issues a get_more command as soon as the
     * previous results start to be used.
     * 
     * @param reply
     *            The last reply received.
     * @return The list of loaded documents.
     * 
     * @throws RuntimeException
     *             On a failure to load documents.
     */
    protected List<Document> loadDocuments(final Reply reply)
            throws RuntimeException {

        myCursorId = reply.getCursorId();

        // Setup the documents and adjust the limit for the documents we have.
        // Do this before the fetch again so the nextBatchSize() has the updated
        // limit.
        List<Document> docs = reply.getResults();
        if (0 < myLimit) {
            // Check if we have too many docs.
            if (myLimit <= docs.size()) {
                docs = docs.subList(0, myLimit);
                close();
            }
            myLimit -= docs.size();
        }

        // Pre-fetch the next set of documents while we iterate over the
        // documents we just got.
        if ((myCursorId != 0) && !myShutdown) {
            sendRequest();
        }
        // Exhausted the cursor - no more results.
        // Don't need to kill the cursor since we exhausted it.

        return docs;
    }

    /**
     * Computes the size for the next batch of documents to get.
     * 
     * @return The returnNex
     */
    protected int nextBatchSize() {
        if ((0 < myLimit) && (myLimit <= myBatchSize)) {
            return -myLimit;
        }
        return myBatchSize;
    }

    /**
     * Sends a {@link KillCursors} message if there is an active cursor.
     * 
     * @throws MongoDbException
     *             On a failure to send the {@link KillCursors} message.
     */
    protected void sendKill() throws MongoDbException {
        final long cursorId = myCursorId;
        if ((cursorId != 0) && !myShutdown) {
            myCursorId = 0;
            myClient.send(new KillCursors(new long[] { cursorId },
                    ReadPreference.server(myAddress)), null);
        }
    }

    /**
     * Sends a request to start the next match of documents.
     * 
     * @throws MongoDbException
     *             On a failure to send the request.
     */
    protected void sendRequest() throws MongoDbException {
        final GetMore getMore = new GetMore(myDatabaseName, myCollectionName,
                myCursorId, nextBatchSize(), ReadPreference.server(myAddress));

        myClient.send(getMore, this);
    }

    /**
     * Pushes the results from the reply to the application's callback.
     * 
     * @param reply
     *            The reply containing the results to push to the application's
     *            callback.
     */
    private void push(final Reply reply) {
        // Request the load in the synchronized block so there is only 1
        // outstanding request.
        synchronized (myForwardCallback) {
            if (myClosed) {
                myCursorId = reply.getCursorId();
                sendKill();
            }
            else {
                try {
                    for (final Document result : loadDocuments(reply)) {
                        myForwardCallback.callback(result);
                    }
                    if (myCursorId == 0) {
                        // Signal the end of the results.
                        myForwardCallback.done();
                    }
                }
                catch (final RuntimeException re) {
                    exception(re);
                    close();
                }
            }
        }
    }
}