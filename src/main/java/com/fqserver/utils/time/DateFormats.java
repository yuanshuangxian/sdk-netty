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
package com.fqserver.utils.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class DateFormats {

    private static final Map<String, DateFormat> patternFormats = new ConcurrentHashMap<String, DateFormat>();
    private static final Map<Class<?>, String> defaultPatterns = new ConcurrentHashMap<Class<?>, String>();

    public static final String HOUR_PATTERN = "HH";
    public static final String MONTH_PATTERN = "yyyy-MM";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String RFC_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String GMT_DATE_PATTERN = "E, d MMM yyyy HH:mm:ss z";

    public static final String[] DEFAULT_PATTERNS = new String[]{DATETIME_PATTERN,
                                                                 DATE_PATTERN,
                                                                 TIME_PATTERN,
                                                                 TIMESTAMP_PATTERN,
                                                                 RFC_DATE_PATTERN};

    public static final DateFormat DATE_FORMAT = new ConcurrentDateFormat(DATE_PATTERN);
    public static final DateFormat TIME_FORMAT = new ConcurrentDateFormat(TIME_PATTERN);
    public static final DateFormat DATETIME_FORMAT = new ConcurrentDateFormat(DATETIME_PATTERN);
    public static final DateFormat TIMESTAMP_FORMAT = new ConcurrentDateFormat(TIMESTAMP_PATTERN);
    public static final DateFormat REF_DATE_FORMAT = new ConcurrentDateFormat(RFC_DATE_PATTERN);

    static {
        patternFormats.put(DATE_PATTERN, DATE_FORMAT);
        patternFormats.put(TIME_PATTERN, TIME_FORMAT);
        patternFormats.put(DATETIME_PATTERN, DATETIME_FORMAT);
        patternFormats.put(TIMESTAMP_PATTERN, TIMESTAMP_FORMAT);
        patternFormats.put(RFC_DATE_PATTERN, REF_DATE_FORMAT);

        defaultPatterns.put(java.sql.Timestamp.class, TIMESTAMP_PATTERN);
        defaultPatterns.put(java.sql.Time.class, TIME_PATTERN);
        defaultPatterns.put(java.sql.Date.class, DATE_PATTERN);
        defaultPatterns.put(Date.class, DATETIME_PATTERN);
    }

    protected DateFormats() {

    }

    /**
     * Get a date/time formatter using the supplied pattern.
     * 
     * @param pattern
     *            the pattern to format date/time.
     * @return the formatter using the pattern.
     */
    public static DateFormat getFormat(String pattern) {
        DateFormat format = patternFormats.get(pattern);

        if (null == format) {
            format = new ConcurrentDateFormat(pattern);

            patternFormats.put(pattern, format);
        }

        return format;
    }

    /**
     * get a date/time formatter using the pattern of the supplied type. the
     * type can be {@link java.sql.Timestamp}, {@link java.sql.Time},
     * {@link java.sql.Date}, {@link java.util.Date}.
     * 
     * @param type
     *            the type to represent the pattern.
     * @return the formatter using the pattern.
     */
    public static DateFormat getFormat(Class<?> type) {
        return getFormat(getPattern(type));
    }

    /**
     * get the pattern string according to the supplied type. return pattern
     * string "yyyy-MM-dd HH:mm:ss" if there's no pattern string matches the
     * type.
     * 
     * @param type
     *            the type to get the matched pattern string.
     * @return the pattern string according to the supplied type.
     */
    public static String getPattern(Class<?> type) {
        String pattern = defaultPatterns.get(type);

        return null == pattern ? DATETIME_PATTERN : pattern;
    }

    /**
     * @author zhenwei.liu created on 2013 13-8-29 下午5:35
     * @version $Id$
     */

    /** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // System.out.println("put new sdf of pattern " + pattern
                    // + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new
                    // SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            // System.out.println("thread: "
                            // + Thread.currentThread()
                            // + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern, Locale.US);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    public static String formatH(Date date) {
        return getSdf(HOUR_PATTERN).format(date);
    }

    public static String formatM(Date date) {
        return getSdf(MONTH_PATTERN).format(date);
    }

    public static String formatD(Date date) {
        return getSdf(DATE_PATTERN).format(date);
    }

    public static String formatDT(Date date) {
        return getSdf(DATETIME_PATTERN).format(date);
    }

    public static String formatDT(Object date) {
        return getSdf(DATETIME_PATTERN).format(date);
    }

    public static String formatGMT(Date date) {
        SimpleDateFormat sdf = getSdf(GMT_DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String formatGMT(Object date) {
        SimpleDateFormat sdf = getSdf(GMT_DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,
     * 这样每个线程只会有一个SimpleDateFormat
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parseD(String dateStr) throws ParseException {
        return getSdf(DATE_PATTERN).parse(dateStr);
    }

    public static Date parseDT(String dateStr) throws ParseException {
        return getSdf(DATETIME_PATTERN).parse(dateStr);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
}