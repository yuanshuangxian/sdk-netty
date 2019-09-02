package com.fqserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.fqserver.lang.util.concurrent.AtomicLongMap;
import com.fqserver.lang.util.concurrent.CountStats;



public class ServerContext {
    public static boolean isLocalCacheReady = false;

    public static int groupId;
    public static int workerId;

    public static HttpServer httpServer;

    public static final ThreadLocal<AtomicLong> LocalRequestAtomicLong = new ThreadLocal<AtomicLong>() {
        @Override
        protected AtomicLong initialValue() {
            return new AtomicLong(0);
        }
    };
    public static final AtomicLongMap<String> UrlCountMap = AtomicLongMap.create();
    public static final Map<String, CountStats> UrlCountMap2 = new HashMap<String, CountStats>();

    public static long addUrlRequest2(String url) {
        return UrlCountMap.addAndGet(url, 1);
    }

    public static void resetUrlRequest2(String url) {
        UrlCountMap.reset(url);
    }

    public static void addUrlRequest(String url) {
        CountStats count = UrlCountMap2.get(url);
        if (count == null) {
            UrlCountMap2.put(url, new CountStats(1));
        } else {
            count.incr();
        }
    }

    public static void resetUrlRequest(String url) {
        CountStats count = UrlCountMap2.get(url);
        if (count != null) {
            count.reset();
        }
    }

    public static long getLocalRequestId() {
        return LocalRequestAtomicLong.get().addAndGet(1);
    }

    public static String getLocalRequestPrefix() {
        return Thread.currentThread().getId() + "_" + getLocalRequestId();
    }
}