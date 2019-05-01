package com.zksite.jeeboss.web.modules.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 权限拦截<br>
 * 可以通过配置在方法和class上，拦截粒度有角色和资源两种类型
 * 
 * @author hanjieHu
 *
 */

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Permission {

    /**
     * 权限类型
     * 
     * @return
     */
    public PermissionType type() default PermissionType.RESOURCE;

    public String value();

    public enum PermissionType {
        ROLE(), RESOURCE()
    }
}
