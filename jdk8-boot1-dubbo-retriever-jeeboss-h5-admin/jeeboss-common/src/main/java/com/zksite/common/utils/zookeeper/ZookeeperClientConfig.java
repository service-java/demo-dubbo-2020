package com.zksite.common.utils.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.common.utils.NetUtils;

/**
 * ZookeeperClient配置类<br/>
 * 若配置了zk地址，则将注册ZookeeperClient类到Spring容器中
 * 
 *
 */
@Configuration
@PropertySources(value = {
        @PropertySource(value = "classpath:${spring.profiles.active}/application.properties")})
public class ZookeeperClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperClientConfig.class);

    @Autowired
    private Environment env;

    private ZookeeperClient zookeeperClient;

    @Bean(name = "zookeeperClient")
    public ZookeeperClient getZookeeperClient() {
        if (env.containsProperty("zookeeper_address")) {
            String address = env.getProperty("zookeeper_address");
            zookeeperClient = new ZookeeperClient(address);
            logger.info("ZookeeperClient is connecting to {}...", address);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        zookeeperClient.close();
                    } catch (IOException e) {
                    }
                    logger.info("ZookeeperClient closed.");
                }
            });
        }

        return zookeeperClient;
    }

    public static void main(String[] args) throws Exception {
        ZookeeperClient client = new ZookeeperClient("127.0.0.1:2181");
        String create = client.create("/test/1234", "test", CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(create);
        client.close();
    }

}
