/**
 * Copyright 2003-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.io;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fireflow.model.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author chennieyun
 */
public class Util4JAXPSerializer {
    private static final DateFormat STANDARD_DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
    
    static Element addElement(Document w3cDoc,Element parent, String name){
        Element child = w3cDoc.createElementNS(parent.getNamespaceURI(),name);
        child.setPrefix(parent.getPrefix());
        return (Element)parent.appendChild(child);
    }

    /** 
     * Add a child element with the specific name and the given value to <br>
     * the given parent element and return the child element.  This method <br>
     * will use the namespace of the parent element for the child element's namespace.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, Date value){
        return addElement(w3cDoc,parent, name, value, null);
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, Date value,
    		Date defaultValue){
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(w3cDoc,parent, name);
            child.setTextContent(STANDARD_DF.format(value));
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, String value){
        return addElement(w3cDoc,parent, name, value, null);
    }



    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, String value,
    		String defaultValue){
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(w3cDoc,parent, name);
            child.setTextContent(value);
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, URL value){
        return addElement(w3cDoc,parent, name, value, null);
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, URL value,
    		URL defaultValue){
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(w3cDoc,parent, name);
            child.setTextContent(value.toString());
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, Duration value){
        return addElement(w3cDoc,parent, name, value, null);
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    static Element addElement(Document w3cDoc,Element parent, String name, Duration value,
    		Duration defaultValue){
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(w3cDoc,parent, name);
            child.setTextContent(value.toString());
        }

        return child;
    }

}
