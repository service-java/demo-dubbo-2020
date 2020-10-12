package com.alibaba.dubbo.samples.echo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.samples.echo.api.EchoService;

/**
 * @author yiji@apache.org
 */
public class EchoConsumer {
    public static void main(String[] args) {
        ReferenceConfig<EchoService> reference = new ReferenceConfig<>();
        // #1 设置消费方应用名称
        reference.setApplication(new ApplicationConfig("java-echo-consumer"));
        // #2 设置注册中心地址和协议
        reference.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        // #3 指定要消费的服务接口
        reference.setInterface(EchoService.class);
        // #4 创建远程连接并做动态代理转换
        EchoService greetingsService = reference.get();
        String message = greetingsService.echo("Hello world!");
        System.out.println(message);
    }
}
