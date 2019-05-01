package com.zksite.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster.Reset;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Slowlog;

/**
 * Jedis Cache 工具类
 * 
 */
@SuppressWarnings("deprecation")
public class JedisClient {

    private Logger logger = LoggerFactory.getLogger(JedisClient.class);

    private JedisPool jedisPool;



    public JedisClient(JedisPool jedisPool) {
        super();
        this.jedisPool = jedisPool;
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value
                        : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String set(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取List缓存
     * 
     * @param key 键
     * @return 值
     */
    public List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存
     * 
     * @param key 键
     * @return 值
     */
    public List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (byte[] bs : list) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long listObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = Sets.newHashSet();
                Set<byte[]> set = jedis.smembers(getBytesKey(key));
                for (byte[] bs : set) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Set缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map缓存
     * 
     * @param key 键
     * @return 值
     */
    public Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 设置Map缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Map缓存
     * 
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public String mapPut(String key, Map<String, String> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            logger.debug("mapObjectPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapObjectPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long mapRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean mapExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     * 
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * 
     * @param key 键
     * @return
     */
    public Long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                result = jedis.del(key);
                logger.debug("del {}", key);
            } else {
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * 
     * @param key 键
     * @return
     */
    public long delObject(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                result = jedis.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("delObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * 
     * @param key 键
     * @return
     */
    public Boolean exists(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.warn("exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * 
     * @param key 键
     * @return
     */
    public boolean existsObject(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取资源
     * 
     * @return
     * @throws JedisException
     */
    public Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.warn("getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源
     * 
     * @param jedis
     * @param isBroken
     */
    public void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    /**
     * 释放资源
     * 
     * @param jedis
     * @param isBroken
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 获取byte[]类型Key
     * 
     * @param key
     * @return
     */
    public byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return ((String) object).getBytes();
        } else {
            return ObjectUtils.serialize(object);
        }
    }

    /**
     * Object转换byte[]类型
     * 
     * @param key
     * @return
     */
    public byte[] toBytes(Object object) {
        return ObjectUtils.serialize(object);
    }

    /**
     * byte[]型转换Object
     * 
     * @param key
     * @return
     */
    public Object toObject(byte[] bytes) {
        return ObjectUtils.unserialize(bytes);
    }

    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            logger.debug("existsObject {}", key);
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String setex(String key, int timeout, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            logger.debug("existsObject {}", key);
            return jedis.setex(key, timeout, value);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long lpush(String queue, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lpush(queue, keys);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    public void lpush(String queue, String messages) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.lpush(queue, messages);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
    }

    public List<String> brpop(int i, String queue) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.brpop(i, queue);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String set(final String key, final String value, final String nxxx, final String expx,
            final int time) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, value, nxxx, expx, time);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setnx(key, value);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long expire(String key, int ttl) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.expire(key, ttl);
        } catch (Exception e) {
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    public String set(String key, String value, String nxxx, String expx, long time) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, value, nxxx, expx, time);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long exists(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.exists(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long del(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.del(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String type(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.type(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public String rename(String oldkey, String newkey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.rename(oldkey, newkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long renamenx(String oldkey, String newkey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.renamenx(oldkey, newkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long expireAt(String key, long unixTime) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long move(String key, int dbIndex) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.move(key, dbIndex);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String getSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> mget(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.mget(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String mset(String... keysvalues) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.mset(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long msetnx(String... keysvalues) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.msetnx(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long decrBy(String key, long integer) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.decrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long incrBy(String key, long integer) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double incrByFloat(String key, double value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incrByFloat(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long append(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.append(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String substr(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.substr(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hget(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hmset(key, hash);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long hincrBy(String key, String field, long value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double hincrByFloat(String key, String field, double value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrByFloat(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean hexists(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long hlen(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hlen(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> hkeys(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hkeys(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public List<String> hvals(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hvals(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashMap<String, String>(0);
    }

    public Long rpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.rpush(key, strings);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long llen(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.llen(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String ltrim(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String lindex(String key, long index) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lindex(key, index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String lset(String key, long index, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long lrem(String key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String lpop(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String rpop(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String rpoplpush(String srckey, String dstkey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sadd(key, members);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long srem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.srem(key, members);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String spop(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.spop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> spop(String key, long count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.spop(key, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long smove(String srckey, String dstkey, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.smove(srckey, dstkey, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean sismember(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> sinter(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sinter(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long sinterstore(String dstkey, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sinterstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> sunion(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sunion(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long sunionstore(String dstkey, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sunionstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> sdiff(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sdiff(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long sdiffstore(String dstkey, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sdiffstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String srandmember(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.srandmember(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> srandmember(String key, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.srandmember(key, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zadd(String key, double score, String member, ZAddParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zadd(key, score, member, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zadd(key, scoreMembers, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long zrem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrem(key, members);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double zincrby(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double zincrby(String key, double score, String member, ZIncrByParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zincrby(key, score, member, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zrank(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zrevrank(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String watch(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.watch(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> sort(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sort(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public List<String> sort(String key, SortingParams sortingParameters) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sort(key, sortingParameters);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public List<String> blpop(int timeout, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.blpop(timeout, keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public List<String> blpop(String... args) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.blpop(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public List<String> brpop(String... args) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.brpop(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sort(key, sortingParameters, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long sort(String key, String dstkey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sort(key, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> brpop(int timeout, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.brpop(timeout, keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long zcount(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zcount(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset,
            int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset,
            int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrevrangeByScore(String key, String max, String min) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset,
            int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset,
            int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<Tuple>(0);
    }

    public Long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zremrangeByScore(String key, double start, double end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zremrangeByScore(String key, String start, String end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zunionstore(String dstkey, String... sets) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zunionstore(dstkey, sets);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zunionstore(dstkey, params, sets);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zinterstore(String dstkey, String... sets) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zinterstore(dstkey, sets);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zinterstore(dstkey, params, sets);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long zlexcount(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zlexcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Set<String> zrangeByLex(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByLex(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByLex(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrevrangeByLex(String key, String max, String min) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByLex(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeByLex(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashSet<String>(0);
    }

    public Long zremrangeByLex(String key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zremrangeByLex(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long strlen(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.strlen(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long lpushx(String key, String... string) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.lpushx(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long persist(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.persist(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long rpushx(String key, String... string) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.rpushx(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String echo(String string) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.echo(string);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.linsert(key, where, pivot, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String brpoplpush(String source, String destination, int timeout) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.brpoplpush(source, destination, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean setbit(String key, long offset, boolean value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setbit(key, offset, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean setbit(String key, long offset, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setbit(key, offset, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean getbit(String key, long offset) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getbit(key, offset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long setrange(String key, long offset, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.setrange(key, offset, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String getrange(String key, long startOffset, long endOffset) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long bitpos(String key, boolean value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.bitpos(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long bitpos(String key, boolean value, BitPosParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.bitpos(key, value, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> configGet(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.configGet(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String configSet(String parameter, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.configSet(parameter, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Object eval(String script, int keyCount, String... params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.eval(script, keyCount, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
    }

    public Long publish(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.psubscribe(jedisPubSub, patterns);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.eval(script, keys, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Object eval(String script) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.eval(script);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Object evalsha(String script) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.evalsha(script);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.evalsha(sha1, keys, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Object evalsha(String sha1, int keyCount, String... params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.evalsha(sha1, keyCount, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Boolean scriptExists(String sha1) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scriptExists(sha1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<Boolean> scriptExists(String... sha1) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scriptExists(sha1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<Boolean>(0);
    }

    public String scriptLoad(String script) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scriptLoad(script);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<Slowlog> slowlogGet() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.slowlogGet();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<Slowlog>(0);
    }

    public List<Slowlog> slowlogGet(long entries) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.slowlogGet(entries);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<Slowlog>(0);
    }

    public Long objectRefcount(String string) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.objectRefcount(string);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long bitcount(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.bitcount(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long bitcount(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.bitcount(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.bitop(op, destKey, srcKeys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<Map<String, String>> sentinelMasters() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelMasters();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<Map<String, String>>(0);
    }

    public List<String> sentinelGetMasterAddrByName(String masterName) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelGetMasterAddrByName(masterName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long sentinelReset(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelReset(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<Map<String, String>> sentinelSlaves(String masterName) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelSlaves(masterName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<Map<String, String>>(0);
    }

    public String sentinelFailover(String masterName) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelFailover(masterName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelMonitor(masterName, ip, port, quorum);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String sentinelRemove(String masterName) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelRemove(masterName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String sentinelSet(String masterName, Map<String, String> parameterMap) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sentinelSet(masterName, parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public byte[] dump(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.dump(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new byte[0];
    }

    public String restore(String key, int ttl, byte[] serializedValue) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.restore(key, ttl, serializedValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long pexpire(String key, long milliseconds) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pexpire(key, milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long pexpireAt(String key, long millisecondsTimestamp) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pexpireAt(key, millisecondsTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long pttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pttl(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String psetex(String key, long milliseconds, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.psetex(key, milliseconds, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String set(String key, String value, String nxxx) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, value, nxxx);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clientKill(String client) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clientKill(client);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clientSetname(String name) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clientSetname(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String migrate(String host, int port, String key, int destinationDb, int timeout) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.migrate(host, port, key, destinationDb, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> scan(int cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scan(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> scan(int cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scan(cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> sscan(String key, int cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sscan(key, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> sscan(String key, int cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sscan(key, cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Tuple> zscan(String key, int cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zscan(key, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Tuple> zscan(String key, int cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zscan(key, cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> scan(String cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scan(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> scan(String cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.scan(cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hscan(key, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.hscan(key, cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> sscan(String key, String cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sscan(key, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.sscan(key, cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Tuple> zscan(String key, String cursor) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zscan(key, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.zscan(key, cursor, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterNodes() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterNodes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterMeet(String ip, int port) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterMeet(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterReset(Reset resetType) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterReset(resetType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterAddSlots(int... slots) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterAddSlots(slots);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterDelSlots(int... slots) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterDelSlots(slots);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterInfo() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterInfo();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> clusterGetKeysInSlot(int slot, int count) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterGetKeysInSlot(slot, count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String clusterSetSlotNode(int slot, String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSetSlotNode(slot, nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterSetSlotMigrating(int slot, String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSetSlotMigrating(slot, nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterSetSlotImporting(int slot, String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSetSlotImporting(slot, nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterSetSlotStable(int slot) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSetSlotStable(slot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterForget(String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterForget(nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterFlushSlots() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterFlushSlots();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long clusterKeySlot(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterKeySlot(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long clusterCountKeysInSlot(int slot) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterCountKeysInSlot(slot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterSaveConfig() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSaveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String clusterReplicate(String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterReplicate(nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> clusterSlaves(String nodeId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSlaves(nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public String clusterFailover() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterFailover();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<Object> clusterSlots() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.clusterSlots();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public String asking() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.asking();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> pubsubChannels(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pubsubChannels(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Map<String, String> pubsubNumSub(String... channels) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pubsubNumSub(channels);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new HashMap<String, String>();
    }


    public Long pfadd(String key, String... elements) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pfadd(key, elements);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public long pfcount(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pfcount(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    public long pfcount(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pfcount(keys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    public String pfmerge(String destkey, String... sourcekeys) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.pfmerge(destkey, sourcekeys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> blpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.blpop(timeout, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public Long geoadd(String key, double longitude, double latitude, String member) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geoadd(key, longitude, latitude, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geoadd(key, memberCoordinateMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double geodist(String key, String member1, String member2) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geodist(key, member1, member2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geodist(key, member1, member2, unit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public List<String> geohash(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geohash(key, members);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(0);
    }

    public List<GeoCoordinate> geopos(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.geopos(key, members);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<GeoCoordinate>(0);
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
            double radius, GeoUnit unit) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.georadius(key, longitude, latitude, radius, unit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<GeoRadiusResponse>(0);
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
            double radius, GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.georadius(key, longitude, latitude, radius, unit, param);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<GeoRadiusResponse>(0);
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
            GeoUnit unit) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.georadiusByMember(key, member, radius, unit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<GeoRadiusResponse>(0);
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
            GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.georadiusByMember(key, member, radius, unit, param);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<GeoRadiusResponse>(0);
    }


}
