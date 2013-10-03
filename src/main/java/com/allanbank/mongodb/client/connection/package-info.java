/*
 * Copyright 2012-2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

/**
 * Provides interfaces for managing connections to different MongoDB server configurations.
 *
 * <h2>Usage</h2>
 * <p>
 * Users should not need to directly use the classes within this package.
 * </p>
 *
 * <h2>Design</h2>
 * <p> 
 * {@link com.allanbank.mongodb.client.connection.ConnectionFactory} instances are responsible for creating and
 * managing the state for a set of connections to MongoDB.  {@link com.allanbank.mongodb.client.connection.Connection}
 * provides the basic interface for sending and asynchronously receiving replies from the MongoDB servers.
 * Connection factories and connections are layered to achieve the desired interaction with the MongoDB servers.
 * </p>
 * <p>
 * The <a href="bootstrap/package-summary.html">com.allanbank.mongodb.client.connection.bootstrap</p> package
 * contains a <tt>ConnectionFactory</tt> that simply detects the type of MongoDB cluster being used and 
 * then delegates all future request to an appropriate delegate for the cluster type.
 * </p>
 * <p>
 * The <a href="auth/package-summary.html">com.allanbank.mongodb.client.connection.auth</p> package
 * contains a <tt>ConnectionFactory</tt> that wraps another connection factory and ensures 
 * all requests are properly authenticated prior to being sent to the server.
 * </p>
 * <p>
 * The <a href="rs/package-summary.html">com.allanbank.mongodb.client.connection.rs</p> package
 * that knows how to manage connections to a replica-set configuration.
 * </p>
 * <p>
 * The <a href="sharded/package-summary.html">com.allanbank.mongodb.client.connection.sharded</p> package
 * that knows how to manage connections to a sharded configuration.
 * </p>
 * <p>
 * The <a href="socket/package-summary.html">com.allanbank.mongodb.client.connection.socket</p> package
 * that creates a direct socket connection to the MongoDB server.
 * </p>
 * <p>
 * The <a href="proxy/package-summary.html">com.allanbank.mongodb.client.connection.proxy</p> package
 * contains base classes for <tt>ConnectionFactory</tt> and <tt>Connection</tt> implementations
 * that delegate to another implementation.
 * </p>
 * <p>
 * The <a href="state/package.html">com.allanbank.mongodb.client.connection.state</a> package contains 
 * support classes for state management.
 * </p>
 * 
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
package com.allanbank.mongodb.client.connection;