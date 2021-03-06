/*
 * #%L
 * WriteVisitor.java - mongodb-async-driver - Allanbank Consulting, Inc.
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
package com.allanbank.mongodb.bson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.allanbank.mongodb.bson.element.SizeAwareVisitor;

/**
 * A visitor to myOutput.write the BSON document to a {@link OutputStream}. The
 * BSON specification uses prefixed length integers in several locations. This
 * visitor uses a {@link StringEncoder} and the {@link Element#size()} to
 * compute the size item about to be written removing the requirements to buffer
 * the data being written.
 * 
 * @api.no This class is <b>NOT</b> part of the drivers API. This class may be
 *         mutated in incompatible ways between any two releases of the driver.
 * @copyright 2011-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
/* package */class WriteVisitor implements SizeAwareVisitor {

    /** Stream to myOutput.write to. */
    protected final BsonOutputStream myOutput;

    /**
     * Creates a new {@link WriteVisitor}.
     * 
     * @param output
     *            The stream to myOutput.write to.
     */
    public WriteVisitor(final BsonOutputStream output) {
        myOutput = output;
    }

    /**
     * Creates a new {@link WriteVisitor}.
     * 
     * @param output
     *            The stream to myOutput.write to.
     */
    public WriteVisitor(final OutputStream output) {
        this(new BsonOutputStream(output));
    }

    /**
     * Returns the I/O exception encountered by the visitor.
     * 
     * @return The I/O exception encountered by the visitor.
     */
    public IOException getError() {
        return myOutput.getError();
    }

    /**
     * Returns true if the visitor had an I/O error.
     * 
     * @return True if the visitor had an I/O error, false otherwise.
     */
    public boolean hasError() {
        return myOutput.hasError();
    }

    /**
     * Clears the internal buffer and prepares to myOutput.write another
     * document.
     */
    public void reset() {
        myOutput.reset();
    }

    /**
     * Determines the size of the document written in BSON format. The
     * {@link Document}'s size is cached for subsequent write operations.
     * 
     * @param doc
     *            The document to determine the size of.
     * @return The number of bytes require to Write the document.
     * @deprecated Replaced with {@link Document#size()}. This method will be
     *             removed after the 2.2.0 release.
     */
    @Deprecated
    public int sizeOf(final Document doc) {
        return (int) doc.size();
    }

    /**
     * Computes the size of the encoded UTF8 String.
     * 
     * @param string
     *            The string to determine the length of.
     * @return The length of the string encoded as UTF8.
     * @see StringEncoder#utf8Size(String)
     */
    public int utf8Size(final String string) {
        return StringEncoder.utf8Size(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final List<Element> elements) {

        int size = 4 + 1; // Length (int,4) and null.
        for (final Element element : elements) {
            size += element.size();
        }

        writeElements(elements, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitArray(final String name, final List<Element> elements) {
        myOutput.writeByte(ElementType.ARRAY.getToken());
        myOutput.writeCString(name);
        visit(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitArray(final String name, final List<Element> elements,
            final long totalSize) {
        myOutput.writeByte(ElementType.ARRAY.getToken());
        myOutput.writeCString(name);
        writeElements(elements,
                ((int) totalSize) - (myOutput.sizeOfCString(name) + 1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitBinary(final String name, final byte subType,
            final byte[] data) {
        myOutput.writeByte(ElementType.BINARY.getToken());
        myOutput.writeCString(name);
        switch (subType) {
        case 2: {
            myOutput.writeInt(data.length + 4);
            myOutput.writeByte(subType);
            myOutput.writeInt(data.length);
            myOutput.writeBytes(data);
            break;

        }
        case 0:
        default:
            myOutput.writeInt(data.length);
            myOutput.writeByte(subType);
            myOutput.writeBytes(data);
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitBoolean(final String name, final boolean value) {

        myOutput.writeByte(ElementType.BOOLEAN.getToken());
        myOutput.writeCString(name);
        myOutput.writeByte(value ? (byte) 0x01 : 0x00);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    public void visitDBPointer(final String name, final String databaseName,
            final String collectionName, final ObjectId id) {
        myOutput.writeByte(ElementType.DB_POINTER.getToken());
        myOutput.writeCString(name);
        myOutput.writeString(databaseName + "." + collectionName);
        // Just to be complicated the Object ID is big endian.
        myOutput.writeInt(EndianUtils.swap(id.getTimestamp()));
        myOutput.writeLong(EndianUtils.swap(id.getMachineId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitDocument(final String name, final List<Element> elements) {
        myOutput.writeByte(ElementType.DOCUMENT.getToken());
        myOutput.writeCString(name);
        visit(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitDocument(final String name, final List<Element> elements,
            final long totalSize) {
        myOutput.writeByte(ElementType.DOCUMENT.getToken());
        myOutput.writeCString(name);
        writeElements(elements,
                ((int) totalSize) - (myOutput.sizeOfCString(name) + 1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitDouble(final String name, final double value) {
        myOutput.writeByte(ElementType.DOUBLE.getToken());
        myOutput.writeCString(name);
        myOutput.writeLong(Double.doubleToLongBits(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitInteger(final String name, final int value) {
        myOutput.writeByte(ElementType.INTEGER.getToken());
        myOutput.writeCString(name);
        myOutput.writeInt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitJavaScript(final String name, final String code) {
        myOutput.writeByte(ElementType.JAVA_SCRIPT.getToken());
        myOutput.writeCString(name);
        myOutput.writeString(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitJavaScript(final String name, final String code,
            final Document scope) {
        myOutput.writeByte(ElementType.JAVA_SCRIPT_WITH_SCOPE.getToken());
        myOutput.writeCString(name);

        myOutput.writeInt(4 + StringEncoder.computeStringSize(code)
                + (int) scope.size());
        myOutput.writeString(code);

        scope.accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitLong(final String name, final long value) {
        myOutput.writeByte(ElementType.LONG.getToken());
        myOutput.writeCString(name);
        myOutput.writeLong(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitMaxKey(final String name) {
        myOutput.writeByte(ElementType.MAX_KEY.getToken());
        myOutput.writeCString(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitMinKey(final String name) {
        myOutput.writeByte(ElementType.MIN_KEY.getToken());
        myOutput.writeCString(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitMongoTimestamp(final String name, final long value) {
        myOutput.writeByte(ElementType.MONGO_TIMESTAMP.getToken());
        myOutput.writeCString(name);
        myOutput.writeLong(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitNull(final String name) {
        myOutput.writeByte(ElementType.NULL.getToken());
        myOutput.writeCString(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitObjectId(final String name, final ObjectId id) {
        myOutput.writeByte(ElementType.OBJECT_ID.getToken());
        myOutput.writeCString(name);
        // Just to be complicated the Object ID is big endian.
        myOutput.writeInt(EndianUtils.swap(id.getTimestamp()));
        myOutput.writeLong(EndianUtils.swap(id.getMachineId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitRegularExpression(final String name, final String pattern,
            final String options) {
        myOutput.writeByte(ElementType.REGEX.getToken());
        myOutput.writeCString(name);
        myOutput.writeCString(pattern);
        myOutput.writeCString(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitString(final String name, final String value) {
        myOutput.writeByte(ElementType.STRING.getToken());
        myOutput.writeCString(name);
        myOutput.writeString(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitSymbol(final String name, final String symbol) {
        myOutput.writeByte(ElementType.SYMBOL.getToken());
        myOutput.writeCString(name);
        myOutput.writeString(symbol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitTimestamp(final String name, final long timestamp) {
        myOutput.writeByte(ElementType.UTC_TIMESTAMP.getToken());
        myOutput.writeCString(name);
        myOutput.writeLong(timestamp);
    }

    /**
     * Writes a list of elements.
     * 
     * @param elements
     *            The sub elements of the document.
     * @param size
     *            The size of the elements.
     */
    protected void writeElements(final List<Element> elements, final int size) {
        myOutput.writeInt(size);
        for (final Element element : elements) {
            element.accept(this);
        }
        myOutput.writeByte((byte) 0);
    }

}