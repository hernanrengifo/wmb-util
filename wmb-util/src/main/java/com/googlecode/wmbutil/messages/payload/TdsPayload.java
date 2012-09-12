/*
 * Copyright 2007 (C) Callista Enterprise.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *	http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.googlecode.wmbutil.messages.payload;

import com.googlecode.wmbutil.NiceMbException;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMRM;
import com.ibm.broker.plugin.MbMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for working with typical flat file data. This class assumes you
 * have a message containing a set of records, each with a number of fields.
 */
public class TdsPayload extends Payload {

    /**
     * Wrap an message containing a flat file message with the helper class.
     * Automatically locates the MRM tree.
     *
     * @param msg      The message to wrap.
     * @return The helper class
     * @throws MbException
     */
    public static TdsPayload wrap(MbMessage msg) throws MbException {
        MbElement elm = locatePayload(msg);

        if (elm == null) {
            throw new NiceMbException("Failed to find CSV payload");
        }

        return new TdsPayload(elm);
    }

    /**
     * Creates a payload as the last child, even if one already exists
     *
     * @param msg The message on which the payload should be created
     * @return The helper class
     * @throws MbException
     */
    public static TdsPayload create(MbMessage msg) throws MbException {
        MbElement elm = msg.getRootElement().createElementAsLastChild(MbMRM.PARSER_NAME);
        return new TdsPayload(elm);
    }

    /**
     * Wraps if payload already exists, of creates payload otherwise.
     *
     * @param msg The message on which to wrap the payload
     * @return The helper class
     * @throws MbException
     */
    public static TdsPayload wrapOrCreate(MbMessage msg) throws MbException {
        if (has(msg)) {
            return wrap(msg);
        } else {
            return create(msg);
        }
    }

    /**
     * Removes (detaches) the first MRM payload
     *
     * @param msg The message on which to remove the payload
     * @return The helper class for the removed payload
     * @throws MbException
     */
    public static TdsPayload remove(MbMessage msg) throws MbException {
        MbElement elm = locatePayload(msg);

        if (elm != null) {
            elm.detach();
            return new TdsPayload(elm); // TODO: Create Immutable variant
        } else {
            throw new NiceMbException("Failed to find XML payload");
        }
    }

    /**
     * Checks if the message has a payload of this type
     *
     * @param msg The message to check
     * @return true if the payload exists, false otherwise.
     * @throws MbException
     */
    public static boolean has(MbMessage msg) throws MbException {
        MbElement elm = locatePayload(msg);
        return elm != null;
    }

    /**
     * Locates the payload in a message
     *
     * @param msg The message containing the payload
     * @return
     * @throws MbException
     */
    private static MbElement locatePayload(MbMessage msg) throws MbException {
        MbElement elm = msg.getRootElement().getFirstElementByPath("/MRM");

        return elm;
    }

    /**
     * Class constructor
     *
     * @param elm      The message element
     * @throws MbException
     */
    private TdsPayload(MbElement elm) throws MbException {
        super(elm);

    }

    /**
     * Create a record with the specfied name as the last
     * child in the message
     *
     * @param name The name of the record to create
     * @return The created record
     * @throws MbException
     */
    public TdsRecord createRecord(String name) throws MbException {
        return new TdsRecord(getMbElement().createElementAsLastChild(MbElement.TYPE_NAME, name, null));
    }

    /**
     * Get all records with the specified name in the order they are
     * located in the message.
     *
     * @param name The name of the records to retrieve
     * @return The list of records
     * @throws MbException
     */
    public List<TdsRecord> getRecords(String name) throws MbException {
        List elms = (List) getMbElement().evaluateXPath(name);
        List<TdsRecord> records = new ArrayList<TdsRecord>();
        for (Object elm : elms) {
            records.add(new TdsRecord((MbElement) elm));
        }
        return records;
    }

    /**
     * Gets the record of the specified index
     *
     * @param index Position of the wanted record
     * @return The record
     * @throws MbException
     */
    public TdsRecord getRecord(int index) throws MbException {
        List records = getAllRecords();
        try {
            return new TdsRecord((MbElement) records.get(index));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw NiceMbException.propagate(e);
        }
    }

    /**
     * Returns the first record
     *
     * @return The first record
     * @throws MbException
     */
    public TdsRecord getRecord() throws MbException {
        return getRecord(0);
    }

    /**
     * Get all records in the order they are located in the message
     *
     * @return List of all records
     * @throws MbException
     */
    public List getAllRecords() throws MbException {
        List elms = (List) getMbElement().evaluateXPath("*");
        List records = new ArrayList();
        for (Object elm : elms) {
            records.add(new TdsRecord((MbElement) elm));
        }
        return records;
    }
}
