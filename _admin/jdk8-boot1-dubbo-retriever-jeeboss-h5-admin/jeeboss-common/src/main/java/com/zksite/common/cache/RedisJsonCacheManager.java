package com.zksite.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zksite.common.utils.JedisClient;

/**
 * redis缓存<br>
 * 以json串形式存储，支持简单对象缓存
 * 
 * @author hanjieHu
 *
 */

@Component
public class RedisJsonCacheManager implements CacheManager {

    @Autowired
    private JedisClient jedisClient;

    @Override
    public void set(String key, Object value, int timeout) {
        jedisClient.set(key, JSON.toJSONString(value), timeout);
    }

    @Override
    public Object get(String key, int timeout) {
        return jedisClient.get(key);
    }

    @Override
    public void remove(String key, int timeout) {
        jedisClient.del(key);
    }

}
