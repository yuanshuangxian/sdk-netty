package com.fqserver.server;

import java.util.HashMap;
import java.util.Map;

public final class UrlMap {

    public static final Map<String, Class<? extends RequestHandler>> urlMap = new HashMap<String, Class<? extends RequestHandler>>();

}
