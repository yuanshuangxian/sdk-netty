/*
 * Copyright 2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.fqserver.lang.util;

import java.io.UnsupportedEncodingException;

/**
 * <code>null</code> safe {@link String} utility
 */
// From apache commons-lang3, under Apache License 2.0
public class Strings {

    public static final String EMPTY = "";
    public static final String COMMA = ",";

    protected Strings() {

    }

    // Fomatting
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Formats a template string, and inserts dynamic values in placeholders.
     * </p>
     * 
     * <pre>
     * 
     * Strings.format("Hello {0}","world") -> "Hello world"
     * 
     * </pre>
     * 
     * @param template
     * @param args
     * 
     * @return formated string , empty("") if null input.
     */
    public static String format(String template, Object... args) {
        if (isEmpty(template)) {
            return EMPTY;
        }

        char[] templateChars = template.toCharArray();

        int templateLength = templateChars.length;
        int length = 0;
        int tokenCount = args.length;
        for (int i = 0; i < tokenCount; i++) {
            Object sourceString = args[i];
            if (sourceString != null) {
                length += sourceString.toString().length();
            }
        }

        // The following buffer size is just an initial estimate. It is legal
        // for
        // any given pattern, such as {0}, to occur more than once, in which
        // case
        // the buffer size will expand automatically if need be.
        StringBuilder buffer = new StringBuilder(length + templateLength);

        int lastStart = 0;
        for (int i = 0; i < templateLength; i++) {
            char ch = templateChars[i];
            if (ch == '{') {
                // Only check for single digit patterns that have an associated
                // token.
                if (i + 2 < templateLength && templateChars[i + 2] == '}') {
                    int tokenIndex = templateChars[i + 1] - '0';
                    if (tokenIndex >= 0 && tokenIndex < tokenCount) {
                        buffer.append(templateChars, lastStart, i - lastStart);
                        Object sourceString = args[tokenIndex];
                        if (sourceString != null)
                            buffer.append(sourceString.toString());

                        i += 2;
                        lastStart = i + 1;
                    }
                }
            }
            // ELSE: Do nothing. The character will be added in later.
        }

        buffer.append(templateChars, lastStart, templateLength - lastStart);

        return new String(buffer);
    }

