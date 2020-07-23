package com.zksite.common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zksite.common.utils.JedisClient;

/**
 * redis实现分布式锁
 * 
 * @author hanjieHu
 *
 */
@Component
public class RedisDistributedLock implements DistributedLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisDistributedLock.class);

    private static final String default_value = "-1";

    @Autowired
    private JedisClient jedisClient;

    @Override
    public boolean tryLock(String key, int ttl) {
        return tryLock(key, default_value, ttl);
    }

    @Override
    public boolean tryLock(String key, String value, int ttl) {
        boolean isLock = false;
        String setResult = jedisClient.set(key, value, "NX", "EX", ttl);
        if (!"OK".equals(setResult)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("try to lock {} failed.", key);
            }
        } else {
            isLock = true;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("try to lock {} sucess.", key);
            }
        }
        return isLock;
    }

    @Override
    public boolean tryLock(String key, int ttl, int timeout) {
        return tryLock(key, default_value, ttl, timeout);
    }

    @Override
    public boolean tryLock(String key, String value, int ttl, int timeout) {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            if (tryLock(key, value, ttl)) {
                return true;
            }
            try {
                Thread.sleep(50);// 先睡5毫秒，防止线程一直忙
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        jedisClient.del(key);
    }

    @Override
    public boolean unlock(String key, String value) {
        String string = jedisClient.get(key);
        if (string.equals(value)) {
            jedisClient.del(key);
            return true;
        }
        return false;
    }

}
