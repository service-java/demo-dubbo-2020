package com.zksite.web.common.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.zksite.common.aop.aspect.BaseAspect;
import com.zksite.web.common.monitor.annotation.Meter;
import com.zksite.web.common.monitor.entity.MeterEntity;

@Aspect
public class MonitorAspect extends BaseAspect {

    @Pointcut("@annotation(com.zksite.web.common.monitor.annotation.Histogram) || @annotation(com.zksite.web.common.monitor.annotation.Meter)")
    public void monitor() {};

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private MonitorConfigurer monitorConfigurer;

    private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP

    private Map<String, com.codahale.metrics.Meter> meterContainer = new ConcurrentHashMap<>();
    private Map<String, com.codahale.metrics.Histogram> histogramContainer =
            new ConcurrentHashMap<>();


    public static final String METER_SUFFIX = "[meter]";

    public static final String HISTOGRAM_SUFFIX = "[histogram]";


    @Around(value = "monitor()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        RequestMapping methodMapping = getAnnotation(pjp, RequestMapping.class);
        if (methodMapping != null) {// 只有当是一个接口时才做处理
            RequestMapping mapping = getObjectRequestMapping(pjp);
            String name = getUrl(methodMapping, mapping);
            return stat(pjp, name);
        }
        return pjp.proceed();
    }

    private Object stat(ProceedingJoinPoint pjp, String name) throws Throwable {
        Meter meter = getAnnotation(pjp, Meter.class);
        if (meter != null) {
            getMeter(name).mark();// 统计速率
        }
        com.codahale.metrics.Histogram histogram = getHistogram(name);
        long start = System.currentTimeMillis();
        Object object = pjp.proceed();
        long end = System.currentTimeMillis();
        int time = (int) (end - start);
        if (histogram != null) {
            histogram.update(time);
        }
        return object;
    }


    private com.codahale.metrics.Histogram getHistogram(String name) {
        String newName = name + HISTOGRAM_SUFFIX;
        com.codahale.metrics.Histogram histogram = histogramContainer.get(newName);
        if (histogram != null) {
            return histogram;
        }
        Histogram h = metricRegistry.histogram(newName);
        histogramContainer.put(name, h);
        return h;
    }

    private com.codahale.metrics.Meter getMeter(String apiname) {
        String newName = apiname + METER_SUFFIX;
        com.codahale.metrics.Meter m = meterContainer.get(newName);
        if (m != null) {
            return m;
        }
        MeterEntity lastMeter = metricsRepository.getLastMeter(monitorConfigurer.getApplication(),
                LOCAL_IP, apiname);
        com.codahale.metrics.Meter meter = metricRegistry.meter(newName);
        if (lastMeter != null) {
            meter.mark(lastMeter.getCount());
        }
        meterContainer.put(newName, meter);
        return meter;

    }

    private RequestMapping getObjectRequestMapping(ProceedingJoinPoint pjp) {
        Object target = pjp.getTarget();
        if (target != null) {
            return target.getClass().getAnnotation(RequestMapping.class);
        }
        return null;
    }

    private String getUrl(RequestMapping methodMapping, RequestMapping objMapping) {
        StringBuilder finalUrl = new StringBuilder();
        for (RequestMethod method : methodMapping.method()) {
            finalUrl.append("[").append(method.name()).append("]");
        }
        for (RequestMethod method : objMapping.method()) {
            finalUrl.append("[").append(method.name()).append("]");
        }
        if (objMapping != null) {
            String url = getMappingUrl(objMapping);
            if (url != null) {
                if (url.indexOf("/") != 0) {
                    finalUrl.append("/").append(url);
                } else {
                    finalUrl.append(url);
                }
            }
        }
        String url = getMappingUrl(methodMapping);
        if (url != null) {
            if (url.indexOf("/") != 0) {
                finalUrl.append("/").append(url);
            } else {
                finalUrl.append(url);
            }
        }

        return finalUrl.toString();
    }

    private String getMappingUrl(RequestMapping mapping) {
        String[] value = mapping.value();
        if (value.length > 0) {// 只取第一个mapping url
            return value[0];
        }
        return null;
    }
}

