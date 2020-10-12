package com.zksite.common.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zksite.common.aop.annotation.CentralConfiguration;
import com.zksite.common.utils.zookeeper.ZookeeperClient;
import com.zksite.common.utils.zookeeper.ZookeeperClient.ZKNodeListener;

/**
 * Spring Bean加载处理器<br/>
 * 用于扫描添加了@CentralConfiguration注解的public字段或方法，监听配置中心(zookeeper)数据变化并更新至字段/调用方法
 * 
 *
 */
@Component
@Order(10)
public class CentralConfigurationBeanPostProcessor implements BeanPostProcessor {

    private static Logger logger =
            LoggerFactory.getLogger(CentralConfigurationBeanPostProcessor.class);

    @Autowired
    private ZookeeperClient zookeeperClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        processCentralConfigurationFields(bean);
        processCentralConfigurationMethods(bean);
        return bean;
    }

    private void processCentralConfigurationFields(final Object bean) {
        Field[] fields = bean.getClass().getFields();
        for (final Field field : fields) {
            // 处理添加了@CentralConfiguration注解的字段
            if (field.isAnnotationPresent(CentralConfiguration.class)) {
                final CentralConfiguration centralConfiguration =
                        field.getAnnotation(CentralConfiguration.class);

                logger.info("Watching central configuration [{}] on [{}.{}]...",
                        centralConfiguration.key(), bean.getClass().getSimpleName(),
                        field.getName());

                try {
                    zookeeperClient.watchNode(centralConfiguration.key(),
                            centralConfiguration.defaultValue(), new ZKNodeListener() {
                                @Override
                                public void onChange(String data, NodeCache nodeCache) {
                                    // 获取值后，设置到bean的字段中
                                    setValueToField(bean, field, data);
                                }
                            });
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(
                            "Failed to load configuration [" + centralConfiguration.key()
                                    + "] for [" + bean.getClass().getSimpleName() + "."
                                    + field.getName() + "]: " + e.getMessage());
                }
            }
        }
    }

    private void processCentralConfigurationMethods(final Object bean) {
        Method[] methods = bean.getClass().getMethods();
        for (final Method method : methods) {
            // 处理添加了@CentralConfiguration注解的方法
            if (method.isAnnotationPresent(CentralConfiguration.class)) {
                final CentralConfiguration centralConfiguration =
                        method.getAnnotation(CentralConfiguration.class);

                logger.info("Watching central configuration [{}] on [{}.{}(String)]...",
                        centralConfiguration.key(), bean.getClass().getSimpleName(),
                        method.getName());

                try {
                    zookeeperClient.watchNode(centralConfiguration.key(),
                            centralConfiguration.defaultValue(), new ZKNodeListener() {
                                @Override
                                public void onChange(String data, NodeCache nodeCache) {
                                    try {
                                        Class<?>[] parameterTypes = method.getParameterTypes();
                                        if (parameterTypes.length == 1) {
                                            Object value = getValue(parameterTypes[0], data);
                                            method.invoke(bean, value);
                                        }
                                    } catch (IllegalAccessException | IllegalArgumentException
                                            | InvocationTargetException e) {
                                        logger.error(e.getMessage(), e);
                                        throw new RuntimeException(e);
                                    }
                                }

                            });
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(
                            "Failed to load configuration [" + centralConfiguration.key()
                                    + "] for [" + bean.getClass().getSimpleName() + "."
                                    + method.getName() + "(String)]: " + e.getMessage());
                }
            }
        }
    }

    private Object getValue(Class<?> type, String value) {
        String name = type.getName();
        switch (name) {
            case "java.lang.String":
                return value;
            case "int":
                return StringUtils.isBlank(value) ? 0 : Integer.parseInt(value);
            case "java.lang.Integer":
                return StringUtils.isBlank(value) ? null : Integer.valueOf(value);
            case "long":
                return StringUtils.isBlank(value) ? 0L : Long.parseLong(value);
            case "java.lang.Long":
                return StringUtils.isBlank(value) ? null : Long.valueOf(value);
            case "double":
                return StringUtils.isBlank(value) ? 0.0 : Double.parseDouble(value);
            case "java.lang.Double":
                return StringUtils.isBlank(value) ? null : Double.valueOf(value);
            case "float":
                return StringUtils.isBlank(value) ? 0.0 : Float.parseFloat(value);
            case "java.lang.Float":
                return StringUtils.isBlank(value) ? null : Float.valueOf(value);
            case "boolean":
                return StringUtils.isBlank(value) ? false : Boolean.parseBoolean(value);
            case "java.lang.Boolean":
                return StringUtils.isBlank(value) ? null : Boolean.valueOf(value);
            case "com.alibaba.fastjson.JSONObject":
                return StringUtils.isBlank(value) ? null : JSONObject.parse(value);
            case "com.alibaba.fastjson.JSONArray":
                return StringUtils.isBlank(value) ? null : JSONArray.parse(value);
            // support List since 20161124
            case "java.util.List":
            case "java.util.ArrayList":
                return StringUtils.isBlank(value) ? Collections.emptyList()
                        : Arrays.asList(value.split(","));
            default:
                return null;
        }
    }

    private void setValueToField(Object bean, Field field, String value) {
        if (logger.isDebugEnabled()) {
            logger.debug("Type of {}.{} is {}.", bean.getClass().getSimpleName(), field.getName(),
                    field.getType().getName());
        }

        Object typeValue = getValue(field.getType(), value);
        try {
            field.set(bean, typeValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        String maskedValue = value == null ? "null"
                : (value.length() <= 3
                        ? (value.substring(0, 1) + "***"
                                + value.substring(value.length() - 1, value.length()))
                        : (value.substring(0, 2) + "***"
                                + value.substring(value.length() - 1, value.length())));
        logger.info("Set value [{}] to [{}.{}].", maskedValue, bean.getClass().getSimpleName(),
                field.getName());
    }

}
