package com.zksite.common.job;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.zksite.common.job.model.JobInfo;
import com.zksite.common.utils.Reflections;

@SuppressWarnings("unchecked")
public abstract class AbstractAnnotationRegistry<T extends Annotation, Y extends AbstractJob>
        implements BeanDefinitionRegistryPostProcessor {

    public Logger logger = LoggerFactory.getLogger(getClass());

    public abstract JobInfo generateJobInfo(Method method);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        String[] names = registry.getBeanDefinitionNames();
        for (String name : names) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(name);
            try {
                registerJobDefinition(beanDefinition, name, registry);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void registerJobDefinition(BeanDefinition beanDefinition, String name,
            BeanDefinitionRegistry registry) throws ClassNotFoundException {
        String className = beanDefinition.getBeanClassName();
        if (StringUtils.isBlank(className)) {
            return;
        }
        List<Method> methods = findMethod(Class.forName(className));
        for (Method method : methods) {
            JobInfo job = generateJobInfo(method);
            BeanDefinitionHolder holder = createBefinition(method, name, job);
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);// 将beanDefinntion注册到容器，由spring初始化然后启动定时任务
        }
    }



    private List<Method> findMethod(Class<?> clazz) {
        Class<? extends Annotation> rawType = Reflections.getClassGenricType(getClass(), 0);
        List<Method> methods = Reflections.findMethodByAnnotation(clazz, rawType);
        return methods;
    }

    private BeanDefinitionHolder createBefinition(Method method, String instanceBeanName, JobInfo job) {
        Class<? extends AbstractJob> jobClass = Reflections.getClassGenricType(getClass(), 1);
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(jobClass); // 设置类
        definition.setScope("singleton"); // 设置scope
        definition.setLazyInit(false); // 设置是否懒加载
        definition.setAutowireCandidate(false); // 设置是否可以被其他对象自动注入
        definition.getPropertyValues().add("instance", new RuntimeBeanReference(instanceBeanName));
        definition.getPropertyValues().add("method", method);
        definition.getPropertyValues().add("job", job);// 注入job
        BeanDefinitionHolder holder =
                new BeanDefinitionHolder(definition, generateJobBeanName(job));
        return holder;
    }

    protected String generateJobBeanName(JobInfo job) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(job.getGroup())) {
            sb.append(job.getGroup()).append(".").append(job.getName());
        } else {
            sb.append(job.getName()).append(".");
        }
        sb.append(job.getName()).append(".")
                .append(UUID.randomUUID().toString().replaceAll("-", ""));
        return sb.toString();
    }
}
