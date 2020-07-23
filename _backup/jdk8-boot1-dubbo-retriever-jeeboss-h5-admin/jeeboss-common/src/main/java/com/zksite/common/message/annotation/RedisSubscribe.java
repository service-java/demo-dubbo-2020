package com.zksite.common.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis消息订阅者<br><br>
 * 支持主备/全活模式
 * 
 * @author hanjieHu
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisSubscribe {


    /**
     * 订阅的频道
     * 
     * @return
     */
    public String channel() default "";

    /**
     * 是否启用HA模式支持。默认为true
     * 
     * @return
     */
    public boolean haEnable() default true;

    /**
     * HA模式下，是否standby模式（是-主备模式，否-全活模式）。默认为true
     * 
     * @return
     */
    public boolean haStandyby() default true;
}
