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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <code>null</code> safe {@link Array} utility
 */
// From apache commons-lang3, under Apache License 2.0
public class Arrays {

    private static final int INDEX_NOT_FOUND = -1;

    protected Arrays() {

    }

    // IndexOf search
    // ----------------------------------------------------------------------

    public static <T> T[] asArray(T... a) {
        return a;
    }

    public static <E, T extends E> List<E> toList(T... elements) {
        List<E> list = new ArrayList<E>();

        if (null != elements) {
            for (T e : elements) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Converts a T[] array to a {@link Set}<T>.
     */
    public static <E, T extends E> Set<E> toSet(T... elements) {
        if (null == elements || elements.length == 0) {
            return new LinkedHashSet<E>();
        } else {
            LinkedHashSet<E> set = new LinkedHashSet<E>();
            for (T e : elements) {
                set.add(e);
            }
            return set;
        }
    }

    /**
     * Checks that {@code fromIndex} and {@code toIndex} are in the range and
     * throws an appropriate exception, if they aren't.
     */
    private static void rangeCheck(int length, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex("
                                               + fromIndex
                                               + ") > toIndex("
                                               + toIndex
                                               + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > length) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

    /**
     * Searches the specified array of chars for the specified value using the
     * binary search algorithm. The array must be sorted (as by the
     * {@link #sort(char[])} method) prior to making this call. If it is not
     * sorted, the results are undefined. If the array contains multiple
     * elements with the specified value, there is no guarantee which one will
     * be found.
     *
     * @param a
     *            the array to be searched
     * @param key
     *            the value to be searched for
     * @return index of the search key, if it is contained in the array;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the key
     *         would be inserted into the array: the index of the first element
     *         greater than the key, or <tt>a.length</tt> if all elements in the
     *         array are less than the specified key. Note that this guarantees
     *         that the return value will be &gt;= 0 if and only if the key is
     *         found.
     */
    public static int binarySearch(char[] a, char key) {
        return binarySearch0(a, 0, a.length, key);
    }

    /**
     * Searches a range of the specified array of chars for the specified value
     * using the binary search algorithm. The range must be sorted (as by the
     * {@link #sort(char[], int, int)} method) prior to making this call. If it
     * is not sorted, the results are undefined. If the range contains multiple
     * elements with the specified value, there is no guarantee which one will
     * be found.
     *
     * @param a
     *            the array to be searched
     * @param fromIndex
     *            the index of the first element (inclusive) to be searched
     * @param toIndex
     *            the index of the last element (exclusive) to be searched
     * @param key
     *            the value to be searched for
     * @return index of the search key, if it is contained in the array within
     *         the specified range; otherwise,
     *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion
     *         point</i> is defined as the point at which the key would be
     *         inserted into the array: the index of the first element in the
     *         range greater than the key, or <tt>toIndex</tt> if all elements
     *         in the range are less than the specified key. Note that this
     *         guarantees that the return value will be &gt;= 0 if and only if
     *         the key is found.
     * @throws IllegalArgumentException
     *             if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *             if {@code fromIndex < 0 or toIndex > a.length}
     * @since 1.6
     */
    public static int binarySearch(char[] a, int fromIndex, int toIndex, char key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    // Like public version, but without range checks.
    private static int binarySearch0(char[] a, int fromIndex, int toIndex, char key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            char midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1); // key not found.
    }
    // Private Methods
    // ---------------------------------------------------------------------------------------------
}