package com.zksite.common.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 已注解形式添加定时任务，当cron值为空时interval、timeUnit、repeat不能为空
 * 
 * @author hanjieHu
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    /**
     * 定时任务名称，调度器唯一
     * 
     * @return
     */
    String name();

    /**
     * 定时任务所在组
     * 
     * @return
     */
    String group() default "";

    /**
     * 是否启用HA模式支持
     * 
     * @return
     */
    boolean isHAEnable() default true;

    /**
     * HA模式下，是否standby模式（是-主备模式，否-全活模式）
     * 
     * @return
     */
    boolean isHAStandby() default true;

    /**
     * 执行间隔
     * 
     * @return
     */
    int interval() default 0;

    /**
     * 执行间隔时间单位
     * 
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;// 执行间隔时间单位

    /**
     * 重复执行次数，-1永久重复执行
     * 
     * @return
     */
    int repeat() default 0;

    /**
     * cron表达式
     * 
     * @return
     */
    String cron() default "";
}
