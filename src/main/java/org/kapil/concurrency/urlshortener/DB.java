package org.kapil.concurrency.urlshortener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DB {
    public static Map<String, String> cache = new ConcurrentHashMap<>();
}