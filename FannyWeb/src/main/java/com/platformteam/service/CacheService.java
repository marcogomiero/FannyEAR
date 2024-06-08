package com.platformteam.service;

import org.infinispan.Cache;
import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private SpringEmbeddedCacheManager cacheManager;

    public void put(String key, String value) {
        Cache<String, String> cache = (Cache<String, String>) cacheManager.getCache("myCache");
        assert cache != null;
        cache.put(key, value);
    }

    public String get(String key) {
        Cache<String, String> cache = (Cache<String, String>) cacheManager.getCache("myCache");
        assert cache != null;
        return cache.get(key);
    }

    public void remove(String key) {
        Cache<String, String> cache = (Cache<String, String>) cacheManager.getCache("myCache");
        assert cache != null;
        cache.remove(key);
    }
}
