package com.alibaba.dubbo.samples.echo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class AnnotationProvider {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        System.in.read();
    }

    @Configuration
    // #1 指定扫描服务的位置
    @EnableDubbo(scanBasePackages = "com.alibaba.dubbo.samples.echo")
    static class ProviderConfiguration {
        @Bean
        public ProviderConfig providerConfig() {
            return new ProviderConfig();
        }

        @Bean
        public ApplicationConfig applicationConfig() {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("echo-annotation-provider");
            return applicationConfig;
        }

        @Bean
        public RegistryConfig registryConfig() {
            RegistryConfig registryConfig = new RegistryConfig();
            // #2 使用zookeeper作为注册中心，同时给出注册中心ip和端口
            registryConfig.setProtocol("zookeeper");
            registryConfig.setAddress("localhost");
            registryConfig.setPort(2181);
            return registryConfig;
        }

        @Bean
        public ProtocolConfig protocolConfig() {
            ProtocolConfig protocolConfig = new ProtocolConfig();
            // #3 默认服务使用dubbo协议，在20880监听服务
            protocolConfig.setName("dubbo");
            protocolConfig.setPort(20880);
            return protocolConfig;
        }
    }
}
