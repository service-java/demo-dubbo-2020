package com.reger.test.user;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.alibaba.dubbo.config.ServiceConfig;
import com.reger.dubbo.rpc.filter.Utils;
import com.reger.test.user.model.User;
import com.reger.test.user.service.UserService;

@EnableJpaRepositories(basePackages="com.reger.test.user.dao")
@SpringBootApplication
public class DemoJpaApplication implements CommandLineRunner, DisposableBean{

	private final Logger log = LoggerFactory.getLogger(DemoJpaApplication.class);

	private final static CountDownLatch latch = new CountDownLatch(1);
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws Exception {
		Utils.register(RuntimeException.class); // 注册允许传递的异常类型
		context = SpringApplication.run(DemoJpaApplication.class, args);
		latch.await();
	}

	@Override
	public void destroy() throws Exception {
		latch.countDown();
		context.close();
		log.info("服务提供者关闭------>>服务关闭");
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("服务提供者启动完毕------>>启动完毕");
		this.testJpa();
	}
	
	@Autowired UserService userService;
	private void testJpa() {
		userService.delAll();
		userService.save("张三", "张三的描述");
		List<User> users = userService.findAll();
		System.err.println(users);
	}
}

