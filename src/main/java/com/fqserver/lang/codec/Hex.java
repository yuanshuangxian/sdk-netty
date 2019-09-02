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
package com.fqserver.lang.codec;

public class Hex {

    public static String encodeHexString(final byte[] data) {
        return HexApacheImpl.encodeHexString(data);
    }

    public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
        return HexApacheImpl.encodeHexString(data, toLowerCase);
    }

    public static byte[] encodeHex(final byte[] data) {
        return HexApacheImpl.encodeHex(data);
    }

    public static byte[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return HexApacheImpl.encodeHex(data, toLowerCase);
    }

    public static byte[] decodeHex(final String hex) {
        return HexApacheImpl.decodeHex(hex.toCharArray());
    }

    public static byte[] decodeHex(final byte[] hex) {
        return HexApacheImpl.decodeHexByte(hex);
    }

    protected Hex() {

    }
}