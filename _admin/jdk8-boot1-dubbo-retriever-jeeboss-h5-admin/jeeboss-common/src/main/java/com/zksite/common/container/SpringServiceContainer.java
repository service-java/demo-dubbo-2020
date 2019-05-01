package com.zksite.common.container;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServiceContainer {

    private static Logger logger = LoggerFactory.getLogger(SpringServiceContainer.class);

    private static final String SPRING_CONFIG = "spring-context-all.xml";

    private static ClassPathXmlApplicationContext applicationContext;

    public static void main(String[] args) {
        System.setProperty("dubbo.application.logger", "slf4j"); // 指定dubbo日志
        applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG);
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    logger.info("shutting down services...");
                    SpringServiceContainer.class.notify();
                }

            });
            synchronized (SpringServiceContainer.class) {
                String port = System.getProperty("dubbo.protocol.port");
                if (StringUtils.isBlank(port)) {
                    port = applicationContext.getEnvironment().getProperty("dubbo.port");
                }
                if (StringUtils.isNotBlank(port)) {
                    logger.info("start dubbo services on port {} success...", port);
                } else {
                    logger.info("start services success...");
                }
                SpringServiceContainer.class.wait();
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        applicationContext.close();
    }

}
