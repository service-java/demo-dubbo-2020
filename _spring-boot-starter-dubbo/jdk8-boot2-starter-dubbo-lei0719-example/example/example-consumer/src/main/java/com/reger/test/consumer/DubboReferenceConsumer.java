package com.reger.test.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.test.dubbo.model.User;
import com.test.dubbo.service.MathService;
import com.test.dubbo.service.MathService2;

@Component
public class DubboReferenceConsumer implements CommandLineRunner {
	@Autowired
	public MathService bidService;
	@Autowired
	public MathService2 bidService2;
	Integer a=1;
	Integer b=2;
	User user = new User(11,"测试","hello word !");

	@Override
	public void run(String... args) {
//		System.err.println("注入的是同一个对象："+bidService.equals(service));
		
		System.err.printf("MathService2 %s+%s=%s", a, b ,  bidService2.add(a, b));
		System.err.println();
		System.err.printf("%s+%s=%s", a, b ,  bidService.add(a, b));
		System.err.println();
		System.err.printf("list=%s", bidService.toList(1, "22", true, 'b' , user));
		System.err.println();
		System.err.println(bidService.getUser(user));
		try {
			bidService.throwThrowable();
		} catch (Exception e) {
			 System.err.println(e.getMessage());
		}
	}


}
