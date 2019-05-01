package com.zksite.common.mq;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

@Configuration
public class MqConfig implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MqConfig.class);

    @Autowired
    private Environment env;

    private boolean isInit = false;

    private Address[] mqAddresses;

    private ConnectionFactory mqConnectionFactory;

    private Connection mqConnection;

    private Channel mqChannel;

    private synchronized void init() {
        if (!isInit) {
            isInit = true;

            mqConnectionFactory = initConnectionFactory();

            if (this.mqConnectionFactory != null) {
                try {
                    this.mqConnection = mqConnectionFactory.newConnection(mqAddresses);
                    this.mqConnection.addShutdownListener(new ShutdownListener() {
                        @Override
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            logger.warn("MQ Connection shutdown.");
                        }
                    });
                    this.mqChannel = mqConnection.createChannel();
                    this.mqChannel.basicQos(1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Bean(name = "mqConnection")
    public Connection getMqConnection() {
        init();

        return this.mqConnection;
    }

    @Bean(name = "mqChannel")
    public Channel getMqChannel() {
        init();

        return this.mqChannel;
    }

    private ConnectionFactory initConnectionFactory() {
        String mqAddress = env.getProperty("mq_address", "");
        if (StringUtils.isEmpty(mqAddress)) {
            return null;
        }
        this.mqAddresses = Address.parseAddresses(mqAddress);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(env.getProperty("mq_vhost", ConnectionFactory.DEFAULT_VHOST));
        factory.setUsername(env.getProperty("mq_user", ""));
        factory.setPassword(env.getProperty("mq_password", ""));

        factory.setAutomaticRecoveryEnabled(true);
        factory.setConnectionTimeout(Integer.parseInt(env.getProperty("mq_timeout",
                String.valueOf(ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT))));


        logger.info("Initialized MQ connection factory [{}].", mqAddress);
        return factory;
    }

    @Override
    public void destroy() throws Exception {}

}
