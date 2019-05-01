package com.zksite.web.common.monitor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

public class MonitorConfigurer
        implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ApplicationEvent> {
    ConsoleReporter reporter;

    private String application;

    private Class<? extends MetricsRepository> metricsRepository;

    private int interval;// 数据采集间隔

    private TimeUnit timeUnit;

    private String timeUnitClass;


    @Bean
    public MetricRegistry createRegistry() {
        MetricRegistry metricRegistry = new MetricRegistry();
        reporter = ConsoleReporter.forRegistry(metricRegistry).build();
        return metricRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        GenericBeanDefinition metricMonitorAspectDefinition = new GenericBeanDefinition();
        metricMonitorAspectDefinition.setBeanClass(MonitorAspect.class);
        metricMonitorAspectDefinition.setScope("singleton");
        metricMonitorAspectDefinition.setLazyInit(false);
        metricMonitorAspectDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder metricMonitorAspectHolder = new BeanDefinitionHolder(
                metricMonitorAspectDefinition, MonitorAspect.class.getName());

        GenericBeanDefinition metricsRepositoryDefinition = new GenericBeanDefinition();
        metricsRepositoryDefinition.setBeanClass(metricsRepository);
        metricsRepositoryDefinition.setScope("singleton");
        metricsRepositoryDefinition.setLazyInit(false);
        metricsRepositoryDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder metricsRepositoryHolder =
                new BeanDefinitionHolder(metricsRepositoryDefinition, metricsRepository.getName());

        GenericBeanDefinition monitorJobDefinition = new GenericBeanDefinition();
        monitorJobDefinition.setBeanClass(MonitorJob.class);
        monitorJobDefinition.setScope("singleton");
        monitorJobDefinition.setLazyInit(false);
        monitorJobDefinition.setAutowireCandidate(true);
        BeanDefinitionHolder monitorJobHolder =
                new BeanDefinitionHolder(monitorJobDefinition, MonitorJob.class.getName());

        BeanDefinitionReaderUtils.registerBeanDefinition(metricMonitorAspectHolder, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(metricsRepositoryHolder, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(monitorJobHolder, registry);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ApplicationContext source = (ApplicationContext) event.getSource();
        if (ContextRefreshedEvent.class.getName().equals(event.getClass().getName())
                && source.getParent() == null) {
            //reporter.start(3, TimeUnit.SECONDS);
        }

    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Class<? extends MetricsRepository> getMetricsRepository() {
        return metricsRepository;
    }

    public void setMetricsRepository(Class<? extends MetricsRepository> metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getTimeUnitClass() {
        return timeUnitClass;
    }

    public void setTimeUnitClass(String timeUnitClass) {
        switch (timeUnitClass) {
            case "java.util.concurrent.TimeUnit.DAYS":
                timeUnit = TimeUnit.DAYS;
                break;
            case "java.util.concurrent.TimeUnit.HOURS":
                timeUnit = TimeUnit.HOURS;
                break;
            case "java.util.concurrent.TimeUnit.MICROSECONDS":
                timeUnit = TimeUnit.MICROSECONDS;
                break;
            case "java.util.concurrent.TimeUnit.MILLISECONDS":
                timeUnit = TimeUnit.MILLISECONDS;
                break;
            case "java.util.concurrent.TimeUnit.MINUTES":
                timeUnit = TimeUnit.MINUTES;
                break;
            case "java.util.concurrent.TimeUnit.NANOSECONDS":
                timeUnit = TimeUnit.NANOSECONDS;
                break;
            case "java.util.concurrent.TimeUnit.SECONDS":
                timeUnit = TimeUnit.SECONDS;
                break;
        }
        this.timeUnitClass = timeUnitClass;
    }
}
