/*
 * Copyright 2011, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.connection.bootstrap;

import static com.allanbank.mongodb.connection.CallbackReply.reply;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.allanbank.mongodb.MongoDbConfiguration;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.connection.ClusterType;
import com.allanbank.mongodb.connection.Connection;
import com.allanbank.mongodb.connection.ConnectionFactory;
import com.allanbank.mongodb.connection.MockMongoDBServer;
import com.allanbank.mongodb.connection.auth.AuthenticationConnectionFactory;
import com.allanbank.mongodb.connection.rs.ReplicaSetConnectionFactory;
import com.allanbank.mongodb.connection.rs.ReplicaSetReconnectStrategy;
import com.allanbank.mongodb.connection.sharded.ShardedConnectionFactory;
import com.allanbank.mongodb.connection.socket.SocketConnectionFactory;
import com.allanbank.mongodb.connection.state.SimpleReconnectStrategy;

/**
 * Integration test for the {@link BootstrapConnectionFactory}.
 * 
 * @copyright 2011, Allanbank Consulting, Inc., All Rights Reserved
 */
public class BootstrapConnectionFactoryTest {

    /** A test password. You really shouldn't use this as a password... */
    public static final String PASSWORD = "password";

    /** A test user name. */
    public static final String USER_NAME = "user";

    /** A Mock MongoDB server to connect to. */
    private static MockMongoDBServer ourServer;

