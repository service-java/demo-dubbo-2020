package com.reger.test.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.reger.dubbo.annotation.Inject;
import com.reger.test.user.model.User;
import com.reger.test.user.service.UserService;

@SpringBootApplication(scanBasePackages = "com.reger.test.consumer")
public class DubboLeaderApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DubboLeaderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DubboLeaderApplication.class, args) ;
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("服务消费者启动完毕------>>启动完毕");
		
		this.testJpa();
	}

	@Inject UserService userService;

	private void testJpa() {
		userService.delAll();
		log.info("清理用户数据完毕");
		User user1 = userService.save("张三1", "张三的描述");
		log.info("保存用户数据完毕 {}",user1);
		User user2 = userService.save("张三2", "张三2的描述");
		log.info("保存用户数据完毕 {}",user2);
		List<User> users = userService.findAll();
		log.info("查询得到的用户数据列表 {}",users);
	}
}
