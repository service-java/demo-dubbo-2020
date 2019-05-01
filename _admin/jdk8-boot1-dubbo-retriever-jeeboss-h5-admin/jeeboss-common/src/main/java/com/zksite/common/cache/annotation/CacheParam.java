package com.zksite.common.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存动态参数，参数将用于拼装成缓存key，必须结合Cacheable注解一起使用。<br/>
 * 同一Cacheable支持多个CacheParam，按参数顺序进行拼装
 * 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheParam {
	/**
	 * 组装缓存key前缀，默认为空字符串
	 * 
	 * @return
	 */
	public String prefix() default "";

	/**
	 * 组装缓存key后缀，默认为空字符串
	 * 
	 * @return
	 */
	public String suffix() default "";
}
