package com.zksite.common.message;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.zksite.common.job.AbstractAnnotationRegistry;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.message.annotation.MqttConsumer;

@Component
public class MqttConsumerRegistry extends AbstractAnnotationRegistry<MqttConsumer, MqttConsumerJob> {

	public static final String TOPIC = "queue";
	public static final String QOS = "QOS";

	private static final String JOB_GROUP = "mqtt_consumer";

	private static final String CLIENT_CONSUMER_SUFFIX = "_consumer";

	@Override
	public JobInfo generateJobInfo(Method method) {
		MqttConsumer mqttConsumer = method.getAnnotation(MqttConsumer.class);
		JobInfo job = new JobInfo();
		job.setName(method.getName() + mqttConsumer.topic() + CLIENT_CONSUMER_SUFFIX);
		job.setGroup(JOB_GROUP);
		job.setInterval(5);
		job.setTimeUnit(TimeUnit.SECONDS);
		job.setRepeat(0);
		job.setIsHAEnable(mqttConsumer.haEnable());
		job.setIsHAStandby(mqttConsumer.haStandyby());
		job.getData().put(TOPIC, mqttConsumer.topic());
		job.getData().put(QOS, mqttConsumer.qos());
		return job;
	}

}
