package com.zksite.common.lock;



/**
 * 分布式锁
 * 
 * @author hanjieHu
 *
 */
public interface DistributedLock {

    /**
     * 获取锁，快速失败
     * 
     * @param key
     * @param ttl 持有锁时长
     * @return
     */
    boolean tryLock(String key, int ttl);

    /**
     * 获取锁，快速失败
     * 
     * @param key
     * @param value 自定义值
     * @param ttl 持有锁时长
     * @return
     */
    boolean tryLock(String key, String value, int ttl);

    /**
     * 获取锁，尝试等待
     * 
     * @param key
     * @param ttl
     * @param timeout
     * @return
     */
    boolean tryLock(String key, int ttl, int timeout);

    /**
     * 获取锁，尝试等待
     * 
     * @param key
     * @param value
     * @param ttl
     * @param timeout
     * @return
     */
    boolean tryLock(String key, String value, int ttl, int timeout);

    /**
     * 释放锁
     * 
     * @param key
     */
    void unlock(String key);


    /**
     * 检查释放锁<br>
     * 当返回值为false时，可能是当前操作已超时<br>
     * 只有当返回true时才会释放锁
     * 
     * @param key
     * @param value
     * @return 当value和存储的值不一致，返回false
     */
    boolean unlock(String key, String value);
}
