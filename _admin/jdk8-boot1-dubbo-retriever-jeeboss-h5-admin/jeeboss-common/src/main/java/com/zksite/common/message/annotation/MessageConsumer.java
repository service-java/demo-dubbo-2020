package com.zksite.common.message.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zksite.common.message.MessageServer;

/**
 * 消息消费者。必须注解参数为String的方法上，参数即为接收到的队列消息<br><br>
 * 支持主备/全活模式
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageConsumer {

    public MessageServer server() default MessageServer.Redis;

    /**
     * 队列/topic名称
     * 
     * @return
     */
    String queue();

    /**
     * 监听线程数(子任务数)
     * 
     * @return
     */
    int threads() default 1;

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
     * 当获取不到消息时，休眠间隔，MQ不需要
     * 
     * @return
     */
    int interval() default 100;

    /**
     * 延迟执行，当消息到达时，延迟多少毫秒执行
     * 
     * @return
     */
    int delay() default 0;
}
