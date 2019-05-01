package com.zksite.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置中心配置数据项<br/>
 * 要求必须注解在public变量或方法上，注解在private上将无效<br/>
 * 支持多个数据类型，包括String/int/Integer/long/Long/double/Double/float/Float/
 * boolean/Boolean/JSONObject/JSONArray<br/>
 *<strong>注意：如果注解在属性上时，调用获取值的类和注解上的类不能同一个（具体原因个人还没搞明白，如有知道其中原理的烦请大家不吝赐教）</strong>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CentralConfiguration {

    /**
     * 配置中心的配置项Key<br/>
     * 如果配置中心采用ZK实现，则对应配置中心的path，如“/config/xx/yy”
     * 
     * @return
     */
    public String key() default "";

    /**
     * 配置项默认值<br/>
     * 若配置中心无对应Key的配置，则会将此值写入配置中心，然后返回此默认值<br/>
     * 
     * @return
     */
    public String defaultValue() default "";

    /**
     * 当首次访问配置中心获取配置出错时，是否忽略错误，默认为false。<br/>
     * 若设置为true，则忽略并取默认值<br/>
     * 此设置用于无ZK环境下使用
     * 
     * @deprecated 为无ZK环境而设置，目前均已启用ZK
     * @return
     */
    public boolean ignoreError() default false;

}
