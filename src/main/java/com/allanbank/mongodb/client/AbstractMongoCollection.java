/*
 * Copyright 2011-2012, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import java.util.List;
import java.util.concurrent.Future;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.ClosableIterator;
import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.IntegerElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.builder.Aggregate;
import com.allanbank.mongodb.builder.Distinct;
import com.allanbank.mongodb.builder.Find;
import com.allanbank.mongodb.builder.FindAndModify;
import com.allanbank.mongodb.builder.GroupBy;
import com.allanbank.mongodb.builder.MapReduce;
import com.allanbank.mongodb.connection.FutureCallback;
import com.allanbank.mongodb.connection.message.GetLastError;
import com.allanbank.mongodb.util.FutureUtils;

/**
 * Helper class for forward all methods to the canonical version (which is
 * abstract in this class).
 * <p>
 * This class keeps the clutter in the derived class to a minimum and also deals
 * with the conversion of the asynchronous method invocations into synchronous
 * methods for those uses cases that do not require an asynchronous interface.
 * </p>
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2011-2012, Allanbank Consulting, Inc., All Rights Reserved
 */
public abstract class AbstractMongoCollection implements MongoCollection {

    /**
     * The default for if a delete should only delete the first document it
     * matches.
     */
    public static final boolean DELETE_SINGLE_DELETE_DEFAULT = false;

    /** The default empty index options. */
    public static final Document EMPTY_INDEX_OPTIONS = BuilderFactory.start()
            .build();

    /** The default for if an insert should continue on an error. */
    public static final boolean INSERT_CONTINUE_ON_ERROR_DEFAULT = false;

    /** The default for a UNIQUE index options. */
    public static final Document UNIQUE_INDEX_OPTIONS = BuilderFactory.start()
            .add("unique", true).build();

    /** The default for doing a multiple-update on an update. */
    public static final boolean UPDATE_MULTIUPDATE_DEFAULT = false;

    /** The default for doing an upsert on an update. */
    public static final boolean UPDATE_UPSERT_DEFAULT = false;

    /** The client for interacting with MongoDB. */
    protected final Client myClient;

    /** The name of the database we interact with. */
    protected final MongoDatabase myDatabase;

    /** The name of the collection we interact with. */
    protected final String myName;

    /** The {@link Durability} for writes from this database instance. */
    private Durability myDurability;

