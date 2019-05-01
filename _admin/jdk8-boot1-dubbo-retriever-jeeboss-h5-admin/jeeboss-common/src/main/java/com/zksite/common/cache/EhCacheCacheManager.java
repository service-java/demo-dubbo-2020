package com.zksite.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;


@Component
public class EhCacheCacheManager implements CacheManager {

    @Autowired
    private org.springframework.cache.ehcache.EhCacheCacheManager manager;

    private Cache getCache(int timeout) {
        String cacheName = String.valueOf(timeout);
        Cache cache = manager.getCache(cacheName);
        if (cache == null) {
            throw new RuntimeException("local cache of timeout[" + timeout + "] is not supported.");
        }

        return cache;
    }

    public Object get(String key, int timeout) {
        ValueWrapper valueWrapper = getCache(timeout).get(key);
        if (valueWrapper != null) {
            return valueWrapper.get();
        }

        return null;
    }

    public void set(String key, Object value, int timeout) {
        getCache(timeout).put(key, value);
    }

    public void remove(String key, int timeout) {
        getCache(timeout).evict(key);
    }

}
