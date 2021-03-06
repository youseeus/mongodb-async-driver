/*
 * #%L
 * MongoDatabaseImplTest.java - mongodb-async-driver - Allanbank Consulting, Inc.
 * %%
 * Copyright (C) 2011 - 2014 Allanbank Consulting, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package com.allanbank.mongodb.client;

import static com.allanbank.mongodb.AnswerCallback.callback;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.allanbank.mongodb.Callback;
import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.ProfilingStatus;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.client.callback.SingleDocumentReplyCallback;
import com.allanbank.mongodb.client.message.Command;
import com.allanbank.mongodb.client.message.Query;
import com.allanbank.mongodb.client.message.Reply;
import com.allanbank.mongodb.util.IOUtils;

/**
 * MongoDatabaseImplTest provides tests for the {@link MongoDatabaseImpl} class.
 * 
 * @copyright 2012-2014, Allanbank Consulting, Inc., All Rights Reserved
 */
@SuppressWarnings("unchecked")
public class MongoDatabaseImplTest {

    /** The client the collection interacts with. */
    private Client myMockClient = null;

    /** The client the collection interacts with. */
    private MongoClient myMockMongoClient = null;

    /** The instance under test. */
    private MongoDatabaseImpl myTestInstance = null;

    /**
     * Creates the base set of objects for the test.
     */
    @Before
    public void setUp() {
        myMockMongoClient = EasyMock.createMock(MongoClient.class);
        myMockClient = EasyMock.createMock(Client.class);

        myTestInstance = new MongoDatabaseImpl(myMockMongoClient, myMockClient,
                "test");

        expect(myMockClient.getConfig()).andReturn(
                new MongoClientConfiguration()).anyTimes();
    }

