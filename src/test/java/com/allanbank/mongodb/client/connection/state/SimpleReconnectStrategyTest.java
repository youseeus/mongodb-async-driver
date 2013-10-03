/*
 * Copyright 2012-2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.client.connection.state;

import static com.allanbank.mongodb.client.connection.CallbackReply.cb;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoDbException;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.impl.ImmutableDocument;
import com.allanbank.mongodb.client.connection.Connection;
import com.allanbank.mongodb.client.connection.message.IsMaster;
import com.allanbank.mongodb.client.connection.message.ServerStatus;
import com.allanbank.mongodb.client.connection.proxy.ProxiedConnectionFactory;

/**
 * SimpleReconnectStrategyTest provides tests for the
 * {@link SimpleReconnectStrategy} class.
 * 
 * @copyright 2012-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class SimpleReconnectStrategyTest {

    /** Update document to mark servers as the primary. */
    private static final Document PRIMARY_UPDATE = new ImmutableDocument(
            BuilderFactory.start().add("ismaster", true));

    /** The address for the test. */
    private String myAddress = null;

    /**
     * Creates the basic test objects.
     */
    @Before
    public void setUp() {
        myAddress = "localhost:27017";
    }

    /**
     * Cleans up the test.
     */
    @After
    public void tearDown() {
        myAddress = null;
    }

    /**
     * Test method for {@link SimpleReconnectStrategy#reconnect(Connection)}.
     * 
     * @throws IOException
     *             On a Failure setting up the mock configuration for the test.
     * @throws InterruptedException
     *             On a Failure setting up the mock configuration for the test.
     */
    @Test
    public void testReconnect() throws IOException, InterruptedException {
        final MongoClientConfiguration config = new MongoClientConfiguration();
        final Cluster cluster = new Cluster(config);
        final Server server = cluster.add("foo:27017");

        final Connection mockOldConnection = createMock(Connection.class);
        final Connection mockNewConnection = createMock(Connection.class);
        final ProxiedConnectionFactory mockFactory = createMock(ProxiedConnectionFactory.class);
        final ServerSelector mockSelector = createMock(ServerSelector.class);

        expect(mockOldConnection.getServerName()).andReturn("foo:27017");
        expect(mockFactory.connect(server, config)).andThrow(
                new IOException("Inject"));

        expect(mockSelector.pickServers()).andReturn(
                Collections.singletonList(server));

        expect(mockFactory.connect(server, config))
                .andReturn(mockNewConnection);

        expect(
                mockNewConnection.send(anyObject(IsMaster.class),
                        cb(BuilderFactory.start(PRIMARY_UPDATE)))).andReturn(
                myAddress);

        replay(mockOldConnection, mockNewConnection, mockFactory, mockSelector);

        final SimpleReconnectStrategy strategy = new SimpleReconnectStrategy();

        strategy.setConnectionFactory(mockFactory);
        strategy.setConfig(config);
        strategy.setSelector(mockSelector);
        strategy.setState(cluster);

        assertSame(mockNewConnection, strategy.reconnect(mockOldConnection));

        verify(mockOldConnection, mockNewConnection, mockFactory, mockSelector);
    }

    /**
     * Test method for {@link SimpleReconnectStrategy#reconnect(Connection)}.
     * 
     * @throws IOException
     *             On a Failure setting up the mock configuration for the test.
     * @throws InterruptedException
     *             On a Failure setting up the mock configuration for the test.
     */
    @Test
    public void testReconnectBackWorks() throws IOException,
            InterruptedException {
        final MongoClientConfiguration config = new MongoClientConfiguration();
        final Cluster cluster = new Cluster(config);
        final Server server = cluster.add("foo:27017");

        final Connection mockOldConnection = createMock(Connection.class);
        final Connection mockNewConnection = createMock(Connection.class);
        final ProxiedConnectionFactory mockFactory = createMock(ProxiedConnectionFactory.class);
        final ServerSelector mockSelector = createMock(ServerSelector.class);

        expect(mockOldConnection.getServerName()).andReturn("foo:27017");
        expect(mockFactory.connect(server, config))
                .andReturn(mockNewConnection);
        expect(
                mockNewConnection.send(anyObject(IsMaster.class),
                        cb(BuilderFactory.start(PRIMARY_UPDATE)))).andReturn(
                myAddress);

        replay(mockOldConnection, mockNewConnection, mockFactory, mockSelector);

        final SimpleReconnectStrategy strategy = new SimpleReconnectStrategy();

        strategy.setConnectionFactory(mockFactory);
        strategy.setConfig(config);
        strategy.setSelector(mockSelector);
        strategy.setState(cluster);

        assertSame(mockNewConnection, strategy.reconnect(mockOldConnection));

        verify(mockOldConnection, mockNewConnection, mockFactory, mockSelector);
    }

    /**
     * Test method for {@link SimpleReconnectStrategy#reconnect(Connection)}.
     * 
     * @throws IOException
     *             On a failure setting up the mocks.
     */
    @Test
    public void testReconnectFails() throws IOException {
        final MongoClientConfiguration config = new MongoClientConfiguration();
        final Cluster cluster = new Cluster(config);
        final Server server = cluster.add("foo:27017");

        final Connection mockOldConnection = createMock(Connection.class);
        final Connection mockNewConnection = createMock(Connection.class);
        final ProxiedConnectionFactory mockFactory = createMock(ProxiedConnectionFactory.class);
        final ServerSelector mockSelector = createMock(ServerSelector.class);

        expect(mockOldConnection.getServerName()).andReturn("foo:27017");
        expect(mockFactory.connect(server, config)).andThrow(
                new IOException("Inject"));

        expect(mockSelector.pickServers()).andReturn(new ArrayList<Server>());

        replay(mockOldConnection, mockNewConnection, mockFactory, mockSelector);

        final SimpleReconnectStrategy strategy = new SimpleReconnectStrategy();

        strategy.setConnectionFactory(mockFactory);
        strategy.setConfig(config);
        strategy.setSelector(mockSelector);
        strategy.setState(cluster);

        assertNull(strategy.reconnect(mockOldConnection));

        verify(mockOldConnection, mockNewConnection, mockFactory, mockSelector);
    }

    /**
     * Test method for {@link SimpleReconnectStrategy#reconnect(Connection)}.
     * 
     * @throws IOException
     *             On a Failure setting up the mock configuration for the test.
     * @throws InterruptedException
     *             On a Failure setting up the mock configuration for the test.
     */
    @Test
    public void testReconnectFirstFails() throws IOException,
            InterruptedException {
        final MongoClientConfiguration config = new MongoClientConfiguration();
        final Cluster cluster = new Cluster(config);
        final Server server = cluster.add(new InetSocketAddress("foo", 27017));

        final Connection mockOldConnection = createMock(Connection.class);
        final Connection mockNewConnection = createMock(Connection.class);
        final ProxiedConnectionFactory mockFactory = createMock(ProxiedConnectionFactory.class);
        final ServerSelector mockSelector = createMock(ServerSelector.class);

        expect(mockOldConnection.getServerName()).andReturn("foo:27017");

        expect(mockFactory.connect(server, config)).andThrow(
                new IOException("Inject"));

        expect(mockSelector.pickServers()).andReturn(
                Arrays.asList(server, server));

        expect(mockFactory.connect(server, config)).andThrow(new IOException());
        expect(mockFactory.connect(server, config))
                .andReturn(mockNewConnection);

        expect(
                mockNewConnection.send(anyObject(ServerStatus.class),
                        cb(BuilderFactory.start(PRIMARY_UPDATE)))).andReturn(
                myAddress);

        replay(mockOldConnection, mockNewConnection, mockFactory, mockSelector);

        final SimpleReconnectStrategy strategy = new SimpleReconnectStrategy();

        strategy.setConnectionFactory(mockFactory);
        strategy.setConfig(config);
        strategy.setSelector(mockSelector);
        strategy.setState(cluster);

        assertSame(cluster, strategy.getState());
        assertSame(mockSelector, strategy.getSelector());
        assertSame(config, strategy.getConfig());
        assertSame(mockFactory, strategy.getConnectionFactory());

        assertSame(mockNewConnection, strategy.reconnect(mockOldConnection));

        verify(mockOldConnection, mockNewConnection, mockFactory, mockSelector);
    }

    /**
     * Test method for {@link SimpleReconnectStrategy#reconnect(Connection)}.
     * 
     * @throws IOException
     *             On a Failure setting up the mock configuration for the test.
     */
    @Test
    public void testReconnectPingFails() throws IOException {
        final MongoClientConfiguration config = new MongoClientConfiguration();
        final Cluster cluster = new Cluster(config);
        final Server server = cluster.add(new InetSocketAddress("foo", 27017));

        final Connection mockOldConnection = createMock(Connection.class);
        final Connection mockNewConnection = createMock(Connection.class);
        final ProxiedConnectionFactory mockFactory = createMock(ProxiedConnectionFactory.class);
        final ServerSelector mockSelector = createMock(ServerSelector.class);

        expect(mockOldConnection.getServerName()).andReturn("foo:27017");
        expect(mockFactory.connect(server, config)).andThrow(
                new IOException("Inject"));

        expect(mockSelector.pickServers()).andReturn(
                Collections.singletonList(server));
        expect(mockFactory.connect(server, config))
                .andReturn(mockNewConnection);
        expect(
                mockNewConnection.send(anyObject(ServerStatus.class),
                        cb(new MongoDbException("Injected")))).andReturn(
                myAddress);

        mockNewConnection.close();

        replay(mockOldConnection, mockNewConnection, mockFactory, mockSelector);

        final SimpleReconnectStrategy strategy = new SimpleReconnectStrategy();

        strategy.setConnectionFactory(mockFactory);
        strategy.setConfig(config);
        strategy.setSelector(mockSelector);
        strategy.setState(cluster);

        assertNull(strategy.reconnect(mockOldConnection));

        verify(mockOldConnection, mockNewConnection, mockFactory, mockSelector);
    }
}