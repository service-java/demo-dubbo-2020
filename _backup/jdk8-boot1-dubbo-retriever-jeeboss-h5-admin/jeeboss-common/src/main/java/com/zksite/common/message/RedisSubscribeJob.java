package com.zksite.common.message;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zksite.common.job.AnnotationJob;
import com.zksite.common.utils.JedisClient;

import redis.clients.jedis.JedisPubSub;

public class RedisSubscribeJob extends AnnotationJob {

    @Autowired
    private JedisClient jedisClient;


    private String channel;


    @Override
    protected void onStart() {
        channel = (String) getJob().getData().get(RedisSubscribeRegistry.CHANNEL);
    }

    @Override
    protected void action() {
        jedisClient.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    getMethod().invoke(getInstance(), message);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }, channel);
    }


}