    /** The {@link ReadPreference} for reads from this database instance. */
    private ReadPreference myReadPreference;

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
        myDurability = null;
        myReadPreference = null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #aggregateAsync(Aggregate)}.
     * </p>
     * 
     * @see #aggregateAsync(Aggregate)
     */
    @Override
    public List<Document> aggregate(final Aggregate command)
            throws MongoDbException {
        return FutureUtils.unwrap(aggregateAsync(command));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #aggregateAsync(Callback, Aggregate)}.
     * </p>
     * 
     * @see #aggregateAsync(Callback, Aggregate)
     */
    @Override
    public Future<List<Document>> aggregateAsync(final Aggregate command)
            throws MongoDbException {
        final FutureCallback<List<Document>> future = new FutureCallback<List<Document>>();

        aggregateAsync(future, command);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>aggregate</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#aggregateAsync(Callback, Aggregate)
     */
    @Override
    public abstract void aggregateAsync(Callback<List<Document>> results,
            Aggregate command) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #count(DocumentAssignable, ReadPreference)}
     * method with {@link #getReadPreference()} as the <tt>readPreference</tt>
     * argument.
     * </p>
     */
    @Override
    public long count(final DocumentAssignable query) throws MongoDbException {
        return count(query, getReadPreference());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #countAsync(DocumentAssignable, ReadPreference)} method.
     * </p>
     */
    @Override
    public long count(final DocumentAssignable query,
            final ReadPreference readPreference) throws MongoDbException {

        final Future<Long> future = countAsync(query, readPreference);

        return FutureUtils.unwrap(future).longValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #countAsync(Callback, DocumentAssignable, ReadPreference)} method
     * with {@link #getReadPreference()} as the <tt>readPreference</tt>
     * argument.
     * </p>
     */
    @Override
    public void countAsync(final Callback<Long> results,
            final DocumentAssignable query) throws MongoDbException {
        countAsync(results, query, getReadPreference());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>count</code> method that implementations must
     * override.
     * </p>
     */
    @Override
    public abstract void countAsync(Callback<Long> results,
            DocumentAssignable query, ReadPreference readPreference)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #countAsync(Callback, DocumentAssignable, ReadPreference)} method
     * with {@link #getReadPreference()} as the <tt>readPreference</tt>
     * argument.
     * </p>
     * 
     * @param query
     *            The query document.
     * @return A future that will be updated with the number of matching
     *         documents.
     * @throws MongoDbException
     *             On an error finding the documents.
     */
    @Override
    public Future<Long> countAsync(final DocumentAssignable query)
            throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        countAsync(future, query, getReadPreference());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #countAsync(Callback, DocumentAssignable, ReadPreference)} method.
     * </p>
     */
    @Override
    public Future<Long> countAsync(final DocumentAssignable query,
            final ReadPreference readPreference) throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        countAsync(future, query, readPreference);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #createIndex(String, boolean, Element...)}
     * method with <code>null</code> for the name.
     * </p>
     * 
     * @see #createIndex(String, boolean, Element...)
     */
    @Override
    public void createIndex(final boolean unique, final Element... keys)
            throws MongoDbException {
        createIndex(null, unique, keys);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #createIndex(String,DocumentAssignable,Element...)} method with
     * <code>null</code> for <tt>name</tt>.
     * </p>
     * 
     * @see #createIndex(String,DocumentAssignable,Element...)
     */
    @Override
    public void createIndex(final DocumentAssignable options,
            final Element... keys) throws MongoDbException {
        createIndex(null, options, keys);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #createIndex(DocumentAssignable, Element...)} method with
     * {@link #EMPTY_INDEX_OPTIONS} for <tt>options</tt>.
     * </p>
     * 
     * @see #createIndex(DocumentAssignable, Element...)
     */
    @Override
    public void createIndex(final Element... keys) throws MongoDbException {
        createIndex(EMPTY_INDEX_OPTIONS, keys);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #createIndex(String,DocumentAssignable,Element...)} method with
     * {@link #UNIQUE_INDEX_OPTIONS} if {@code unique} is <code>true</code> or
     * {@link #EMPTY_INDEX_OPTIONS} id {@code unique} is <code>false</code>.
     * </p>
     * 
     * @see #createIndex(String, DocumentAssignable, Element...)
     */
    @Override
    public void createIndex(final String name, final boolean unique,
            final Element... keys) throws MongoDbException {
        createIndex(name, unique ? UNIQUE_INDEX_OPTIONS : EMPTY_INDEX_OPTIONS,
                keys);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>createIndex</code> method that
     * implementations must override.
     * </p>
     * 
     * @see MongoCollection#createIndex(String,DocumentAssignable,Element...)
     */
    @Override
    public abstract void createIndex(String name, DocumentAssignable options,
            final Element... keys) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #delete(DocumentAssignable, boolean, Durability)} method with
     * false as the <tt>singleDelete</tt> argument and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #delete(DocumentAssignable, boolean, Durability)
     */
    @Override
    public long delete(final DocumentAssignable query) throws MongoDbException {
        return delete(query, DELETE_SINGLE_DELETE_DEFAULT, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #delete(DocumentAssignable, boolean, Durability)} method with the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #delete(DocumentAssignable, boolean, Durability)
     */
    @Override
    public long delete(final DocumentAssignable query,
            final boolean singleDelete) throws MongoDbException {
        return delete(query, singleDelete, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(DocumentAssignable, boolean, Durability)} method.
     * </p>
     * 
     * @see #deleteAsync(DocumentAssignable, boolean, Durability)
     */
    @Override
    public long delete(final DocumentAssignable query,
            final boolean singleDelete, final Durability durability)
            throws MongoDbException {

        final Future<Long> future = deleteAsync(query, singleDelete, durability);

        return FutureUtils.unwrap(future).longValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(DocumentAssignable, boolean, Durability)} method with
     * false as the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see #delete(DocumentAssignable, boolean, Durability)
     */
    @Override
    public long delete(final DocumentAssignable query,
            final Durability durability) throws MongoDbException {
        return delete(query, DELETE_SINGLE_DELETE_DEFAULT, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, DocumentAssignable, boolean, Durability)}
     * method with false as the <tt>singleDelete</tt> argument and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #deleteAsync(Callback, DocumentAssignable, boolean, Durability)
     */
    @Override
    public void deleteAsync(final Callback<Long> results,
            final DocumentAssignable query) throws MongoDbException {
        deleteAsync(results, query, DELETE_SINGLE_DELETE_DEFAULT,
                getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, DocumentAssignable, boolean, Durability)}
     * method with the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable)
     */
    @Override
    public void deleteAsync(final Callback<Long> results,
            final DocumentAssignable query, final boolean singleDelete)
            throws MongoDbException {
        deleteAsync(results, query, singleDelete, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>delete</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable, boolean,
     *      Durability)
     */
    @Override
    public abstract void deleteAsync(final Callback<Long> results,
            final DocumentAssignable query, final boolean singleDelete,
            final Durability durability) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, DocumentAssignable, boolean, Durability)}
     * method with false as the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable, boolean)
     */
    @Override
    public void deleteAsync(final Callback<Long> results,
            final DocumentAssignable query, final Durability durability)
            throws MongoDbException {
        deleteAsync(results, query, DELETE_SINGLE_DELETE_DEFAULT, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, DocumentAssignable, boolean, Durability)}
     * method with false as the <tt>singleDelete</tt> argument and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<Long> deleteAsync(final DocumentAssignable query)
            throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        deleteAsync(future, query, DELETE_SINGLE_DELETE_DEFAULT,
                getDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #deleteAsync(Callback, DocumentAssignable, boolean, Durability)}
     * method with the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<Long> deleteAsync(final DocumentAssignable query,
            final boolean singleDelete) throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        deleteAsync(future, query, singleDelete, getDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #deleteAsync(Callback, DocumentAssignable)}
     * method.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<Long> deleteAsync(final DocumentAssignable query,
            final boolean singleDelete, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        deleteAsync(future, query, singleDelete, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #deleteAsync(Callback, DocumentAssignable)}
     * method with false as the <tt>singleDelete</tt> argument.
     * </p>
     * 
     * @see MongoCollection#deleteAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<Long> deleteAsync(final DocumentAssignable query,
            final Durability durability) throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        deleteAsync(future, query, DELETE_SINGLE_DELETE_DEFAULT, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #distinctAsync(Distinct)}.
     * </p>
     */
    @Override
    public ArrayElement distinct(final Distinct command)
            throws MongoDbException {
        return FutureUtils.unwrap(distinctAsync(command));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>disitnct</code> method that implementations
     * must override.
     * </p>
     */
    @Override
    public abstract void distinctAsync(Callback<ArrayElement> results,
            Distinct command) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #distinctAsync(Callback, Distinct)}.
     * </p>
     */
    @Override
    public Future<ArrayElement> distinctAsync(final Distinct command)
            throws MongoDbException {
        final FutureCallback<ArrayElement> future = new FutureCallback<ArrayElement>();

        distinctAsync(future, command);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * To generate the name of the index and then drop it.
     * </p>
     */
    @Override
    public boolean dropIndex(final IntegerElement... keys)
            throws MongoDbException {
        return dropIndex(buildIndexName(keys));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>dropIndex</code> method that implementations
     * must override.
     * </p>
     */
    @Override
    public abstract boolean dropIndex(String name) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #explain(Find)} method.
     * </p>
     * 
     * @see #explain(Find)
     */
    @Override
    public Document explain(final DocumentAssignable query)
            throws MongoDbException {
        return explain(new Find.Builder(query).build());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #explainAsync(Find)} method.
     * </p>
     * 
     * @see #explainAsync(Find)
     */
    @Override
    public Document explain(final Find query) throws MongoDbException {
        return FutureUtils.unwrap(explainAsync(query));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>count</code> method that implementations must
     * override.
     * </p>
     */
    @Override
    public abstract void explainAsync(Callback<Document> results, Find query)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #explainAsync(Callback,Find)} method.
     * </p>
     * 
     * @see #explainAsync(Callback,Find)
     */
    @Override
    public Future<Document> explainAsync(final Find query)
            throws MongoDbException {
        final FutureCallback<Document> future = new FutureCallback<Document>();

        explainAsync(future, query);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAsync(DocumentAssignable)} method.
     * </p>
     * 
     * @see #findAsync(DocumentAssignable)
     */
    @Override
    public ClosableIterator<Document> find(final DocumentAssignable query)
            throws MongoDbException {
        return FutureUtils.unwrap(findAsync(query));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAsync(Find)} method.
     * </p>
     * 
     * @see #findAsync(Find)
     */
    @Override
    public ClosableIterator<Document> find(final Find query)
            throws MongoDbException {
        return FutureUtils.unwrap(findAsync(query));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAndModifyAsync(FindAndModify)}.
     * </p>
     * 
     * @see #findAndModifyAsync(FindAndModify)
     */
    @Override
    public Document findAndModify(final FindAndModify command)
            throws MongoDbException {
        return FutureUtils.unwrap(findAndModifyAsync(command));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>findAndModify</code> method that
     * implementations must override.
     * </p>
     * 
     * @see MongoCollection#findAndModifyAsync(Callback, FindAndModify)
     */
    @Override
    public abstract void findAndModifyAsync(Callback<Document> results,
            FindAndModify command) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findAndModifyAsync(Callback, FindAndModify)}.
     * </p>
     * 
     * @see #findAndModifyAsync(Callback, FindAndModify)
     */
    @Override
    public Future<Document> findAndModifyAsync(final FindAndModify command)
            throws MongoDbException {
        final FutureCallback<Document> future = new FutureCallback<Document>();

        findAndModifyAsync(future, command);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAsync(Callback, Find)}.
     * </p>
     * 
     * @see #findAsync(Callback, DocumentAssignable)
     */
    @Override
    public void findAsync(final Callback<ClosableIterator<Document>> results,
            final DocumentAssignable query) throws MongoDbException {
        findAsync(results, new Find.Builder(query.asDocument()).build());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>find</code> method that implementations must
     * override.
     * </p>
     * 
     * @see MongoCollection#findAsync(Callback, Find)
     */
    @Override
    public abstract void findAsync(
            final Callback<ClosableIterator<Document>> results, final Find query)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAsync(Callback, DocumentAssignable)}.
     * </p>
     * 
     * @see #findAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<ClosableIterator<Document>> findAsync(
            final DocumentAssignable query) throws MongoDbException {
        final FutureCallback<ClosableIterator<Document>> future = new FutureCallback<ClosableIterator<Document>>();

        findAsync(future, query);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findAsync(Callback, Find)}.
     * </p>
     * 
     * @see #findAsync(Callback, Find)
     */
    @Override
    public Future<ClosableIterator<Document>> findAsync(final Find query)
            throws MongoDbException {
        final FutureCallback<ClosableIterator<Document>> future = new FutureCallback<ClosableIterator<Document>>();

        findAsync(future, query);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findOneAsync(DocumentAssignable)}.
     * </p>
     * 
     * @see #findOneAsync(DocumentAssignable)
     */
    @Override
    public Document findOne(final DocumentAssignable query)
            throws MongoDbException {
        return FutureUtils.unwrap(findOneAsync(query));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findOneAsync(Callback, Find)}.
     * </p>
     * 
     * @see #findOneAsync(Callback, Find)
     */
    @Override
    public void findOneAsync(Callback<Document> results,
            DocumentAssignable query) throws MongoDbException {
        findOneAsync(results, new Find.Builder(query).build());
    }

    /**
     * <p>
     * Overridden to call the {@link #findOneAsync(Callback, Find)}.
     * </p>
     * 
     * @see #findOneAsync(Callback, Find)
     */
    @Override
    public Document findOne(Find query) throws MongoDbException {
        return FutureUtils.unwrap(findOneAsync(query));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>findOne</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#findOneAsync(Callback, Find)
     */
    @Override
    public abstract void findOneAsync(Callback<Document> results, Find query)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #findOneAsync(Callback, Find)}.
     * </p>
     * 
     * @see #findOneAsync(Callback, Find)
     */
    @Override
    public Future<Document> findOneAsync(Find query) throws MongoDbException {
        final FutureCallback<Document> future = new FutureCallback<Document>();

        findOneAsync(future, query);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #findOneAsync(Callback, DocumentAssignable)}.
     * </p>
     * 
     * @see #findOneAsync(Callback, DocumentAssignable)
     */
    @Override
    public Future<Document> findOneAsync(final DocumentAssignable query)
            throws MongoDbException {
        final FutureCallback<Document> future = new FutureCallback<Document>();

        findOneAsync(future, query);

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
     * {@inheritDoc}
     */
    @Override
    public Durability getDurability() {
        Durability result = myDurability;
        if (result == null) {
            result = myDatabase.getDurability();
        }
        return result;
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
     */
    @Override
    public ReadPreference getReadPreference() {
        ReadPreference result = myReadPreference;
        if (result == null) {
            result = myDatabase.getReadPreference();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #groupByAsync(GroupBy)}.
     * </p>
     */
    @Override
    public ArrayElement groupBy(final GroupBy command) throws MongoDbException {
        return FutureUtils.unwrap(groupByAsync(command));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>groupBy</code> method that implementations
     * must override.
     * </p>
     */
    @Override
    public abstract void groupByAsync(Callback<ArrayElement> results,
            GroupBy command) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #groupByAsync(Callback, GroupBy)}.
     * </p>
     */
    @Override
    public Future<ArrayElement> groupByAsync(final GroupBy command)
            throws MongoDbException {
        final FutureCallback<ArrayElement> future = new FutureCallback<ArrayElement>();

        groupByAsync(future, command);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insert(boolean, Durability, DocumentAssignable...)} method with
     * the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #insert(boolean, Durability, DocumentAssignable[])
     */
    @Override
    public int insert(final boolean continueOnError,
            final DocumentAssignable... documents) throws MongoDbException {
        return insert(continueOnError, getDurability(), documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(boolean, Durability, DocumentAssignable...)} method.
     * </p>
     * 
     * @see #insertAsync(boolean, Durability, DocumentAssignable[])
     */
    @Override
    public int insert(final boolean continueOnError,
            final Durability durability, final DocumentAssignable... documents)
            throws MongoDbException {
        final Future<Integer> future = insertAsync(continueOnError, durability,
                documents);

        return FutureUtils.unwrap(future).intValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insert(boolean, Durability, DocumentAssignable...)} method with
     * <tt>continueOnError</tt> set to false and the {@link #getDurability()
     * default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public int insert(final DocumentAssignable... documents)
            throws MongoDbException {
        return insert(INSERT_CONTINUE_ON_ERROR_DEFAULT, getDurability(),
                documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insert(boolean, Durability, DocumentAssignable...)} method with
     * <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see #insert(boolean, Durability, DocumentAssignable[])
     */
    @Override
    public int insert(final Durability durability,
            final DocumentAssignable... documents) throws MongoDbException {
        return insert(INSERT_CONTINUE_ON_ERROR_DEFAULT, durability, documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public Future<Integer> insertAsync(final boolean continueOnError,
            final DocumentAssignable... documents) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, continueOnError, getDurability(), documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with <tt>continueOnError</tt> set to false and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public Future<Integer> insertAsync(final boolean continueOnError,
            final Durability durability, final DocumentAssignable... documents)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, continueOnError, durability, documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final boolean continueOnError,
            final DocumentAssignable... documents) throws MongoDbException {
        insertAsync(results, continueOnError, getDurability(), documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>insert</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public abstract void insertAsync(final Callback<Integer> results,
            final boolean continueOnError, final Durability durability,
            final DocumentAssignable... documents) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with <tt>continueOnError</tt> set to false and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final DocumentAssignable... documents) throws MongoDbException {
        insertAsync(results, INSERT_CONTINUE_ON_ERROR_DEFAULT, getDurability(),
                documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public void insertAsync(final Callback<Integer> results,
            final Durability durability, final DocumentAssignable... documents)
            throws MongoDbException {
        insertAsync(results, INSERT_CONTINUE_ON_ERROR_DEFAULT, durability,
                documents);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with <tt>continueOnError</tt> set to false and the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public Future<Integer> insertAsync(final DocumentAssignable... documents)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, INSERT_CONTINUE_ON_ERROR_DEFAULT, getDurability(),
                documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #insertAsync(Callback, boolean, Durability, DocumentAssignable...)}
     * method with <tt>continueOnError</tt> set to false.
     * </p>
     * 
     * @see MongoCollection#insertAsync(Callback, boolean, Durability,
     *      DocumentAssignable[])
     */
    @Override
    public Future<Integer> insertAsync(final Durability durability,
            final DocumentAssignable... documents) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        insertAsync(future, INSERT_CONTINUE_ON_ERROR_DEFAULT, durability,
                documents);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #mapReduceAsync(MapReduce)}.
     * </p>
     * 
     * @see #mapReduceAsync(MapReduce)
     */
    @Override
    public List<Document> mapReduce(final MapReduce command)
            throws MongoDbException {
        return FutureUtils.unwrap(mapReduceAsync(command));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>mapReduce</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#mapReduceAsync(Callback, MapReduce)
     */
    @Override
    public abstract void mapReduceAsync(Callback<List<Document>> results,
            MapReduce command) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #mapReduceAsync(Callback, MapReduce)}.
     * </p>
     * 
     * @see #mapReduceAsync(Callback, MapReduce)
     */
    @Override
    public Future<List<Document>> mapReduceAsync(final MapReduce command)
            throws MongoDbException {
        final FutureCallback<List<Document>> future = new FutureCallback<List<Document>>();

        mapReduceAsync(future, command);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #save(DocumentAssignable, Durability)}
     * using the {@link #getDurability() default durability}.
     * </p>
     */
    @Override
    public int save(final DocumentAssignable document) throws MongoDbException {
        return save(document, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the {@link #saveAsync(DocumentAssignable, Durability)}
     * .
     * </p>
     */
    @Override
    public int save(final DocumentAssignable document,
            final Durability durability) throws MongoDbException {
        return FutureUtils.unwrap(saveAsync(document, durability)).intValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #saveAsync(Callback, DocumentAssignable, Durability)} using the
     * {@link #getDurability() default durability}.
     * </p>
     */
    @Override
    public void saveAsync(final Callback<Integer> results,
            final DocumentAssignable document) throws MongoDbException {
        saveAsync(results, document, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>save</code> method that implementations must
     * override.
     * </p>
     */
    @Override
    public abstract void saveAsync(Callback<Integer> results,
            DocumentAssignable document, Durability durability)
            throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #saveAsync(Callback, DocumentAssignable, Durability)} using the
     * {@link #getDurability() default durability}.
     * </p>
     */
    @Override
    public Future<Integer> saveAsync(final DocumentAssignable document)
            throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        saveAsync(future, document, getDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #saveAsync(Callback, DocumentAssignable, Durability)}.
     * </p>
     */
    @Override
    public Future<Integer> saveAsync(final DocumentAssignable document,
            final Durability durability) throws MongoDbException {
        final FutureCallback<Integer> future = new FutureCallback<Integer>();

        saveAsync(future, document, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDurability(final Durability durability) {
        myDurability = durability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadPreference(final ReadPreference readPreference) {
        myReadPreference = readPreference;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #update(DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * method with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #update(DocumentAssignable, DocumentAssignable, boolean, boolean,
     *      Durability)
     */
    @Override
    public long update(final DocumentAssignable query,
            final DocumentAssignable update) throws MongoDbException {
        return update(query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #update(DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * method with the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #update(DocumentAssignable, DocumentAssignable, boolean, boolean,
     *      Durability)
     */
    @Override
    public long update(final DocumentAssignable query,
            final DocumentAssignable update, final boolean multiUpdate,
            final boolean upsert) throws MongoDbException {
        return update(query, update, multiUpdate, upsert, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * method.
     * </p>
     * 
     * @see #updateAsync(DocumentAssignable, DocumentAssignable, boolean,
     *      boolean, Durability)
     */
    @Override
    public long update(final DocumentAssignable query,
            final DocumentAssignable update, final boolean multiUpdate,
            final boolean upsert, final Durability durability)
            throws MongoDbException {

        final Future<Long> future = updateAsync(query, update, multiUpdate,
                upsert, durability);

        return FutureUtils.unwrap(future).longValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #update(DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * method with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #update(DocumentAssignable, DocumentAssignable, boolean, boolean,
     *      Durability)
     */
    @Override
    public long update(final DocumentAssignable query,
            final DocumentAssignable update, final Durability durability)
            throws MongoDbException {
        return update(query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public void updateAsync(final Callback<Long> results,
            final DocumentAssignable query, final DocumentAssignable update)
            throws MongoDbException {
        updateAsync(results, query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * using the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public void updateAsync(final Callback<Long> results,
            final DocumentAssignable query, final DocumentAssignable update,
            final boolean multiUpdate, final boolean upsert)
            throws MongoDbException {
        updateAsync(results, query, update, multiUpdate, upsert,
                getDurability());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the canonical <code>update</code> method that implementations
     * must override.
     * </p>
     * 
     * @see MongoCollection#updateAsync(Callback, DocumentAssignable,
     *      DocumentAssignable, boolean, boolean, Durability)
     */
    @Override
    public abstract void updateAsync(final Callback<Long> results,
            final DocumentAssignable query, final DocumentAssignable update,
            final boolean multiUpdate, final boolean upsert,
            final Durability durability) throws MongoDbException;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public void updateAsync(final Callback<Long> results,
            final DocumentAssignable query, final DocumentAssignable update,
            final Durability durability) throws MongoDbException {
        updateAsync(results, query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * with multiUpdate set to true, upsert set to false, and using the
     * {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public Future<Long> updateAsync(final DocumentAssignable query,
            final DocumentAssignable update) throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        updateAsync(future, query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, getDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * using the {@link #getDurability() default durability}.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public Future<Long> updateAsync(final DocumentAssignable query,
            final DocumentAssignable update, final boolean multiUpdate,
            final boolean upsert) throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        updateAsync(future, query, update, multiUpdate, upsert, getDurability());

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * method.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public Future<Long> updateAsync(final DocumentAssignable query,
            final DocumentAssignable update, final boolean multiUpdate,
            final boolean upsert, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        updateAsync(future, query, update, multiUpdate, upsert, durability);

        return future;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to call the
     * {@link #updateAsync(Callback, DocumentAssignable, DocumentAssignable, boolean, boolean, Durability)}
     * with multiUpdate set to true, and upsert set to false.
     * </p>
     * 
     * @see #updateAsync(Callback, DocumentAssignable, DocumentAssignable,
     *      boolean, boolean, Durability)
     */
    @Override
    public Future<Long> updateAsync(final DocumentAssignable query,
            final DocumentAssignable update, final Durability durability)
            throws MongoDbException {
        final FutureCallback<Long> future = new FutureCallback<Long>();

        updateAsync(future, query, update, UPDATE_MULTIUPDATE_DEFAULT,
                UPDATE_UPSERT_DEFAULT, durability);

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
        return new GetLastError(getDatabaseName(), durability);
    }

    /**
     * Generates a name for the index based on the keys.
     * 
     * @param keys
     *            The keys for the index.
     * @return The name for the index.
     */
    protected String buildIndexName(final Element... keys) {
        final StringBuilder nameBuilder = new StringBuilder();
        for (final Element key : keys) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append('_');
            }
            nameBuilder.append(key.getName().replace(' ', '_'));
            nameBuilder.append("_");
            if (key instanceof NumericElement) {
                nameBuilder.append(((NumericElement) key).getIntValue());
            } else if (key instanceof StringElement) {
                nameBuilder.append(((StringElement) key).getValue());
            }
        }
        return nameBuilder.toString();
    }
}