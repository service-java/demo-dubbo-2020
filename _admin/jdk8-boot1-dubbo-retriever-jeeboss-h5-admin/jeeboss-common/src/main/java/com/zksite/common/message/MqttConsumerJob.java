package com.zksite.common.message;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.zksite.common.job.AnnotationJob;

/**
 * mqtt 消费者<br>
 * <strong>注意</strong><br>
 * 如果在处理消息向上层抛出异常，mqtt client会断开链接。
 * 
 * @author hanjieHu
 *
 */
public class MqttConsumerJob extends AnnotationJob {

	private MqttClient mqttClient;

	private String topic;

	private int qos;

	@Autowired
	private Environment environment;

	@Autowired
	private MqttConnectOptions connOpts;

	private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP
	private static final String CLIENT_CONSUMER_SUFFIX = "_" + LOCAL_IP + "_consumer";

	@Override
	protected void action() {
		String userName = environment.getProperty("mqtt_user");
		String password = environment.getProperty("mqtt_pwd");
		String broker = environment.getProperty("mqtt_broker");
		if (StringUtils.isBlank(password) || StringUtils.isBlank(userName) || StringUtils.isBlank(broker)) {
			throw new IllegalStateException("mqtt client is null");
		}
		MemoryPersistence persistence = new MemoryPersistence();
		try {
			mqttClient = new MqttClient(broker, topic + getMethod().getName() + CLIENT_CONSUMER_SUFFIX, persistence);
			mqttClient.connect(connOpts);
			mqttClient.setCallback(new MqttCallback() {

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					try {
						getMethod().invoke(getInstance(), topic, new String(message.getPayload()));
					} catch (Exception e) {// 当消费者抛出异常时，会导致client断开链接
						LOGGER.error(e.getMessage(), e);
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}

				@Override
				public void connectionLost(Throwable cause) {

				}
			});
			int[] qoss = { qos };
			String[] topics = { topic };
			mqttClient.subscribe(topics, qoss);
			LOGGER.info("subscribe mqtt message topic[{}],qos[{}]", topic, qos);
		} catch (MqttException e) {
			LOGGER.error("init MqttConsumer error.{}", e);
		}
	}

	@Override
	protected void onStart() {
		topic = (String) getJob().getData().get(MqttConsumerRegistry.TOPIC);
		qos = (Integer) getJob().getData().get(MqttConsumerRegistry.QOS);
	}

	@Override
	protected void onStop() {
		if (mqttClient != null) {
			try {
				mqttClient.disconnect();
			} catch (MqttException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

}
