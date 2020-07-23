package com.zksite.common.lock.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * @author hanjieHu
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁服务器
     * 
     * @return
     */
    public LockServer server() default LockServer.Redis;

    /**
     * 锁键
     * 
     * @return
     */
    public String lockKey();

    /**
     * 锁时长，单位秒
     * 
     * @return
     */
    public int ttl() default 30;


    /**
     * 是否等待获取锁
     * 
     * @return
     */
    public boolean isWait() default false;

    /**
     * 等待时长，单位毫秒
     * 
     * @return
     */
    public int waitTime() default 3000;

    public enum LockServer {
        Redis
    }
}
