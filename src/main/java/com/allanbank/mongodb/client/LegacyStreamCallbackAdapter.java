/*
 * Copyright 2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.StreamCallback;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.builder.Find;

/**
 * LegacyStreamCallbackAdapter provides an adapter from a {@link Callback} to a
 * {@link StreamCallback}. This adapter simulates the old behaviour specified in
 * the {@link MongoCollection#streamingFind(Callback, DocumentAssignable)} and
 * {@link MongoCollection#streamingFind(Callback, Find)} using the new
 * interface.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @deprecated Deprecated to ensure this class is removed with the
 *             {@link MongoCollection#streamingFind(Callback, DocumentAssignable)}
 *             and {@link MongoCollection#streamingFind(Callback, Find)}
 *             methods.
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@Deprecated
final class LegacyStreamCallbackAdapter implements StreamCallback<Document> {

    /** The legacy callback to delegate to. */
    private final Callback<Document> myDelegate;

    /**
     * Creates a new LegacyStreamCallbackAdapter.
     * 
     * @param delegate
     *            The legacy callback to delegate to.
     */
    public LegacyStreamCallbackAdapter(final Callback<Document> delegate) {
        myDelegate = delegate;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward to the legacy {@link Callback#callback}.
     * </p>
     */
    @Override
    public void callback(final Document result) {
        myDelegate.callback(result);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward to {@link Callback#callback callback(null)}.
     * </p>
     */
    @Override
    public void done() {
        myDelegate.callback(null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to forward to the legacy {@link Callback#exception}.
     * </p>
     */
    @Override
    public void exception(final Throwable thrown) {
        myDelegate.exception(thrown);
    }
}