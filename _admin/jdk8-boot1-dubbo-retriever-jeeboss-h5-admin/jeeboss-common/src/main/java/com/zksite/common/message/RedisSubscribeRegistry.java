package com.zksite.common.message;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.zksite.common.job.AbstractAnnotationRegistry;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.message.annotation.RedisSubscribe;


/**
 * redis订阅者bean注册<br>
 * 配合{@link com.zksite.common.message.annotation.RedisSubscribe}一起使用,当有订阅的频道有消息到达时，执行目标方法
 * 
 * @author hanjieHu
 *
 */
@Component
public class RedisSubscribeRegistry
        extends AbstractAnnotationRegistry<RedisSubscribe, RedisSubscribeJob> {

    public static final String CHANNEL = "channel";

    private static final String JOB_GROUP = "message";

    public static final String REDIS_SUBSCRIBE_SUFFIX = "_redis_subscribe";

    @Override
    public JobInfo generateJobInfo(Method method) {
        RedisSubscribe redisSubscribe = method.getAnnotation(RedisSubscribe.class);
        JobInfo job = new JobInfo();
        job.setName(redisSubscribe.channel().replaceAll(":", "_") + REDIS_SUBSCRIBE_SUFFIX);
        job.setGroup(JOB_GROUP);
        job.setInterval(5);
        job.setTimeUnit(TimeUnit.SECONDS);
        job.setRepeat(0);
        job.setIsHAEnable(redisSubscribe.haEnable());
        job.setIsHAStandby(redisSubscribe.haStandyby());
        job.getData().put(CHANNEL, redisSubscribe.channel());
        return job;
    }

}
