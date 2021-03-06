/*
 * #%L
 * JulLogTest.java - mongodb-async-driver - Allanbank Consulting, Inc.
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
package com.allanbank.mongodb.util.log;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JulLogTest provides tests for the {@link JulLog} class.
 * 
 * @copyright 2014, Allanbank Consulting, Inc., All Rights Reserved
 */
public class JulLogTest {

    /** The handler to capture the log records. */
    private CaptureHandler myCaptureHandler = null;

    /** The JUL logger instance. */
    private Logger myJulLog = null;

    /** The Log under test. */
    private Log myTestLog = null;

    /**
     * Initializes the logger for the test.
     * 
     * @throws Exception
     *             On a failure initializing the logger.
     */
    @Before
    public void setUp() throws Exception {
        myCaptureHandler = new CaptureHandler();

        myJulLog = Logger.getLogger(JulLogTest.class.getName());
        myJulLog.addHandler(myCaptureHandler);
        myJulLog.setUseParentHandlers(false);
        myJulLog.setLevel(Level.ALL);

        myTestLog = new JulLogFactory().doGetLog(JulLogTest.class.getName());
    }

    /**
     * Cleanup after the test.
     * 
     * @throws Exception
     *             On a failure cleaning up the logger.
     */
    @After
    public void tearDown() throws Exception {
        myJulLog.removeHandler(myCaptureHandler);
        myJulLog.setUseParentHandlers(true);
        myJulLog.setLevel(null);

        myCaptureHandler.close();

        myCaptureHandler = null;
        myJulLog = null;
        myTestLog = null;

        LogFactory.reset();
    }