    /**
     * Cleans up the base set of objects for the test.
     */
    @After
    public void tearDown() {
        myMockClient = null;

        myTestInstance = null;
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#createCappedCollection(String, long)}.
     */
    @Test
    public void testCreateCappedCollection() {
        final Document goodResult = BuilderFactory.start().addDouble("ok", 1.0)
                .build();
        final Document badResult = BuilderFactory.start().addLong("ok", 0)
                .build();
        final Document missingOkResult = BuilderFactory.start().build();

        final Command command = new Command("test", Command.COMMAND_COLLECTION,
                BuilderFactory.start().add("create", "f").add("capped", true)
                        .add("size", 10000L).build());

        myMockClient.send(eq(command), callback(reply(goodResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(missingOkResult)));
        expectLastCall();

        replay();

        assertTrue(myTestInstance.createCappedCollection("f", 10000L));
        assertFalse(myTestInstance.createCappedCollection("f", 10000L));
        assertFalse(myTestInstance.createCappedCollection("f", 10000L));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#createCollection(String, DocumentAssignable)}.
     */
    @Test
    public void testCreateCollection() {
        final Document goodResult = BuilderFactory.start().addDouble("ok", 1.0)
                .build();
        final Document badResult = BuilderFactory.start().addLong("ok", 0)
                .build();
        final Document missingOkResult = BuilderFactory.start().build();

        final Command command = new Command("test", Command.COMMAND_COLLECTION,
                BuilderFactory.start().add("create", "f").build());

        myMockClient.send(eq(command), callback(reply(goodResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(missingOkResult)));
        expectLastCall();

        replay();

        assertTrue(myTestInstance.createCollection("f", null));
        assertFalse(myTestInstance.createCollection("f", null));
        assertFalse(myTestInstance.createCollection("f", null));

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#drop()}.
     */
    @Test
    public void testDrop() {
        final Document goodResult = BuilderFactory.start().addDouble("ok", 1.0)
                .build();
        final Document badResult = BuilderFactory.start().addLong("ok", 0)
                .build();
        final Document missingOkResult = BuilderFactory.start().build();

        final Command command = new Command("test", Command.COMMAND_COLLECTION,
                BuilderFactory.start().addInteger("dropDatabase", 1).build());

        myMockClient.send(eq(command), callback(reply(goodResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(missingOkResult)));
        expectLastCall();

        replay();

        assertTrue(myTestInstance.drop());
        assertFalse(myTestInstance.drop());
        assertFalse(myTestInstance.drop());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#exists()}.
     */
    @Test
    public void testExists() {

        expect(myMockMongoClient.listDatabaseNames()).andReturn(
                Collections.singletonList("test"));

        replay();

        assertThat(myTestInstance.exists(), is(true));

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getCollection(String)}.
     */
    @Test
    public void testGetCollection() {
        final MongoCollection collection = myTestInstance.getCollection("foo");
        assertTrue(collection instanceof SynchronousMongoCollectionImpl);
        assertSame(myTestInstance,
                ((AbstractAsyncMongoCollection) collection).myDatabase);
        assertSame(myMockClient,
                ((AbstractAsyncMongoCollection) collection).myClient);
        assertEquals("foo", collection.getName());
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getCollection(String)} .
     */
    @Test
    public void testGetDatabaseCachingDoesRelease() {
        MongoCollection collection = myTestInstance.getCollection("foo");
        assertThat(myTestInstance.getCollection("foo"),
                Matchers.sameInstance(collection));

        // Remember the instance id.
        final int instanceId = System.identityHashCode(collection);

        collection = null;

        // Flood the map/memory.
        final Random rand = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[1024];
        for (int i = 0; i < 10000000; ++i) {

            bytes = new byte[bytes.length + 1024];
            rand.nextBytes(bytes);

            final String name = IOUtils.toBase64(bytes);
            myTestInstance.getCollection(name);

            if (((i % 10) == 0)
                    && (instanceId != System.identityHashCode(myTestInstance
                            .getCollection("foo")))) {
                // Woot - got garbage collected.
                break;
            }

            // Try and nudge things along.
            System.gc();
        }

        collection = myTestInstance.getCollection("foo");
        assertThat(System.identityHashCode(collection), not(is(instanceId)));
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getCollection(String)} .
     */
    @Test
    public void testGetDatabaseCachingDoesReleaseMaybeSeenViaGet() {
        MongoCollection collection = myTestInstance.getCollection("foo");
        assertThat(myTestInstance.getCollection("foo"),
                Matchers.sameInstance(collection));

        // Remember the instance id.
        final int instanceId = System.identityHashCode(collection);

        collection = null;

        // Flood the map/memory.
        final Random rand = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[1024];
        for (int i = 0; i < 10000000; ++i) {

            bytes = new byte[bytes.length + 1024];
            rand.nextBytes(bytes);

            final String name = IOUtils.toBase64(bytes);
            myTestInstance.getCollection(name);

            // Check ever time for the removed instance.
            if (instanceId != System.identityHashCode(myTestInstance
                    .getCollection("foo"))) {
                // Woot - got garbage collected.
                break;
            }

            // Try and nudge things along.
            System.gc();
        }

        collection = myTestInstance.getCollection("foo");
        assertThat(System.identityHashCode(collection), not(is(instanceId)));
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getDurability()}.
     */
    @Test
    public void testGetDurabilityFromClient() {
        final Durability defaultDurability = Durability.journalDurable(1234);

        expect(myMockClient.getDefaultDurability())
                .andReturn(defaultDurability);

        replay();

        final Durability result = myTestInstance.getDurability();
        assertSame(defaultDurability, result);

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getDurability()}.
     */
    @Test
    public void testGetDurabilitySet() {
        final Durability defaultDurability = Durability.journalDurable(1234);
        final Durability setDurability = Durability.journalDurable(4321);

        expect(myMockClient.getDefaultDurability())
                .andReturn(defaultDurability);

        replay();

        myTestInstance.setDurability(setDurability);
        assertSame(setDurability, myTestInstance.getDurability());

        myTestInstance.setDurability(null); // Now back to client.
        assertSame(defaultDurability, myTestInstance.getDurability());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getProfilingStatus()}.
     */
    @Test
    public void testGetProfilingLevel() {
        final Document offResult = BuilderFactory.start().add("was", 0)
                .add("slowms", 100).build();
        final Document slowResult = BuilderFactory.start().add("was", 1)
                .add("slowms", 100).build();
        final Document allResult = BuilderFactory.start().add("was", 2)
                .add("slowms", 100).build();
        final Document badResult1 = BuilderFactory.start().add("huh", 0)
                .add("slowms", 100).build();
        final Document badResult2 = BuilderFactory.start().add("was", 0)
                .add("oops", 100).build();
        final Document badResult3 = BuilderFactory.start().add("was", 4)
                .add("slowms", 100).build();

        final Command command = new Command("test", Command.COMMAND_COLLECTION,
                BuilderFactory.start().add("profile", -1).build());

        myMockClient.send(eq(command), callback(reply(offResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(slowResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(allResult)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult1)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult2)));
        expectLastCall();
        myMockClient.send(eq(command), callback(reply(badResult3)));
        expectLastCall();

        replay();

        assertEquals(ProfilingStatus.OFF, myTestInstance.getProfilingStatus());
        assertEquals(ProfilingStatus.slow(100),
                myTestInstance.getProfilingStatus());
        assertEquals(ProfilingStatus.ON, myTestInstance.getProfilingStatus());

        assertNull(myTestInstance.getProfilingStatus());
        assertNull(myTestInstance.getProfilingStatus());
        assertNull(myTestInstance.getProfilingStatus());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getReadPreference()}.
     */
    @Test
    public void testGetReadPreferenceFromClient() {
        final ReadPreference defaultReadPreference = ReadPreference
                .preferSecondary();

        expect(myMockClient.getDefaultReadPreference()).andReturn(
                defaultReadPreference);

        replay();

        final ReadPreference result = myTestInstance.getReadPreference();
        assertSame(defaultReadPreference, result);

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#getReadPreference()}.
     */
    @Test
    public void testGetReadPreferenceSet() {
        final ReadPreference defaultReadPreference = ReadPreference
                .preferSecondary();
        final ReadPreference setReadPreference = ReadPreference.secondary();

        expect(myMockClient.getDefaultReadPreference()).andReturn(
                defaultReadPreference);

        replay();

        myTestInstance.setReadPreference(setReadPreference);
        assertSame(setReadPreference, myTestInstance.getReadPreference());

        myTestInstance.setReadPreference(null); // Now back to client.
        assertSame(defaultReadPreference, myTestInstance.getReadPreference());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#listCollectionNames()}.
     */
    @Test
    public void testListCollectionNames() {

        final Document result1 = BuilderFactory.start()
                .addString("name", "test.collection").build();
        final Document result2 = BuilderFactory.start()
                .addString("name", "test.1.oplog.$").build();

        final Query query = new Query("test", "system.namespaces",
                BuilderFactory.start().build(), null, 0, 0, 0, false,
                ReadPreference.PRIMARY, false, false, false, false);

        myMockClient.send(eq(query), callback(reply(result1, result2)));
        expectLastCall();

        replay();

        assertEquals(Arrays.asList("collection", "1.oplog.$"),
                myTestInstance.listCollectionNames());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#listCollections()}.
     */
    @Test
    @Deprecated
    public void testListCollections() {

        final Document result1 = BuilderFactory.start()
                .addString("name", "test.collection").build();
        final Document result2 = BuilderFactory.start()
                .addString("name", "test.1.oplog.$").build();

        final Query query = new Query("test", "system.namespaces",
                BuilderFactory.start().build(), null, 0, 0, 0, false,
                ReadPreference.PRIMARY, false, false, false, false);

        myMockClient.send(eq(query), callback(reply(result1, result2)));
        expectLastCall();

        replay();

        assertEquals(Arrays.asList("collection", "1.oplog.$"),
                myTestInstance.listCollections());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#runAdminCommand(String)}.
     */
    @Test
    public void testRunAdminCommandString() {
        myTestInstance = new MongoDatabaseImpl(myMockMongoClient, myMockClient,
                "admin");

        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("admin",
                Command.COMMAND_COLLECTION, commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runAdminCommand("command"));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runAdminCommand(String, DocumentAssignable)}.
     */
    @Test
    public void testRunAdminCommandStringDocument() {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("admin",
                Command.COMMAND_COLLECTION, commandDoc.build());

        expect(myMockMongoClient.getDatabase("admin"))
                .andReturn(
                        new MongoDatabaseImpl(myMockMongoClient, myMockClient,
                                "admin"));

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();
        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply,
                myTestInstance.runAdminCommand("command", options.build()));
        assertSame(reply,
                myTestInstance.runAdminCommand("command", options.build()));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runAdminCommand(String, String, DocumentAssignable)}
     * .
     */
    @Test
    public void testRunAdminCommandStringStringDocument() {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addString("command", "name");

        final Command message = new Command("admin",
                Command.COMMAND_COLLECTION, commandDoc.build());

        expect(myMockMongoClient.getDatabase("admin"))
                .andReturn(
                        new MongoDatabaseImpl(myMockMongoClient, myMockClient,
                                "admin"));
        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply,
                myTestInstance.runAdminCommand("command", "name", null));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(Callback, String)}.
     */
    @Test
    public void testRunCommandAsyncCallbackOfDocumentString() {
        final Callback<Document> mockCallback = createMock(Callback.class);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message),
                anyObject(SingleDocumentReplyCallback.class));
        expectLastCall();

        replay(mockCallback);

        myTestInstance.runCommandAsync(mockCallback, "command");

        verify(mockCallback);
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(Callback, String, DocumentAssignable)}
     * .
     */
    @Test
    public void testRunCommandAsyncCallbackOfDocumentStringDocument() {

        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);
        options.addString("command", "1");

        final Callback<Document> mockCallback = createMock(Callback.class);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message),
                anyObject(SingleDocumentReplyCallback.class));
        expectLastCall();

        replay(mockCallback);

        myTestInstance
                .runCommandAsync(mockCallback, "command", options.build());

        verify(mockCallback);
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(Callback, String, String, DocumentAssignable)}
     * .
     */
    @Test
    public void testRunCommandAsyncCallbackOfDocumentStringStringDocument() {
        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);
        options.addInteger("command", 1);

        final Callback<Document> mockCallback = createMock(Callback.class);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addString("command", "name");
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message),
                anyObject(SingleDocumentReplyCallback.class));
        expectLastCall();

        replay(mockCallback);

        myTestInstance.runCommandAsync(mockCallback, "command", "name",
                options.build());

        verify(mockCallback);
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(DocumentAssignable)}.
     * 
     * @throws Exception
     *             On a failure.
     */
    @Test
    public void testRunCommandAsyncDocumentAssignable() throws Exception {

        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runCommandAsync(commandDoc).get());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#runCommandAsync(String)}.
     * 
     * @throws Exception
     *             On a failure.
     */
    @Test
    public void testRunCommandAsyncString() throws Exception {

        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runCommandAsync("command").get());

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(String, DocumentAssignable)}.
     * 
     * @throws Exception
     *             On a failure.
     */
    @Test
    public void testRunCommandAsyncStringDocument() throws Exception {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply,
                myTestInstance.runCommandAsync("command", options.build())
                        .get());

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommandAsync(String, String, DocumentAssignable)}
     * .
     * 
     * @throws Exception
     *             On a failure.
     */
    @Test
    public void testRunCommandAsyncStringStringDocument() throws Exception {
        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);

        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addString("command", "name");
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(
                reply,
                myTestInstance.runCommandAsync("command", "name",
                        options.build()).get());

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#runCommand(DocumentAssignable)}.
     */
    @Test
    public void testRunCommandDocumentAssignable() {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runCommand(commandDoc));

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#runCommand(String)}.
     */
    @Test
    public void testRunCommandString() {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runCommand("command"));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommand(String, DocumentAssignable)}.
     */
    @Test
    public void testRunCommandStringDocument() {
        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addInteger("command", 1);
        commandDoc.addBoolean("option1", true);

        final Command message = new Command(myTestInstance.getName(),
                Command.COMMAND_COLLECTION, commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply, myTestInstance.runCommand("command", options.build()));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#runCommand(String, String, DocumentAssignable)}.
     */
    @Test
    public void testRunCommandStringStringDocument() {
        final DocumentBuilder options = BuilderFactory.start();
        options.addBoolean("option1", true);

        final Document reply = BuilderFactory.start().build();

        final DocumentBuilder commandDoc = BuilderFactory.start();
        commandDoc.addString("command", "name");
        commandDoc.addBoolean("option1", true);

        final Command message = new Command("test", Command.COMMAND_COLLECTION,
                commandDoc.build());

        myMockClient.send(eq(message), callback(reply(reply)));
        expectLastCall();

        replay();

        assertSame(reply,
                myTestInstance.runCommand("command", "name", options.build()));

        verify();
    }

    /**
     * Test method for
     * {@link MongoDatabaseImpl#setProfilingStatus(ProfilingStatus)}.
     */
    @Test
    public void testSetProfilingLevel() {
        final Document offResult = BuilderFactory.start().add("was", 0)
                .add("slowms", 100).build();
        final Document slowResult = BuilderFactory.start().add("was", 1)
                .add("slowms", 100).build();
        final Document allResult = BuilderFactory.start().add("was", 2)
                .add("slowms", 100).build();
        final Document badResult1 = BuilderFactory.start().add("huh", 0)
                .add("slowms", 100).build();
        final Document badResult2 = BuilderFactory.start().add("was", 0)
                .add("oops", 100).build();
        final Document badResult3 = BuilderFactory.start().add("was", 4)
                .add("slowms", 100).build();

        final Command offCommand = new Command("test",
                Command.COMMAND_COLLECTION, BuilderFactory.start()
                        .add("profile", 0)
                        .add("slowms", ProfilingStatus.DEFAULT_SLOW_MS).build());
        final Command slowCommand1 = new Command("test",
                Command.COMMAND_COLLECTION, BuilderFactory.start()
                        .add("profile", 1).add("slowms", 100L).build());
        final Command slowCommand2 = new Command("test",
                Command.COMMAND_COLLECTION, BuilderFactory.start()
                        .add("profile", 1).add("slowms", 1000L).build());
        final Command onCommand = new Command("test",
                Command.COMMAND_COLLECTION, BuilderFactory.start()
                        .add("profile", 2)
                        .add("slowms", ProfilingStatus.DEFAULT_SLOW_MS).build());

        myMockClient.send(eq(offCommand), callback(reply(offResult)));
        expectLastCall();
        myMockClient.send(eq(onCommand), callback(reply(offResult)));
        expectLastCall();

        myMockClient.send(eq(slowCommand1), callback(reply(slowResult)));
        expectLastCall();
        myMockClient.send(eq(slowCommand2), callback(reply(slowResult)));
        expectLastCall();

        myMockClient.send(eq(onCommand), callback(reply(allResult)));
        expectLastCall();
        myMockClient.send(eq(slowCommand2), callback(reply(allResult)));
        expectLastCall();

        myMockClient.send(eq(offCommand), callback(reply(badResult1)));
        expectLastCall();
        myMockClient.send(eq(offCommand), callback(reply(badResult2)));
        expectLastCall();
        myMockClient.send(eq(offCommand), callback(reply(badResult3)));
        expectLastCall();

        replay();

        assertFalse(myTestInstance.setProfilingStatus(ProfilingStatus.OFF));
        assertTrue(myTestInstance.setProfilingStatus(ProfilingStatus.ON));

        assertFalse(myTestInstance
                .setProfilingStatus(ProfilingStatus.slow(100)));
        assertTrue(myTestInstance
                .setProfilingStatus(ProfilingStatus.slow(1000)));

        assertFalse(myTestInstance.setProfilingStatus(ProfilingStatus.ON));
        assertTrue(myTestInstance
                .setProfilingStatus(ProfilingStatus.slow(1000)));

        assertTrue(myTestInstance.setProfilingStatus(ProfilingStatus.OFF));
        assertTrue(myTestInstance.setProfilingStatus(ProfilingStatus.OFF));
        assertTrue(myTestInstance.setProfilingStatus(ProfilingStatus.OFF));

        verify();
    }

    /**
     * Test method for {@link MongoDatabaseImpl#stats()}.
     */
    @Test
    public void testStats() {
        final Document result = BuilderFactory.start().build();

        final Command command = new Command("test", Command.COMMAND_COLLECTION,
                BuilderFactory.start().add("dbStats", 1).build());

        myMockClient.send(eq(command), callback(reply(result)));
        expectLastCall();

        replay();

        assertSame(result, myTestInstance.stats());
        verify();
    }

    /**
     * Performs a {@link EasyMock#replay(Object...)} on the provided mocks and
     * the {@link #myMockClient} object.
     * 
     * @param mocks
     *            The mock to replay.
     */
    private void replay(final Object... mocks) {
        EasyMock.replay(mocks);
        EasyMock.replay(myMockClient, myMockMongoClient);
    }

    /**
     * Creates a reply around the document.
     * 
     * @param replyDoc
     *            The document to include in the reply.
     * @return The {@link Reply}
     */
    private Reply reply(final Document... replyDoc) {
        return new Reply(1, 0, 0, Arrays.asList(replyDoc), false, false, false,
                false);
    }

    /**
     * Performs a {@link EasyMock#verify(Object...)} on the provided mocks and
     * the {@link #myMockClient} object.
     * 
     * @param mocks
     *            The mock to replay.
     */
    private void verify(final Object... mocks) {
        EasyMock.verify(mocks);
        EasyMock.verify(myMockClient, myMockMongoClient);
    }
}
