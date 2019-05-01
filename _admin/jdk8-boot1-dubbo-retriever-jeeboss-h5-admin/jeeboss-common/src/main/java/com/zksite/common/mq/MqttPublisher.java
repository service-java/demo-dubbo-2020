package com.zksite.common.mq;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttPublisher extends MqttClient{

	public MqttPublisher(String serverURI, String clientId) throws MqttException {
		super(serverURI, clientId);
	}

	public MqttPublisher(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(serverURI, clientId, persistence);
	}

}
