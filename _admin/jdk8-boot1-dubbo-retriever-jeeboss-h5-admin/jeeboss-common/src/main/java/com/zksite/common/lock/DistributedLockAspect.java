package com.zksite.common.lock;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.zksite.common.aop.aspect.BaseAspect;
import com.zksite.common.exception.BizException;
import com.zksite.common.lock.annotaion.Lock;
import com.zksite.common.lock.annotaion.LockParam;

/**
 * 分布式锁注解实现<br>
 * 
 * @author hanjieHu
 *
 */
@Component
@Aspect
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class DistributedLockAspect extends BaseAspect {


    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockAspect.class);

    @Autowired
    private DistributedLock distributedLock;

    @Pointcut("@annotation(com.zksite.common.lock.annotaion.Lock)")
    private void pointCutMethod() {};

    private static final Random random = new Random(Integer.MAX_VALUE);

    @Around(value = "pointCutMethod()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Method method = getTargetMethod(pjp);
        Lock redisLock = method.getAnnotation(Lock.class);
        String lockKey = generateLockKey(pjp, method, redisLock);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("redis lock key={}", lockKey);
        }
        String value = System.currentTimeMillis() + random.nextInt() + "";
        try {
            if (distributedLock.tryLock(lockKey, value, redisLock.ttl(), redisLock.waitTime())) {
                return pjp.proceed();
            }
        } catch (Throwable e) {
            throw e;
        } finally {
            boolean flag = distributedLock.unlock(lockKey, value);
            if (flag) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("release redis lock,lockKey={}", lockKey);
                }
            } else {
                throw new BizException("等待超时");
            }
        }
        return null;
    }

    private String generateLockKey(ProceedingJoinPoint pjp, Method method, Lock redisLock) {
        StringBuilder keyBuffer = new StringBuilder(redisLock.lockKey());

        List<Object[]> params =
                getMethodAnnotationAndParametersByAnnotation(pjp, method, LockParam.class);

        for (Object[] param : params) {
            keyBuffer.append(param[1] == null ? "_" : param[1].toString()).append(":");
        }
        if (params.size() > 0) {
            keyBuffer.deleteCharAt(keyBuffer.length() - 1);
        }
        return keyBuffer.toString();
    }

}