    /**
     * Starts a Mock MongoDB server.
     * 
     * @throws IOException
     *             On a failure to start the Mock MongoDB server.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        ourServer = new MockMongoDBServer();
        ourServer.start();
    }

    /**
     * Stops a Mock MongoDB server.
     * 
     * @throws IOException
     *             On a failure to stop the Mock MongoDB server.
     */
    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        ourServer.setRunning(false);
        ourServer.close();
        ourServer = null;
    }

    /** The factory being tested. */
    private BootstrapConnectionFactory myTestFactory;

    /**
     * Cleans up the test connection.
     * 
     * @throws IOException
     *             On a failure to shutdown the test connection.
     */
    @After
    public void tearDown() throws IOException {
        myTestFactory.close();
        myTestFactory = null;
        ourServer.clear();
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapAllFail() {
        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.addString("process", "mongod");

        ourServer.setReplies(reply());

        final MongoDbConfiguration config = new MongoDbConfiguration(
                ourServer.getInetSocketAddress());
        config.setAutoDiscoverServers(false);

        myTestFactory = new BootstrapConnectionFactory(config);

        assertNull("Wrong type of myTestFactory.", myTestFactory.getDelegate());
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapReplicaSet() {
        final String serverName = ourServer.getInetSocketAddress()
                .getHostName()
                + ":"
                + ourServer.getInetSocketAddress().getPort();

        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.push("repl");
        replStatusBuilder.addString("primary", serverName);
        replStatusBuilder.pushArray("hosts").addString(serverName);

        ourServer.setReplies(reply(replStatusBuilder),
                reply(replStatusBuilder), reply(replStatusBuilder), reply());

        final MongoDbConfiguration config = new MongoDbConfiguration(
                ourServer.getInetSocketAddress());
        myTestFactory = new BootstrapConnectionFactory(config);

        assertThat("Wrong type of myTestFactory.", myTestFactory.getDelegate(),
                instanceOf(ReplicaSetConnectionFactory.class));
        assertThat(myTestFactory.getReconnectStrategy(),
                instanceOf(ReplicaSetReconnectStrategy.class));
        assertEquals(ClusterType.REPLICA_SET, myTestFactory.getClusterType());
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapSharded() {

        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.addString("process", "mongos");

        ourServer.setReplies(reply(replStatusBuilder), reply());

        final MongoDbConfiguration config = new MongoDbConfiguration(
                ourServer.getInetSocketAddress());
        config.setAutoDiscoverServers(false);

        myTestFactory = new BootstrapConnectionFactory(config);

        assertThat("Wrong type of myTestFactory.", myTestFactory.getDelegate(),
                instanceOf(ShardedConnectionFactory.class));
        assertThat(myTestFactory.getReconnectStrategy(),
                instanceOf(SimpleReconnectStrategy.class));
        assertEquals(ClusterType.SHARDED, myTestFactory.getClusterType());
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapStandalone() {
        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.addString("process", "mongod");

        ourServer.setReplies(reply(replStatusBuilder));

        final MongoDbConfiguration config = new MongoDbConfiguration(
                ourServer.getInetSocketAddress());
        config.setAutoDiscoverServers(false);

        myTestFactory = new BootstrapConnectionFactory(config);

        assertThat("Wrong type of myTestFactory.", myTestFactory.getDelegate(),
                instanceOf(SocketConnectionFactory.class));
        assertThat(myTestFactory.getReconnectStrategy(),
                instanceOf(SimpleReconnectStrategy.class));
        assertEquals(ClusterType.STAND_ALONE, myTestFactory.getClusterType());
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapStandaloneFirstFails() {
        final InetSocketAddress fails = new InetSocketAddress(ourServer
                .getInetSocketAddress().getAddress(), ourServer
                .getInetSocketAddress().getPort() + 1);

        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.addString("process", "mongod");

        ourServer.setReplies(reply(replStatusBuilder));

        final MongoDbConfiguration config = new MongoDbConfiguration(fails,
                ourServer.getInetSocketAddress());
        config.setAutoDiscoverServers(false);

        myTestFactory = new BootstrapConnectionFactory(config);

        assertThat("Wrong type of myTestFactory.", myTestFactory.getDelegate(),
                CoreMatchers.instanceOf(SocketConnectionFactory.class));
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#bootstrap()} .
     */
    @Test
    public void testBootstrapStandaloneWithAuth() {
        final DocumentBuilder nonceReply = BuilderFactory.start();
        nonceReply.addString("nonce", "deadbeef4bee");

        final DocumentBuilder authReply = BuilderFactory.start();
        authReply.addInteger("ok", 1);

        final DocumentBuilder replStatusBuilder = BuilderFactory.start();
        replStatusBuilder.addString("process", "mongod");

        ourServer.setReplies(reply(nonceReply), reply(authReply),
                reply(replStatusBuilder));

        final MongoDbConfiguration config = new MongoDbConfiguration(
                ourServer.getInetSocketAddress());
        config.authenticate(USER_NAME, PASSWORD);

        myTestFactory = new BootstrapConnectionFactory(config);

        assertThat("Wrong type of myTestFactory.", myTestFactory.getDelegate(),
                CoreMatchers.instanceOf(AuthenticationConnectionFactory.class));
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#close()} .
     * 
     * @throws IOException
     *             On a failure connecting to the Mock MongoDB server.
     */
    @Test
    public void testClose() throws IOException {
        final MongoDbConfiguration config = new MongoDbConfiguration(
                new InetSocketAddress("127.0.0.1", 27017));
        myTestFactory = new BootstrapConnectionFactory(config);

        final ConnectionFactory mockFactory = createMock(ConnectionFactory.class);

        mockFactory.close();
        expectLastCall();

        replay(mockFactory);

        myTestFactory.setDelegate(mockFactory);
        myTestFactory.close();

        verify(mockFactory);

        // Reset the mock for a close in tearDown.
        reset(mockFactory);
    }

    /**
     * Test method for {@link BootstrapConnectionFactory#connect()} .
     */
    @Test
    public void testConnect() {
        try {
            final MongoDbConfiguration config = new MongoDbConfiguration(
                    new InetSocketAddress("127.0.0.1", 27017));
            myTestFactory = new BootstrapConnectionFactory(config);

            final Connection mockConnection = createMock(Connection.class);
            final ConnectionFactory mockFactory = createMock(ConnectionFactory.class);

            expect(mockFactory.connect()).andReturn(mockConnection);

            replay(mockConnection, mockFactory);

            myTestFactory.setDelegate(mockFactory);
            assertSame(mockConnection, myTestFactory.connect());

            verify(mockConnection, mockFactory);

            // Reset the mock for a close in tearDown.
            reset(mockFactory);
        }
        catch (final IOException ioe) {
            final AssertionError error = new AssertionError(ioe.getMessage());
            error.initCause(ioe);
            throw error;
        }
    }

}
