package com.zksite.common.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttConsumer {

	public String topic();// 订阅主题

	/**
	 * 是否启用HA模式支持。默认为true
	 * 
	 * @return
	 */
	boolean haEnable() default true;

	/**
	 * HA模式下，是否standby模式（是-主备模式，否-全活模式）。默认为true
	 * 
	 * @return
	 */
	boolean haStandyby() default true;

	/**
	 * rabbit mq 1和2合并
	 * 
	 * @return
	 */
	int qos() default 1;

}
