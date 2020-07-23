package com.zksite.common.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zksite.common.aop.aspect.BaseAspect;
import com.zksite.common.cache.annotation.CacheParam;
import com.zksite.common.cache.annotation.Cacheable;
import com.zksite.common.cache.annotation.Cacheable.Storage;

@Component
@Aspect
public class CacheAspect extends BaseAspect implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    private static final String STRING_CLASS_SIMPLE_NAME = "String";

    private static final String LIST_CLASS_SIMPLE_NAME = "List";

    public static final String CACHE_NULL_REPLACE_HOLDER = "[-1]";

    private Map<Storage, CacheManager> cacheManagerContainer = new HashMap<>();

    @Pointcut("@annotation(com.zksite.common.cache.annotation.Cacheable)")
    public void cachePointcut() {}

    @Around(value = "cachePointcut()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Method method = getTargetMethod(pjp);

        if (logger.isDebugEnabled()) {
            logger.debug("pointcut @{}", method.getName());
        }

        Cacheable cacheable = getAnnotation(method, Cacheable.class);
        String key = generateCacheKey(pjp, method, cacheable);

        // remove cache
        if (cacheable.remove()) {
            removeCache(cacheable, key);
            return pjp.proceed();
        }

        // get from cache
        Object cacheValue = null;
        try {
            cacheValue = getCache(cacheable, key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        // read from db
        if (cacheValue == null) {
            Object obj = pjp.proceed();

            // set to cache
            setCache(cacheable, key, obj);
            return obj;
        }
        // 即使从缓存获取到数据，有可能是null占位符
        else if (CACHE_NULL_REPLACE_HOLDER.equals(cacheValue)) {
            return null;
        } else {
            if (cacheable.storage() == Cacheable.Storage.Redis_JSON) {
                // 字符串类型，直接返回
                if (STRING_CLASS_SIMPLE_NAME.equals(method.getReturnType().getSimpleName())) {
                    return cacheValue;
                }
                // 列表
                else if (LIST_CLASS_SIMPLE_NAME.equals(method.getReturnType().getSimpleName())) {
                    return JSONArray.parseArray((String) cacheValue, cacheable.deserializeClass());
                }
                // 其他类型，则以拦截方法返回类型反序列化返回
                else {
                    return JSONObject.parseObject((String) cacheValue, method.getReturnType());
                }
            }
            return cacheValue;
        }

    }

    private String generateCacheKey(ProceedingJoinPoint pjp, Method method, Cacheable cacheable) {
        if (StringUtils.isBlank(cacheable.key())) {
            throw new RuntimeException(
                    "Cacheable.key is empty in method[" + method.getName() + "].");
        }

        StringBuilder keyBuffer = new StringBuilder(cacheable.key());

        List<Object[]> annotationAndParams =
                getMethodAnnotationAndParametersByAnnotation(pjp, method, CacheParam.class);

        for (Object[] annotationAndParam : annotationAndParams) {
            keyBuffer.append(((CacheParam) annotationAndParam[0]).prefix());
            keyBuffer
                    .append(annotationAndParam[1] == null ? "_" : annotationAndParam[1].toString());
            keyBuffer.append(((CacheParam) annotationAndParam[0]).suffix());
            keyBuffer.append(":");
        }
        if (annotationAndParams.size() > 0) {
            keyBuffer.deleteCharAt(keyBuffer.length() - 1);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("cache key={}", keyBuffer);
        }
        return keyBuffer.toString();
    }


    private Object getCache(Cacheable cacheable, String key) {
        if (cacheable.refresh()) {
            return null;
        }
        CacheManager cacheManager = cacheManagerContainer.get(cacheable.storage());
        Object object = cacheManager.get(key, cacheable.timeout());
        return object;
    }

    private void removeCache(Cacheable cacheable, String key) {
        CacheManager cacheManager = cacheManagerContainer.get(cacheable.storage());
        cacheManager.remove(key, cacheable.timeout());
    }

    private void setCache(Cacheable cacheable, String key, Object value) {
        CacheManager cacheManager = cacheManagerContainer.get(cacheable.storage());
        cacheManager.set(key, value, cacheable.timeout());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            EhCacheCacheManager cacheManager =
                    event.getApplicationContext().getBean(EhCacheCacheManager.class);
            RedisByteCacheManager redisByteCacheManager =
                    event.getApplicationContext().getBean(RedisByteCacheManager.class);
            RedisJsonCacheManager redisJsonCacheManager =
                    event.getApplicationContext().getBean(RedisJsonCacheManager.class);
            cacheManagerContainer.put(Storage.Local, cacheManager);
            cacheManagerContainer.put(Storage.Redis_BYTE, redisByteCacheManager);
            cacheManagerContainer.put(Storage.Redis_JSON, redisJsonCacheManager);
        }
    }

}
