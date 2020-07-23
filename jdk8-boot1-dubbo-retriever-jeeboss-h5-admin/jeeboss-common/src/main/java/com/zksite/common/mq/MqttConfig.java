package com.zksite.common.mq;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.common.utils.NetUtils;

@Configuration
public class MqttConfig implements DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

	@Autowired
	private Environment environment;

	private MqttPublisher mqttPublisher;

	private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP

	private static final String CLIENT_PUBLISHER_SUFFIX = "_" + LOCAL_IP + "_publisher";

	@Bean
	public MqttConnectOptions getMqttConnectOptions() {
		String userName = environment.getProperty("mqtt_user");
		String password = environment.getProperty("mqtt_pwd");
		String broker = environment.getProperty("mqtt_broker");
		String timeout = environment.getProperty("mqtt_timeout", "30");
		String keepAliveInterval = environment.getProperty("mqtt_keep_alive_interval", "20");
		if (StringUtils.isBlank(password) || StringUtils.isBlank(userName) || StringUtils.isBlank(broker)) {
			return null;
		}
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());
		connOpts.setConnectionTimeout(Integer.valueOf(timeout));
		connOpts.setKeepAliveInterval(Integer.valueOf(keepAliveInterval));
		return connOpts;
	}

	@Bean
	public MqttPublisher getMqttPublisher() {
		String application_name = environment.getProperty("application_name");
		MemoryPersistence persistence = new MemoryPersistence();
		String broker = environment.getProperty("mqtt_broker");
		if (StringUtils.isBlank(broker)) {
			return null;
		}
		try {
			mqttPublisher = new MqttPublisher(broker, application_name + CLIENT_PUBLISHER_SUFFIX, persistence);
			mqttPublisher.connect(getMqttConnectOptions());
			LOGGER.info("init MqttPublisher success:[{}]", broker);
			return mqttPublisher;
		} catch (MqttException e) {
			LOGGER.error("init MqttPublisher error.{}", e);
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		if (mqttPublisher != null) {
			mqttPublisher.disconnect();
		}
	}
}