    /**
     * Test for the {@link AbstractLog#debug(String)} method.
     */
    @Test
    public void testDebugString() {
        final String method = "testDebugString";
        final String messsage = "Debug Message";
        final Level level = Level.FINE;

        myTestLog.debug(messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#debug(String, Object...)} method.
     */
    @Test
    public void testDebugStringObjectArray() {
        final String method = "testDebugStringObjectArray";
        final String messsage = "Debug Message";
        final Level level = Level.FINE;

        myTestLog.debug(messsage, "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#debug(Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testDebugThrowableStringObjectArray() {
        final String method = "testDebugThrowableStringObjectArray";
        final String messsage = "Debug Message";
        final Level level = Level.FINE;
        final Throwable thrown = new Throwable();

        myTestLog.debug(thrown, messsage, "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

    /**
     * Test for the {@link AbstractLog#error(String)} method.
     */
    @Test
    public void testErrorString() {
        final String method = "testErrorString";
        final String messsage = "Error Message";
        final Level level = Level.SEVERE;

        myTestLog.error(messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#error(String)} method.
     */
    @Test
    public void testErrorStringDisabled() {
        final String messsage = "Error Message";

        myJulLog.setLevel(Level.OFF);

        myTestLog.error(messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(0));
    }

    /**
     * Test for the {@link AbstractLog#error(String, Object...)} method.
     */
    @Test
    public void testErrorStringObjectArray() {
        final String method = "testErrorStringObjectArray";
        final String messsage = "Error - Hello World";
        final Level level = Level.SEVERE;

        myTestLog.error("{} - {} {}", "Error", "Hello", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#error(String, Object...)} method.
     */
    @Test
    public void testErrorStringObjectArrayWithNullArray() {
        final String method = "testErrorStringObjectArrayWithNullArray";
        final String messsage = "Error - Hello World";
        final Level level = Level.SEVERE;

        myTestLog.error("Error - Hello World", (Object[]) null);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#error(Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testErrorThrowableStringObjectArray() {
        final String method = "testErrorThrowableStringObjectArray";
        final String messsage = "Error - Hello World";
        final Level level = Level.SEVERE;
        final Throwable thrown = new Throwable();

        myTestLog.error(thrown, "Error - Hello {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

    /**
     * Test for the {@link LogFactory#getLog(Class)} method.
     */
    @Test
    public void testGetLog() {
        final Log log = LogFactory.getLog(getClass());

        // If the SLF4J test runs first we get and Slf4jLog.
        assertThat(log,
                either(instanceOf(JulLog.class)).or(instanceOf(Slf4jLog.class)));
    }

    /**
     * Test for the {@link AbstractLog#info(String)} method.
     */
    @Test
    public void testInfoString() {
        final String method = "testInfoString";
        final String messsage = "Info Message";
        final Level level = Level.INFO;

        myTestLog.info(messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#info(String, Object...)} method.
     */
    @Test
    public void testInfoStringObjectArray() {
        final String method = "testInfoStringObjectArray";
        final String messsage = "Info - Hello World";
        final Level level = Level.INFO;

        myTestLog.info("Info - Hello {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#info(Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testInfoThrowableStringObjectArray() {
        final String method = "testInfoThrowableStringObjectArray";
        final String messsage = "Info - Hello World";
        final Level level = Level.INFO;
        final Throwable thrown = new Throwable();

        myTestLog.info(thrown, "Info - Hello {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

    /**
     * Test for the {@link AbstractLog#isDebugEnabled()} method.
     * 
     * @throws Exception
     *             On a failure to initialize the logger.
     */
    @Test
    public void testIsDebugEnabled() throws Exception {
        assertThat(myTestLog.isDebugEnabled(), is(true));

        // Overcome the caching.
        setUp();
        myJulLog.setLevel(Level.INFO);
        assertThat(myTestLog.isDebugEnabled(), is(false));

        // See the cached level.
        myJulLog.setLevel(Level.FINE);
        assertThat(myTestLog.isDebugEnabled(), is(false));
    }

    /**
     * Test for the {@link AbstractLog#isErrorEnabled()} method.
     * 
     * @throws Exception
     *             On a failure to initialize the logger.
     */
    @Test
    public void testIsErrorEnabled() throws Exception {
        assertThat(myTestLog.isErrorEnabled(), is(true));

        // Overcome the caching.
        setUp();
        myJulLog.setLevel(Level.OFF);
        assertThat(myTestLog.isErrorEnabled(), is(false));

        // See the cached level.
        myJulLog.setLevel(Level.SEVERE);
        assertThat(myTestLog.isErrorEnabled(), is(false));
    }

    /**
     * Test for the {@link AbstractLog#isInfoEnabled()} method.
     * 
     * @throws Exception
     *             On a failure to initialize the logger.
     */
    @Test
    public void testIsInfoEnabled() throws Exception {
        assertThat(myTestLog.isInfoEnabled(), is(true));

        // Overcome the caching.
        setUp();
        myJulLog.setLevel(Level.WARNING);
        assertThat(myTestLog.isInfoEnabled(), is(false));

        // See the cached level.
        myJulLog.setLevel(Level.INFO);
        assertThat(myTestLog.isInfoEnabled(), is(false));
    }

    /**
     * Test for the {@link AbstractLog#isWarnEnabled()} method.
     * 
     * @throws Exception
     *             On a failure to initialize the logger.
     */
    @Test
    public void testIsWarnEnabled() throws Exception {
        assertThat(myTestLog.isWarnEnabled(), is(true));

        // Overcome the caching.
        setUp();
        myJulLog.setLevel(Level.SEVERE);
        assertThat(myTestLog.isWarnEnabled(), is(false));

        // See the cached level.
        myJulLog.setLevel(Level.WARNING);
        assertThat(myTestLog.isWarnEnabled(), is(false));
    }

    /**
     * Test for the {@link AbstractLog#log(Level, String)} method.
     */
    @Test
    public void testLogLevelString() {
        final String method = "testLogLevelString";
        final String messsage = "Log Message";
        final Level level = Level.WARNING;

        myTestLog.log(level, messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#log(Level, String, Object...)} method.
     */
    @Test
    public void testLogLevelStringObjectArray() {
        final String method = "testLogLevelStringObjectArray";
        final String messsage = "Info - Hello World";
        final Level level = Level.INFO;

        myTestLog.info("{}{}{}", "Info - ", "Hello", " World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#log(Level, Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testLogLevelThrowableStringObjectArray() {
        final String method = "testLogLevelThrowableStringObjectArray";
        final String messsage = "Debug - Hello World";
        final Level level = Level.FINE;
        final Throwable thrown = new Throwable();

        myTestLog.debug(thrown, "Debug - Hello {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

    /**
     * Test for the {@link AbstractLog#log(Level, String)} method.
     */
    @Test
    public void testLogLevelTrace() {
        final String method = "testLogLevelTrace";
        final String messsage = "Log Message";
        final Level level = Level.parse("0");

        myTestLog.log(level, messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#info(Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testTooFewArgLocations() {
        final String method = "testTooFewArgLocations";
        final String messsage = "Info - Hello World {}";
        final Level level = Level.INFO;
        final Throwable thrown = new Throwable();

        myTestLog.info(thrown, "Info - Hello {} {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

    /**
     * Test for the {@link AbstractLog#warn(String)} method.
     */
    @Test
    public void testWarnString() {
        final String method = "testWarnString";
        final String messsage = "Warning Message";
        final Level level = Level.WARNING;

        myTestLog.warn(messsage);

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#warn(String, Object...)} method.
     */
    @Test
    public void testWarnStringObjectArray() {
        final String method = "testWarnStringObjectArray";
        final String messsage = "Warn - Hello World";
        final Level level = Level.WARNING;

        myTestLog.warn("{} - Hello {}", "Warn", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), nullValue());
    }

    /**
     * Test for the {@link AbstractLog#warn(Throwable, String, Object...)}
     * method.
     */
    @Test
    public void testWarnThrowableStringObjectArray() {
        final String method = "testWarnThrowableStringObjectArray";
        final String messsage = "Warn - Hello World";
        final Level level = Level.WARNING;
        final Throwable thrown = new Throwable();

        myTestLog.warn(thrown, "Warn - Hello {}", "World");

        final List<LogRecord> records = myCaptureHandler.getRecords();
        assertThat(records.size(), is(1));

        final LogRecord record = records.get(0);
        assertThat(record.getLevel(), is(level));
        assertThat(record.getLoggerName(), is(JulLogTest.class.getName()));
        assertThat(record.getMessage(), is(messsage));
        assertThat(record.getSourceClassName(), is(JulLogTest.class.getName()));
        assertThat(record.getSourceMethodName(), is(method));
        assertThat(record.getThreadID(), is((int) Thread.currentThread()
                .getId()));
        assertThat(record.getThrown(), sameInstance(thrown));
    }

}
