/*
 * Copyright 2011, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.allanbank.mongodb.connection;

import java.io.IOException;

import com.allanbank.mongodb.connection.bootstrap.BootstrapConnectionFactory;
import com.allanbank.mongodb.connection.socket.SocketConnection;

/**
 * Provides an abstraction for constructing a connection. At the lowest level a
 * connection to a MongoDB process is done through a {@link SocketConnection}
 * but there are several connection facades to inteligently connect to Replica
 * Sets and shard configurations.
 * <p>
 * The {@link BootstrapConnectionFactory} can be used to boot strap the
 * appropriate type of connection factory. It will use a single connections to a
 * MongoDB process to perform a series of commands to determine the server
 * configuration type (Sharded, Replica Set, Standalone) and the setup the
 * appropriate delegate connection factory.
 * </p>
 * 
 * @copyright 2011, Allanbank Consulting, Inc., All Rights Reserved
 */
public interface ConnectionFactory {
	/**
	 * Creates a connection to the address provided.
	 * 
	 * @return The Connection to MongoDB.
	 * @throws IOException
	 *             On a failure connecting to the server.
	 */
	public Connection connect() throws IOException;
}