    /**
     * Returns the string representation of the <code>Object</code> argument.
     *
     * @param obj
     *            an <code>Object</code>.
     * @return if the argument is <code>null</code>, then a string equal to
     *         <code>""</code>; otherwise, the value of
     *         <code>obj.toString()</code> is returned.
     * @see java.lang.Object#toString()
     */
    public static String valueOf(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    /**
     * Returns the string representation of the <code>Object</code> argument.
     *
     * @param obj
     *            an <code>Object</code>.
     * @return if the argument is <code>null</code>, then a string equal to
     *         <code>""</code>; otherwise, the value of
     *         <code>obj.toString()</code> is returned.
     * @see java.lang.Object#toString()
     */
    public static String valueOf(Object obj, String def) {
        return (obj == null) ? def : obj.toString();
    }

    /**
     * Returns the string representation of the <code>Object</code> argument.
     *
     * @param obj
     *            an <code>Object</code>.
     * @return if the argument is <code>null</code>, then a string equal to
     *         <code>""</code>; otherwise, the value of
     *         <code>obj.toString()</code> is returned.
     * @see java.lang.Object#toString()
     */
    public static String valueOf(Object obj, int length) {
        String s = (obj == null) ? "" : obj.toString();
        byte[] b = s.getBytes(Charsets.UTF_8);
        if (b.length > length) {
            b = java.util.Arrays.copyOfRange(b, 0, length);
            return new String(b, Charsets.UTF_8);
        }
        return s;
    }

    // Empty checks
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     * 
     * <pre>
     * Strings.isEmpty(null)      = true
     * Strings.isEmpty("")        = true
     * Strings.isEmpty(" ")       = false
     * Strings.isEmpty("bob")     = false
     * Strings.isEmpty("  bob  ") = false
     * </pre>
     * 
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * CharSequence. That functionality is available in isBlank().
     * </p>
     * 
     * @param string
     *            the CharSequence to check, may be null
     * 
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty ("") and not null.
     * </p>
     * 
     * <pre>
     * Strings.isNotEmpty(null)      = false
     * Strings.isNotEmpty("")        = false
     * Strings.isNotEmpty(" ")       = true
     * Strings.isNotEmpty("bob")     = true
     * Strings.isNotEmpty("  bob  ") = true
     * </pre>
     * 
     * @param string
     *            the CharSequence to check, may be null
     * 
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotEmpty(CharSequence string) {
        return null != string && string.length() > 0;
    }

    // private methods
    // ----------------------------------------------------------------------------------------------------------------------

    // Concat
    // -----------------------------------------------------------------------

    // Replacing
    // -----------------------------------------------------------------------

    // startsWith
    // -----------------------------------------------------------------------

    // endsWith
    // -----------------------------------------------------------------------

    // Remove
    // -----------------------------------------------------------------------

    // Get Bytes
    // -----------------------------------------------------------------------
    /**
     * Encodes the given string into a sequence of bytes using the named
     * charset, storing the result into a new byte array.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and rethrows it
     * as {@link IllegalStateException}, which should never happen for a
     * required charset name. Use this method when the encoding is required to
     * be in the JRE.
     * </p>
     * 
     * @param string
     *            the String to encode, may be <code>null</code>
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return encoded bytes, or <code>[]</code> if the input string was
     *         <code>null</code>
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught,
     *             which should never happen for a required charset name.
     * @see String#getBytes(String)
     */
    public static byte[] getBytes(String str, String charset) {
        if (isEmpty(str)) {
            return new byte[0];
        }

        try {
            return str.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Encodes the given string into a sequence of bytes using the ISO-8859-1
     * charset, storing the result into a new byte array.
     * 
     * @param string
     *            the String to encode, may be <code>null</code>
     * @return encoded bytes, or <code>null</code> if the input string was
     *         <code>null</code>
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never
     *             according the the Java specification.
     * @see <a
     *      href="http://download.oracle.com/javase/1.5.0/docs/api/java/nio/charset/Charset.html">Standard
     *      charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesIso8859_1(String string) {
        return getBytes(string, Encoding.ISO_8859_1.name());
    }

    /**
     * Encodes the given string into a sequence of bytes using the US-ASCII
     * charset, storing the result into a new byte array.
     * 
     * @param string
     *            the String to encode, may be <code>null</code>
     * @return encoded bytes, or <code>null</code> if the input string was
     *         <code>null</code>
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never
     *             according the the Java specification.
     * @see <a
     *      href="http://download.oracle.com/javase/1.5.0/docs/api/java/nio/charset/Charset.html">Standard
     *      charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUsAscii(String string) {
        return getBytes(string, Encoding.US_ASCII.name());
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-8
     * charset, storing the result into a new byte array.
     * 
     * @param string
     *            the String to encode, may be <code>null</code>
     * @return encoded bytes, or <code>[]</code> if the input string was
     *         <code>null</code>
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never
     *             according the the Java specification.
     * @see <a
     *      href="http://download.oracle.com/javase/1.5.0/docs/api/java/nio/charset/Charset.html">Standard
     *      charsets</a>
     * @see #getBytes(String, String)
     */
    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Encoding.UTF_8.name());
    }

    // New String
    // -----------------------------------------------------------------------

    /**
     * Constructs a new <code>String</code> by decoding the specified array of
     * bytes using the given charset.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and re-throws it
     * as {@link IllegalStateException}, which should never happen for a
     * required charset name. Use this method when the encoding is required to
     * be in the JRE.
     * </p>
     * 
     * @param bytes
     *            The bytes to be decoded into characters, may be
     *            <code>null</code>
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return A new <code>String</code> decoded from the specified array of
     *         bytes using the given charset, or <code>""</code> if the input
     *         byte array was <code>null</code>.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught,
     *             which should never happen for a required charset name.
     * @see String#String(byte[], String)
     */
    public static String newString(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return Strings.EMPTY;
        }

        try {
            return new String(bytes, charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of
     * bytes using the ISO-8859-1 charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters, may be
     *            <code>null</code>
     * @return A new <code>String</code> decoded from the specified array of
     *         bytes using the ISO-8859-1 charset, or <code>""</code> if the
     *         input byte array was <code>null</code>.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught,
     *             which should never happen since the charset is required.
     */
    public static String newStringIso8859_1(byte[] bytes) {
        return newString(bytes, Encoding.ISO_8859_1.name());
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of
     * bytes using the US-ASCII charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of
     *         bytes using the US-ASCII charset, or <code>""</code> if the input
     *         byte array was <code>null</code>.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught,
     *             which should never happen since the charset is required.
     */
    public static String newStringUsAscii(byte[] bytes) {
        return newString(bytes, Encoding.US_ASCII.name());
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of
     * bytes using the UTF-8 charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of
     *         bytes using the UTF-8 charset, or <code>""</code> if the input
     *         byte array was <code>null</code>.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught,
     *             which should never happen since the charset is required.
     */
    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, Encoding.UTF_8.name());
    }

    // isNumber
    // ----------------------------------------------------------------------------------------------------------------------
    /**
     * <p>
     * Checks if the CharSequence contains only Unicode digits. A decimal point
     * is not a Unicode digit and returns false.
     * </p>
     * 
     * <p>
     * {@code null} will return {@code false}. An empty CharSequence
     * (length()=0) will return {@code false}.
     * </p>
     * 
     * <pre>
     * Strings.isDigits(null)   = false
     * Strings.isDigits("")     = false
     * Strings.isDigits("  ")   = false
     * Strings.isDigits("123")  = true
     * Strings.isDigits("12 3") = false
     * Strings.isDigits("ab2c") = false
     * Strings.isDigits("12-3") = false
     * Strings.isDigits("12.3") = false
     * </pre>
     * 
     * @param cs
     *            the CharSequence to check, may be null
     * 
     * @return {@code true} if only contains digits, and is non-null
     */
    public static boolean isDigits(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks whether the String a valid Java number.
     * </p>
     * 
     * <p>
     * Valid numbers include hexadecimal marked with the <code>0x</code>
     * qualifier, scientific notation and numbers marked with a type qualifier
     * (e.g. 123L).
     * </p>
     * 
     * <p>
     * <code>Null</code> and empty String will return <code>false</code>.
     * </p>
     * 
     * @param str
     *            the <code>String</code> to check
     * @return <code>true</code> if the string is a correctly formatted number
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0' && chars[start + 1] == 'x') {
            int i = start + 2;
            if (i == sz) {
                return false; // str == "0x"
            }
            // checking hex (it can't be anything else)
            for (; i < chars.length; i++) {
                if ((chars[i] < '0' || chars[i] > '9')
                    && (chars[i] < 'a' || chars[i] > 'f')
                    && (chars[i] < 'A' || chars[i] > 'F')) {
                    return false;
                }
            }
            return true;
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another
        // digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns
                && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't
        // pass
        return !allowSigns && foundDigit;
    }

    // private static boolean regionMatches(String string, boolean ignoreCase,
    // int thisStart, String substring, int start, int length) {
    // return string.regionMatches(ignoreCase, thisStart, substring, start,
    // length);
    // }

}
