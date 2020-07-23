package com.zksite.common.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 注解缓存支持，缓存存储策略分别有Redis_JSON,Redis_BYTE, Local;<br>
 * 
 * Redis_JSON:以json形式存储在redis<br>
 * Redis_BYTE:以字节码形式存储在redis
 * 
 * 当缓存存储为Redis_JSON时，只支持简单对象缓存<br>
 * Redis_BYTE支持复杂对象存储
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    public Storage storage() default Storage.Redis_JSON;

    /**
     * 缓存key，默认为""。必须设置，否则缓存将无法设置
     * 
     * @return
     */
    public String key() default "";

    /**
     * 缓存有效时长，默认为0表示永久有效。单位：秒 本地缓存Storage=Local时，仅支持0/5/30/60/300/1800/3600
     * 
     * @return
     */
    public int timeout() default 0;

    /**
     * 是否强制刷新缓存，默认为false。
     * 
     * @return
     */
    public boolean refresh() default false;

    /**
     * 是否删除缓存，默认为false。 当对数据库有更新操作时,可以选择两种缓存过期方案: 1. 再次获取更新的结果,使用refresh功能刷新缓存 2. 直接使用remove功能来删除缓存
     *
     * @return
     */
    public boolean remove() default false;

    /**
     * 当缓存对象为List时，需指定反序列化类
     * 
     * @return
     */
    public Class<?> deserializeClass() default Object.class;

    /**
     * 缓存存储
     * 
     * @author Cobe
     *
     */
    public enum Storage {
        Redis_JSON, Redis_BYTE, Local;
    }
}
