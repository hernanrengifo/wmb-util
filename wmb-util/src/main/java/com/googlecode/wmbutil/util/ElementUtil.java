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

package com.googlecode.wmbutil.util;

import com.google.common.collect.ImmutableMap;
import com.ibm.broker.plugin.*;

public class ElementUtil {

    public static ImmutableMap<String, Object> asImmutableMap(MbElement parent) throws MbException {
        MbElement child = parent.getFirstChild();

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        while (child != null) {
            if (child.getType() == MbElement.TYPE_NAME_VALUE) {
                builder.put(child.getName(), child.getValue());
            }
            child = child.getNextSibling();
        }
        return builder.build();
    }

    public static Object copyValue(MbElement inElm, String inXpath, MbElement outElm, String outXpath) throws MbException {
        Object value = inElm.getFirstElementByPath(inXpath).getValue();
        outElm.getFirstElementByPath(outXpath).setValue(value);
        return value;
    }

    public static void setValue(MbElement outElm, String outXpath, Object value) throws MbException {
        outElm.getFirstElementByPath(outXpath).setValue(value);
    }

    public static Object getValue(MbElement inElm, String inXpath) throws MbException {
        return inElm.getFirstElementByPath(inXpath).getValue();
    }

    public static boolean isMRM(MbElement elm) throws MbException {
        return elm.getParserClassName().equalsIgnoreCase(MbMRM.PARSER_NAME);
    }

    public static boolean isXML(MbElement elm) throws MbException {
        return elm.getParserClassName().equalsIgnoreCase(MbXML.PARSER_NAME);
    }

    public static boolean isXMLNS(MbElement elm) throws MbException {
        return elm.getParserClassName().equalsIgnoreCase(MbXMLNS.PARSER_NAME);
    }

    public static boolean isXMLNSC(MbElement elm) throws MbException {
        return elm.getParserClassName().equalsIgnoreCase(MbXMLNSC.PARSER_NAME);
    }
}