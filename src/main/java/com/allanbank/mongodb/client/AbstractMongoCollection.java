/*
 * Copyright 2011, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoDbConfiguration;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.connection.FutureCallback;
import com.allanbank.mongodb.connection.messsage.GetLastError;

/**
 * Helper class for forward all methods to the canonical version (which is
 * abstract in this class).
 * <p>
 * This class keeps the clutter in the derived class to a minimum and also deals
 * with the conversion of the asynchronous method invocations into synchronous
 * methods for those uses cases that do not require an asynchronous interface.
 * </p>
 * 
 * @copyright 2011, Allanbank Consulting, Inc., All Rights Reserved
 */
public abstract class AbstractMongoCollection implements MongoCollection {

    /** The client for interacting with MongoDB. */
    protected final Client myClient;

    /** The name of the database we interact with. */
    protected final MongoDatabase myDatabase;

    /** The name of the collection we interact with. */
    protected final String myName;

    /**
     * Create a new AbstractMongoCollection.
     * 
     * @param client
     *            The client for interacting with MongoDB.
     * @param database
     *            The database we interact with.
     * @param name
     *            The name of the collection we interact with.
     */
    public AbstractMongoCollection(final Client client,
            final MongoDatabase database, final String name) {
        super();

        myClient = client;
        myDatabase = database;
        myName = name;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Document, boolean, Durability)} method with false as
     * the <tt>singleDelete</tt> argument and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Document, boolean, Durability)
     */
    @Override
    public int delete(final Document query) throws MongoDbException {
        try {
            return deleteAsync(query, false, getDefaultDurability()).get()
                    .intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Document, boolean, Durability)} method with the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Document, boolean, Durability)
     */
    @Override
    public int delete(final Document query, final boolean singleDelete)
            throws MongoDbException {
        try {
            return deleteAsync(query, singleDelete, getDefaultDurability())
                    .get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Document, boolean, Durability)} method.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Document, boolean, Durability)
     */
    @Override
    public int delete(final Document query, final boolean singleDelete,
            final Durability durability) throws MongoDbException {
        try {
            return deleteAsync(query, singleDelete, getDefaultDurability())
                    .get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Document, boolean, Durability)} method with false as
     * the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Document, boolean, Durability)
     */
    @Override
    public int delete(final Document query, final Durability durability)
            throws MongoDbException {
        try {
            return deleteAsync(query, false, durability).get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, Document, boolean, Durability)} method with
     * false as the <tt>singleDelete</tt> argument and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document, boolean, Durability)
     */
    @Override
    public void deleteAsync(final Callback<Integer> results,
            final Document query) throws MongoDbException {
        deleteAsync(results, query, false, getDefaultDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, Document, boolean, Durability)} method with
     * the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document)
     */
    @Override
    public void deleteAsync(final Callback<Integer> results,
            final Document query, final boolean singleDelete)
            throws MongoDbException {
        deleteAsync(results, query, false, getDefaultDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>delete</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document, boolean, Durability)
     */
    @Override
    public abstract void deleteAsync(final Callback<Integer> results,
            final Document query, final boolean singleDelete,
            final Durability durability) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, Document, boolean, Durability)} method with
     * false as the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document, boolean)
     */
    @Override
    public void deleteAsync(final Callback<Integer> results,
            final Document query, final Durability durability)
            throws MongoDbException {
        deleteAsync(results, query, false, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, Document, boolean, Durability)} method with
     * false as the <tt>singleDelete</tt> argument and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document)
     */
    @Override
    public Future<Integer> deleteAsync(final Document query)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        deleteAsync(future, query, false, getDefaultDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, Document, boolean, Durability)} method with
     * the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document)
     */
    @Override
    public Future<Integer> deleteAsync(final Document query,
            final boolean singleDelete) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        deleteAsync(future, query, singleDelete, getDefaultDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #deleteAsync(Callback, Document)} method.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document)
     */
    @Override
    public Future<Integer> deleteAsync(final Document query,
            final boolean singleDelete, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        deleteAsync(future, query, singleDelete, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #deleteAsync(Callback, Document)} method
     * with false as the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, Document)
     */
    @Override
    public Future<Integer> deleteAsync(final Document query,
            final Durability durability) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        deleteAsync(future, query, false, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with
     * <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query)
            throws MongoDbException {
        try {
            return findAsync(query, null, 0, 0, false, false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with
     * <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query, final boolean replicaOk)
            throws MongoDbException {
        try {
            return findAsync(query, null, 0, 0, false, false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with 0
     * for the <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query,
            final Document returnFields) throws MongoDbException {
        try {
            return findAsync(query, returnFields, 0, 0, false, false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with 0
     * for the <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query,
            final Document returnFields, final boolean replicaOk)
            throws MongoDbException {
        try {
            return findAsync(query, returnFields, 0, 0, replicaOk, false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)}
     * method.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query,
            final Document returnFields, final int numberToReturn,
            final int numberToSkip, final boolean replicaOk,
            final boolean partial) throws MongoDbException {
        try {
            return findAsync(query, returnFields, numberToReturn, numberToSkip,
                    replicaOk, partial).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with
     * <code>null</code> for the <tt>returnFields</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query,
            final int numberToReturn, final int numberToSkip)
            throws MongoDbException {
        try {
            return findAsync(query, null, numberToReturn, numberToSkip, false,
                    false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Document, Document, int, int, boolean, boolean)} with
     * <code>null</code> for the <tt>returnFields</tt> and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Iterator<Document> find(final Document query,
            final int numberToReturn, final int numberToSkip,
            final boolean replicaOk) throws MongoDbException {
        try {
            return findAsync(query, null, numberToReturn, numberToSkip,
                    replicaOk, false).get();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query) throws MongoDbException {
        findAsync(results, query, null, 0, 0, false, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final boolean replicaOk)
            throws MongoDbException {
        findAsync(results, query, null, 0, 0, replicaOk, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with 0 for the <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and
     * false for <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final Document returnFields)
            throws MongoDbException {
        findAsync(results, query, returnFields, 0, 0, false, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, numberToReturn</tt>
     * and <tt>numberToSkip</tt>, and false for <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final Document returnFields,
            final boolean replicaOk) throws MongoDbException {
        findAsync(results, query, returnFields, 0, 0, replicaOk, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>find</code> method that implementations must
     * override.
     * </p>
     * 
     * @see MongoCollection#findAsync(Callback, Document, Document, int, int,
     *      boolean, boolean)
     */
    @Override
    public abstract void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final Document returnFields,
            final int numberToReturn, final int numberToSkip,
            final boolean replicaOk, final boolean partial)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt> and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final int numberToReturn,
            final int numberToSkip) throws MongoDbException {
        findAsync(results, query, null, numberToReturn, numberToSkip, false,
                false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt> and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public void findAsync(final Callback<Iterator<Document>> results,
            final Document query, final int numberToReturn,
            final int numberToSkip, final boolean replicaOk)
            throws MongoDbException {
        findAsync(results, query, null, numberToReturn, numberToSkip,
                replicaOk, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query)
            throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, null, 0, 0, false, false);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, 0 for the
     * <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final boolean replicaOk) throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, null, 0, 0, replicaOk, false);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with 0 for the <tt>numberToReturn</tt> and <tt>numberToSkip</tt>, and
     * false for <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final Document returnFields) throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, returnFields, 0, 0, false, false);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final Document returnFields, final boolean replicaOk)
            throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, returnFields, 0, 0, replicaOk, false);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * method.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final Document returnFields, final int numberToReturn,
            final int numberToSkip, final boolean replicaOk,
            final boolean partial) throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, returnFields, numberToReturn, numberToSkip,
                replicaOk, partial);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt>, and false for
     * <tt>replicaOk</tt> and <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final int numberToReturn, final int numberToSkip)
            throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, null, numberToReturn, numberToSkip, false,
                false);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAsync(Callback, Document, Document, int, int, boolean, boolean)}
     * with <code>null</code> for the <tt>returnFields</tt> and false for
     * <tt>partial</tt>.
     * </p>
     * 
     * @see #findAsync(Callback, Document, Document, int, int, boolean, boolean)
     */
    @Override
    public Future<Iterator<Document>> findAsync(final Document query,
            final int numberToReturn, final int numberToSkip,
            final boolean replicaOk) throws MongoDbException {
        final FutureCallback<Iterator<Document>> future = new FutureCallback<Iterator<Document>>();

        findAsync(future, query, null, numberToReturn, numberToSkip, replicaOk,
                false);

        return future;
    }

    /**
     * Returns the name of the database.
     * 
     * @return The name of the database.
     */
    @Override
    public String getDatabaseName() {
        return myDatabase.getName();
    }

    /**
     * Returns the name of the collection.
     * 
     * @return The name of the collection.
     */
    @Override
    public String getName() {
        return myName;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(boolean, Durability, Document...)} method with the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(boolean, Durability, Document[])
     */
    @Override
    public int insert(final boolean continueOnError,
            final Document... documents) throws MongoDbException {
        try {
            return insertAsync(continueOnError, getDefaultDurability(),
                    documents).get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(boolean, Durability, Document...)} method.
     * </p>
     * 
     * @see MongoCollection#insertAsync(boolean, Durability, Document[])
     */
    @Override
    public int insert(final boolean continueOnError,
            final Durability durability, final Document... documents)
            throws MongoDbException {
        try {
            return insertAsync(continueOnError, durability, documents).get()
                    .intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(boolean, Durability, Document...)} method with
     * <tt>continueOnError</tt> set to false and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(boolean, Durability, Document[])
     */
    @Override
    public int insert(final Document... documents) throws MongoDbException {
        try {
            return insertAsync(false, getDefaultDurability(), documents).get()
                    .intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(boolean, Durability, Document...)} method with
     * <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see MongoCollection#insertAsync(boolean, Durability, Document[])
     */
    @Override
    public int insert(final Durability durability, final Document... documents)
            throws MongoDbException {
        try {
            return insertAsync(false, durability, documents).get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public Future<Integer> insertAsync(final boolean continueOnError,
            final Document... documents) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, continueOnError, getDefaultDurability(), documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with <tt>continueOnError</tt> set to false and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public Future<Integer> insertAsync(final boolean continueOnError,
            final Durability durability, final Document... documents)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, continueOnError, durability, documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final boolean continueOnError, final Document... documents)
            throws MongoDbException {
        insertAsync(results, continueOnError, getDefaultDurability(), documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>insert</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public abstract void insertAsync(final Callback<Integer> results,
            final boolean continueOnError, final Durability durability,
            final Document... documents) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with <tt>continueOnError</tt> set to false and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final Document... documents) throws MongoDbException {
        insertAsync(results, false, getDefaultDurability(), documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final Durability durability, final Document... documents)
            throws MongoDbException {
        insertAsync(results, false, durability, documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with <tt>continueOnError</tt> set to false and the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public Future<Integer> insertAsync(final Document... documents)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, false, getDefaultDurability(), documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, Document...)} method
     * with <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      Document[])
     */
    @Override
    public Future<Integer> insertAsync(final Durability durability,
            final Document... documents) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, false, durability, documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Document, Document, boolean, boolean, Durability)}
     * method with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Document, Document, boolean, boolean, Durability)
     */
    @Override
    public int update(final Document query, final Document update)
            throws MongoDbException {
        try {
            return updateAsync(query, update, true, false,
                    getDefaultDurability()).get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Document, Document, boolean, boolean, Durability)}
     * method with the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Document, Document, boolean, boolean, Durability)
     */
    @Override
    public int update(final Document query, final Document update,
            final boolean multiUpdate, final boolean upsert)
            throws MongoDbException {
        try {
            return updateAsync(query, update, multiUpdate, upsert,
                    getDefaultDurability()).get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Document, Document, boolean, boolean, Durability)}
     * method.
     * </p>
     * 
     * @see #updateAsync(Document, Document, boolean, boolean, Durability)
     */
    @Override
    public int update(final Document query, final Document update,
            final boolean multiUpdate, final boolean upsert,
            final Durability durability) throws MongoDbException {
        try {
            return updateAsync(query, update, multiUpdate, upsert, durability)
                    .get().intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Document, Document, boolean, boolean, Durability)}
     * method with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #updateAsync(Document, Document, boolean, boolean, Durability)
     */
    @Override
    public int update(final Document query, final Document update,
            final Durability durability) throws MongoDbException {
        try {
            return updateAsync(query, update, true, false, durability).get()
                    .intValue();
        }
        catch (final InterruptedException e) {
            throw new MongoDbException(e);
        }
        catch (final ExecutionException e) {
            if (e.getCause() instanceof MongoDbException) {
                throw (MongoDbException) e.getCause();
            }
            throw new MongoDbException(e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public void updateAsync(final Callback<Integer> results,
            final Document query, final Document update)
            throws MongoDbException {
        updateAsync(results, query, update, true, false, getDefaultDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * using the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public void updateAsync(final Callback<Integer> results,
            final Document query, final Document update,
            final boolean multiUpdate, final boolean upsert)
            throws MongoDbException {
        updateAsync(results, query, update, multiUpdate, upsert,
                getDefaultDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>update</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#updateAsync(Callback, Document, Document, boolean,
     *      boolean, Durability)
     */
    @Override
    public abstract void updateAsync(final Callback<Integer> results,
            final Document query, final Document update,
            final boolean multiUpdate, final boolean upsert,
            final Durability durability) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public void updateAsync(final Callback<Integer> results,
            final Document query, final Document update,
            final Durability durability) throws MongoDbException {
        updateAsync(results, query, update, true, false, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public Future<Integer> updateAsync(final Document query,
            final Document update) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        updateAsync(future, query, update, true, false, getDefaultDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * using the {@link #getDefaultDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public Future<Integer> updateAsync(final Document query,
            final Document update, final boolean multiUpdate,
            final boolean upsert) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        updateAsync(future, query, update, multiUpdate, upsert,
                getDefaultDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * method.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public Future<Integer> updateAsync(final Document query,
            final Document update, final boolean multiUpdate,
            final boolean upsert, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        updateAsync(future, query, update, multiUpdate, upsert, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, Document, Document, boolean, boolean, Durability)}
     * with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #updateAsync(Callback, Document, Document, boolean, boolean,
     *      Durability)
     */
    @Override
    public Future<Integer> updateAsync(final Document query,
            final Document update, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        updateAsync(future, query, update, true, false, durability);

        return future;
    }

    /**
     * Converts the {@link Durability} into a {@link GetLastError} command.
     * 
     * @param durability
     *            The {@link Durability} to convert.
     * @return The {@link GetLastError} command.
     */
    protected GetLastError asGetLastError(final Durability durability) {
        return new GetLastError(getDatabaseName(), durability.isWaitForFsync(),
                durability.isWaitForJournal(), durability.getWaitForReplicas(),
                durability.getWaitTimeoutMillis());
    }

    /**
     * Returns the {@link Durability} from the {@link MongoDbConfiguration}.
     * 
     * @return The default durability from the {@link MongoDbConfiguration}.
     */
    protected Durability getDefaultDurability() {
        return myClient.getDefaultDurability();
    }
}