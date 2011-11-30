/*--

 Copyright (C) 2002-2003 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The names "OBE" and "Open Business Engine" must not be used to
    endorse or promote products derived from this software without prior
    written permission.  For written permission, please contact
    me@anthonyeden.com.

 4. Products derived from this software may not be called "OBE" or
    "Open Business Engine", nor may "OBE" or "Open Business Engine"
    appear in their name, without prior written permission from
    Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on OBE, please see <http://www.openbusinessengine.org/>.

 */
package org.fireflow.model.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.QName;


/** Dom4J XPDL Serializer utility class.

    @author Anthony Eden
 * Updated by nychen2000
*/
public class Util4Parser {

    /**
     * 私有构造方法
     */
    private Util4Parser() {
        // no op
    }

    /** 
     * Return the child element with the given name.  The element must be in
     * the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child element
     */
    public static Element child(Element element, String name) {
    	Element child = element.element(new QName(name, element.getNamespace()));
        return child;
    }

    /** 
     * Return the child elements with the given name.  The elements must be in
     * the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child elements
     */
    @SuppressWarnings("unchecked")
	public static List<Element> children(Element element, String name) {
        if (element==null){
            return null;
        }
        return element.elements(new QName(name, element.getNamespace()));
    }

    // Conversion

    /** 
     * Return the value of the child element with the given name.  The element
     * must be in the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child element value
     */
    public static String elementAsString(Element element, String name) {
        String s = element.elementTextTrim(
            new QName(name, element.getNamespace()));
        return (s == null || s.length() == 0) ? null : s;
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
