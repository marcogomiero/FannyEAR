package com.platformteam.service;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class CacheService {

    private final Cache<String, String> cache;

    public CacheService() {
        DefaultCacheManager cacheManager = new DefaultCacheManager();
        cache = cacheManager.getCache("fanny-cache", true);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }

    public String get(String key) {
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }
}
