package com.zksite.web.common.monitor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 统计最大值，最小值，平均值
 * 
 * @author hanjieHu
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Histogram {

}
