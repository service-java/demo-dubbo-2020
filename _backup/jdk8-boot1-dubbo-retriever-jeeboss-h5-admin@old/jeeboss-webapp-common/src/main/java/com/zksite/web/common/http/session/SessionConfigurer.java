package com.zksite.web.common.http.session;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自定义会话配置配置
 * 
 * @author hanjieHu
 *
 */
public class SessionConfigurer implements BeanDefinitionRegistryPostProcessor {

    /**
     * session管理器
     */
    private Class<?> sessionManagerClass;

    /**
     * session仓库
     */
    private Class<?> sessionRepositoryClass;

    /**
     * sessionId策略
     */
    private Class<?> httpSessionStrategyClass;


    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        // 创建session管理器
        GenericBeanDefinition sessionManagerDefinition = new GenericBeanDefinition();
        sessionManagerDefinition.setBeanClass(sessionManagerClass);
        sessionManagerDefinition.setScope("singleton");
        sessionManagerDefinition.setLazyInit(false);
        sessionManagerDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder sessionManagerHolder =
                new BeanDefinitionHolder(sessionManagerDefinition, sessionManagerClass.getName());
        // 创建session仓库
        GenericBeanDefinition sessionRepositoryDefinition = new GenericBeanDefinition();
        sessionRepositoryDefinition.setBeanClass(sessionRepositoryClass);
        sessionRepositoryDefinition.setScope("singleton");
        sessionRepositoryDefinition.setLazyInit(false);
        sessionRepositoryDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder sessionRepositoryHolder = new BeanDefinitionHolder(
                sessionRepositoryDefinition, sessionRepositoryClass.getName());
        // 创建获取sessionId策略
        GenericBeanDefinition httpSessionStrategyDefinition = new GenericBeanDefinition();
        httpSessionStrategyDefinition.setBeanClass(httpSessionStrategyClass);
        httpSessionStrategyDefinition.setScope("singleton");
        httpSessionStrategyDefinition.setLazyInit(false);
        httpSessionStrategyDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder httpSessionStrategyHolder = new BeanDefinitionHolder(
                httpSessionStrategyDefinition, httpSessionStrategyClass.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(sessionManagerHolder, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(sessionRepositoryHolder, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(httpSessionStrategyHolder, registry);
    }


    @Bean
    public RedisTemplate<String, RedisSession> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisSession> template = new RedisTemplate<String, RedisSession>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    public Class<?> getSessionManagerClass() {
        return sessionManagerClass;
    }

    public void setSessionManagerClass(Class<?> sessionManagerClass) {
        this.sessionManagerClass = sessionManagerClass;
    }

    public Class<?> getSessionRepositoryClass() {
        return sessionRepositoryClass;
    }

    public void setSessionRepositoryClass(Class<?> sessionRepositoryClass) {
        this.sessionRepositoryClass = sessionRepositoryClass;
    }

    public Class<?> getHttpSessionStrategyClass() {
        return httpSessionStrategyClass;
    }

    public void setHttpSessionStrategyClass(Class<?> httpSessionStrategyClass) {
        this.httpSessionStrategyClass = httpSessionStrategyClass;
    }

}
