package com.zksite.common.message;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.zksite.common.job.AnnotationJob;
import com.zksite.common.utils.JedisClient;


/**
 * 消息消费者<br>
 * 提供了对redis,RabbitMQ的支持，配合{@link com.zksite.common.message.annotation.MessageConsumer}一起使用<br>
 * 当有消息到达时，执行目标方法。可以通过注解配置，是否延迟执行
 * 
 * @author hanjieHu
 *
 */
public class MessageConsumerJob extends AnnotationJob {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private Connection mqConnection;

    private String queue;

    @Override
    protected void onStart() {
        queue = (String) this.getJob().getData().get(MessageConsumerRegistry.MESSAGE_QUEUE);
    }

    @Override
    protected void onStop() {
        LOGGER.info("shutting down message consumer.queue:{}", queue);
    }

    @Override
    protected void action() {
        MessageServer server =
                (MessageServer) this.getJob().getData().get(MessageConsumerRegistry.MESSAGE_SERVER);

        switch (server) {
            case Redis:
                initRedisConsumer();
                break;
            case RabbitMQ:
                initRabbitMQConsumer();
                break;
        }
    }

    private void initRedisConsumer() {
        int threads = (int) this.getJob().getData().get(MessageConsumerRegistry.MESSAGE_THREADS);
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isRunning())
                        popRedisMessage(queue);
                }
            });
            thread.setName(getJob().getName() + queue + i);
            thread.start();
        }
    }

    private void popRedisMessage(String queue) {
        // 阻塞等待方式获取redis消息
        // redis返回的是<queue名字, 消息>，因此截取消息
        List<String> redisMessages = jedisClient.brpop(2, queue);
        if (redisMessages == null || redisMessages.size() == 0) {//
            try {
                Thread.sleep(getJob().getInterval());
            } catch (InterruptedException e) {
            }
            return;
        }
        if (redisMessages != null && redisMessages.size() >= 2) {
            processMessages(Arrays.asList(redisMessages.get(1)));
        }
    }

    private void processMessages(List<String> messages) {
        if (messages.size() == 0) {
            return;
        }
        for (String message : messages) {
            try {
                int delay = (int) getJob().getData().get(MessageConsumerRegistry.MESSAGE_DELAY);
                if (delay > 0) {// 延迟执行，如果消息量很大，会造成消息堆积
                    Thread.sleep(delay);
                }
                getMethod().invoke(getInstance(), message);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 初始化MQ消费者
     */
    private void initRabbitMQConsumer() {
        if (this.getJob().getIsHAEnable()) {
            // 当MQ宕机，关闭zk，让备机马上加入任务
            mqConnection.addShutdownListener(new ShutdownListener() {
                @Override
                public void shutdownCompleted(ShutdownSignalException cause) {
                    this.notifyAll();
                }
            });
        }
        final Channel channel;
        try {
            channel = mqConnection.createChannel();
            channel.basicQos(1);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    processMessages(Arrays.asList(message));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        try {
            channel.basicConsume(queue, false, consumer);
            LOGGER.info("Consuming RabbitMQ queue[{}]...", queue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
