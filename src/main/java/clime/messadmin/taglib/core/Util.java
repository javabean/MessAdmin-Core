/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
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
 */

package clime.messadmin.taglib.core;
//package org.apache.taglibs.standard.tag.common.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import clime.messadmin.utils.Charsets;

/**
 * <p>Utilities in support of tag-handler classes.</p>
 *
 * @author Jan Luehe
 */
public class Util {

    private static final String REQUEST = "request";//$NON-NLS-1$
    private static final String SESSION = "session";//$NON-NLS-1$
    private static final String APPLICATION = "application";//$NON-NLS-1$
    private static final String DEFAULT = "default";//$NON-NLS-1$
    private static final String SHORT = "short";//$NON-NLS-1$
    private static final String MEDIUM = "medium";//$NON-NLS-1$
    private static final String LONG = "long";//$NON-NLS-1$
    private static final String FULL = "full";//$NON-NLS-1$

    /**
     * Converts the given string description of a scope to the corresponding
     * PageContext constant.
     *
     * The validity of the given scope has already been checked by the
     * appropriate TLV.
     *
     * @param scope String description of scope
     *
     * @return PageContext constant corresponding to given scope description
     */
    public static int getScope(String scope) {
        int ret = PageContext.PAGE_SCOPE; // default

        if (REQUEST.equalsIgnoreCase(scope)) {
            ret = PageContext.REQUEST_SCOPE;
        } else if (SESSION.equalsIgnoreCase(scope)) {
            ret = PageContext.SESSION_SCOPE;
        } else if (APPLICATION.equalsIgnoreCase(scope)) {
            ret = PageContext.APPLICATION_SCOPE;
        }

        return ret;
    }

    /**
     * Converts the given string description of a formatting style for
     * dates and times to the corresponding java.util.DateFormat constant.
     *
     * @param style String description of formatting style for dates and times
     * @param errCode Error code to throw if given style is invalid
     *
     * @return java.util.DateFormat constant corresponding to given style
     *
     * @throws JspException if the given style is invalid
     */
    public static int getStyle(String style, String error)
            throws JspException {
        int ret = DateFormat.DEFAULT;

        if (style != null) {
            if (DEFAULT.equalsIgnoreCase(style)) {
                ret = DateFormat.DEFAULT;
            } else if (SHORT.equalsIgnoreCase(style)) {
                ret = DateFormat.SHORT;
            } else if (MEDIUM.equalsIgnoreCase(style)) {
                ret = DateFormat.MEDIUM;
            } else if (LONG.equalsIgnoreCase(style)) {
                ret = DateFormat.LONG;
            } else if (FULL.equalsIgnoreCase(style)) {
                ret = DateFormat.FULL;
            } else {
                throw new JspException(error);
            }
        }

        return ret;
    }


    /**
     * Get the value associated with a content-type attribute.
     * Syntax defined in RFC 2045, section 5.1.
     */
    public static String getContentTypeAttribute(String input, String name) {
        int begin;
        int end;
        int index = input.toUpperCase().indexOf(name.toUpperCase());
        if (index == -1) {
            return null;
        }
        index = index + name.length(); // positioned after the attribute name
        index = input.indexOf('=', index); // positioned at the '='
        if (index == -1) {
            return null;
        }
        index += 1; // positioned after the '='
        input = input.substring(index).trim();

        if (input.charAt(0) == '"') {
            // attribute value is a quoted string
            begin = 1;
            end = input.indexOf('"', begin);
            if (end == -1) {
                return null;
            }
        } else {
            begin = 0;
            end = input.indexOf(';');
            if (end == -1) {
                end = input.indexOf(' ');
            }
            if (end == -1) {
                end = input.length();
            }
        }
        return input.substring(begin, end).trim();
    }
    
    /**
     * URL encodes a string, based on the supplied character encoding.
     * This performs the same function as java.next.URLEncode.encode
     * in J2SDK1.4, and should be removed if the only platform supported
     * is 1.4 or higher.
     * @param s The String to be URL encoded.
     * @param enc The character encoding 
     * @return The URL encoded String
     * [taken from jakarta-tomcat-jasper/jasper2
     *  org.apache.jasper.runtime.JspRuntimeLibrary.java]
     * @deprecated use {@link URLEncoder#encode(String, String)}
     */
    public static String URLEncode(String s, String enc) {

	if (s == null) {
	    return "null";//$NON-NLS-1$
	}

	if (enc == null) {
	    enc = "UTF-8";	// Is this right? //$NON-NLS-1$
	}

	StringBuffer out = new StringBuffer(s.length());
	ByteArrayOutputStream buf = new ByteArrayOutputStream();
	OutputStreamWriter writer = null;
//	try {
	    writer = new OutputStreamWriter(buf, Charsets.forName(enc));
//	} catch (java.io.UnsupportedEncodingException ex) {
//	    // Use the default encoding?
//	    writer = new OutputStreamWriter(buf);
//	}
	
	for (int i = 0; i < s.length(); ++i) {
	    int c = s.charAt(i);
	    if (c == ' ') {
		out.append('+');
	    } else if (isSafeChar(c)) {
		out.append((char)c);
	    } else {
		// convert to external encoding before hex conversion
		try {
		    writer.write(c);
		    writer.flush();
		} catch(IOException e) {
		    buf.reset();
		    continue;
		}
		byte[] ba = buf.toByteArray();
		for (int j = 0; j < ba.length; ++j) {
		    out.append('%');
		    // Converting each byte in the buffer
		    out.append(Character.forDigit((ba[j]>>4) & 0xf, 16));
		    out.append(Character.forDigit(ba[j] & 0xf, 16));
		}
		buf.reset();
	    }
	}
	return out.toString();
    }

    private static boolean isSafeChar(int c) {
	if (c >= 'a' && c <= 'z') {
	    return true;
	}
	if (c >= 'A' && c <= 'Z') {
	    return true;
	}
	if (c >= '0' && c <= '9') {
	    return true;
	}
	if (c == '-' || c == '_' || c == '.' || c == '!' ||
	    c == '~' || c == '*' || c == '\'' || c == '(' || c == ')') {
	    return true;
	}
	return false;
    }

    /**
     * HttpServletRequest.getLocales() returns the server's default locale
     * if the request did not specify a preferred language.
     * We do not want this behavior, because it prevents us from using
     * the fallback locale.
     * We therefore need to return an empty Enumeration if no preferred
     * locale has been specified. This way, the logic for the fallback
     * locale will be able to kick in.
     */
    public static Enumeration getRequestLocales(HttpServletRequest request) {
        Enumeration values = request.getHeaders("accept-language");//$NON-NLS-1$
        if (values == null) {
            // No header for "accept-language". Simply return
            // a new empty enumeration.
            // System.out.println("Null accept-language");
            return new Vector().elements();
        } else if (values.hasMoreElements()) {
            // At least one "accept-language". Simply return
            // the enumeration returned by request.getLocales().
            // System.out.println("At least one accept-language");
            return request.getLocales();
        } else {
            // No header for "accept-language". Simply return
            // the empty enumeration.
            // System.out.println("No accept-language");
            return values;
        }
    }
}
