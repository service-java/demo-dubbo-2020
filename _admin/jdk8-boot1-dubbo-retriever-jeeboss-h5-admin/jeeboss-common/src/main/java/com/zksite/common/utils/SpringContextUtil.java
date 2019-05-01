package com.zksite.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 
 * 获取spring容器，以访问容器中定义的其他bean
 * 
 */
@Service
public class SpringContextUtil implements ApplicationContextAware {

	// Spring应用上下文环境
	private static ApplicationContext applicationContext;

	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境
	 * 
	 * @param applicationContext
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtil.applicationContext = applicationContext;
	}

	/**
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获取对象 这里重写了bean方法，起主要作用
	 * 
	 * @param name
	 * @return Object 一个以所给名字注册的bean的实例
	 * @throws BeansException
	 */
	public static Object getBean(String name) {
		try {
			return applicationContext.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	/**
	 * 根据类型获取对象
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	public static <T> T getBean(Class<T> name) {
		try {
			return (T) applicationContext.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

}