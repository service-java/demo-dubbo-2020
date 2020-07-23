package com.reger.test.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.reger.dubbo.annotation.Inject;
import com.test.dubbo.service.MathService;

@SpringBootApplication(scanBasePackages = "com.reger.test.consumer")
public class DubboLeaderApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DubboLeaderApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(DubboLeaderApplication.class, args);
		MathService service= ctx.getBean( MathService.class);
		ctx.close();
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("服务消费者启动完毕------>>启动完毕");
	}

	@Inject
	public MathService bidService;
}
