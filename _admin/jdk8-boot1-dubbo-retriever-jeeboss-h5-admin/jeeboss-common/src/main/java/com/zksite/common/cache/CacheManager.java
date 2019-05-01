package com.zksite.common.cache;

public interface CacheManager {

    void set(String key, Object value, int timeout);

    Object get(String key, int timeout);

    void remove(String key, int timeout);
}
