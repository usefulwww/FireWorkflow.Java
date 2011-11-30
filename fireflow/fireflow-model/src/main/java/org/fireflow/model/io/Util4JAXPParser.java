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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author chennieyun
 */
public class Util4JAXPParser {
    /**
     * Return the first child element with the given name,<br>
     * The elements must be in the same name space as the parent element.
     * @param element
     * @param name
     * @return
     */
    public static Element child(Element element, String name) {
    	NodeList childNodeList = element.getChildNodes();
    	String parentNameSpaceURI = null;
    	String childNameSpaceURI = null;
    	for (int i=0;i<childNodeList.getLength();i++){
    		Node child = childNodeList.item(i);
    		parentNameSpaceURI = element.getNamespaceURI();
    		if (parentNameSpaceURI==null) parentNameSpaceURI = "";
 
    		childNameSpaceURI = child.getNamespaceURI();
    		if (childNameSpaceURI==null) childNameSpaceURI = "";
    		
    		if (parentNameSpaceURI.equals(childNameSpaceURI) && name.equals(child.getLocalName())){
    			return (Element)child;
    		}
    	}
    	return null;
    }

    /** 
     * Return the child elements with the given name.  <br>
     * The elements must be inthe same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child elements
     */
    public static List<Element> children(Element element, String name) {
        List<Element> result = new ArrayList<Element>();
        
    	NodeList childNodeList = element.getChildNodes();
    	String parentNameSpaceURI = null;
    	String childNameSpaceURI = null;
    	for (int i=0;i<childNodeList.getLength();i++){
    		Node child = childNodeList.item(i);
    		parentNameSpaceURI = element.getNamespaceURI();
    		if (parentNameSpaceURI==null) parentNameSpaceURI = "";
 
    		childNameSpaceURI = child.getNamespaceURI();
    		if (childNameSpaceURI==null) childNameSpaceURI = "";
    		
    		if (parentNameSpaceURI.equals(childNameSpaceURI) && name.equals(child.getLocalName())){
    			result.add((Element)child);
    		}
    	}        
        return result;
    }

    // Conversion

    /** 
     * Return the value of the child element with the given name.  <br>
     * The element must be in the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child element value
     */
    public static String elementAsString(Element element, String name) {
        Element child = child(element, name);
        if (child!=null){
            return child.getTextContent();
        }else{
            return null;
        }
    }

    /**
     * @param element
     * @param name
     * @return
     * @throws FPDLParserException
     */
    public static Date elementAsDate(Element element, String name) throws
        FPDLParserException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return DateUtilities.getInstance().parse(text);
            //return STANDARD_DF.parse(text);
        } catch (ParseException e) {
            throw new FPDLParserException("Error parsing date: " + text, e);
        }
    }

    /**
     * @param element
     * @param name
     * @return
     */
    public static int elementAsInteger(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    /**
     * @param element
     * @param name
     * @return
     */
    public static boolean elementAsBoolean(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return false;
        }

        return new Boolean(text).booleanValue();
    }

    /**
     * @param element
     * @param name
     * @return
     * @throws FPDLParserException
     */
    public static URL elementAsURL(Element element, String name) throws
        FPDLParserException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return new URL(text);
        } catch (MalformedURLException e) {
            throw new FPDLParserException("Invalid URL: " + text, e);
        }
    }

}
