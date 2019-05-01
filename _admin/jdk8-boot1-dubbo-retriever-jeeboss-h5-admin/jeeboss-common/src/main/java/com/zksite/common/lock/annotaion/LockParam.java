package com.zksite.common.lock.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁参数，必须结合Lock注解一起使用。<br/>
 * 同一Lock支持多个CacheParam，按参数顺序进行拼装
 * 
 * @author Cobe
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockParam {
}
