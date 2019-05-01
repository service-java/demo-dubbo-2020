package com.zksite.common.cache;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis缓存管理器</br>
 * 以byte形式存储缓存，支持复杂对象缓存。
 * 当使用此缓存时，存储的对象必须实现{@link java.io.Serializable}接口<br>
 * 若需要更改对象的序列化方式，只需修改redisTemplate的注入
 * @author hanjieHu
 *
 */
@Component
public class RedisByteCacheManager implements CacheManager {

    @Resource(name="redisTemplateObject")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, int timeout) {
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(key);
        boundValueOps.set(value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key, int timeout) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    public void remove(String key, int timeout) {
        redisTemplate.boundHashOps(key).delete(key);
    }

}
