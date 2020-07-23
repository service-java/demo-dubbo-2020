package com.zksite.common.message;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.zksite.common.job.AbstractAnnotationRegistry;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.message.annotation.MessageConsumer;

@Component
public class MessageConsumerRegistry extends AbstractAnnotationRegistry<MessageConsumer, MessageConsumerJob> {

	private static final String QUEUE_GROUP = "message";

	public static final String MESSAGE_SERVER = "message_server";

	public static final String MESSAGE_QUEUE = "message_queue";

	public static final String MESSAGE_THREADS = "message_threads";

	public static final String MESSAGE_DELAY = "message_delay";

	public static final String JOB_NAME_SUFFIX = "_message_consumer";

	@Override
	public JobInfo generateJobInfo(Method method) {
		MessageConsumer messageConsumer = method.getAnnotation(MessageConsumer.class);
		JobInfo job = new JobInfo();
		job.setIsHAEnable(messageConsumer.haEnable());
		job.setIsHAStandby(messageConsumer.haStandyby());
		job.setInterval(messageConsumer.interval());
		job.setName(messageConsumer.queue().replaceAll(":", "-") + method.getName() + JOB_NAME_SUFFIX);
		job.setGroup(QUEUE_GROUP);
		job.setRepeat(0);
		job.setTimeUnit(TimeUnit.SECONDS);
		job.getData().put(MESSAGE_SERVER, messageConsumer.server());
		job.getData().put(MESSAGE_QUEUE, messageConsumer.queue());
		job.getData().put(MESSAGE_THREADS, messageConsumer.threads());
		job.getData().put(MESSAGE_DELAY, messageConsumer.delay());
		return job;
	}

}
