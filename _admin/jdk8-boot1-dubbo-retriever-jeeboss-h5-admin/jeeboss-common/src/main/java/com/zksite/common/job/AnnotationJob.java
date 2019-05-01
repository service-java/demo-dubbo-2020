package com.zksite.common.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zksite.common.job.model.JobInfo;

public class AnnotationJob extends AbstractJob {

    public final Logger LOGGER = LoggerFactory.getLogger(getClass());


    /**
     * 执行的bean
     */
    private Object instance;

    private JobInfo job;

    /**
     * 定时任务方法
     */
    private Method method;

    @Override
    public JobInfo generateJob() {
        return job;
    }

    @Override
    protected void action() {
        try {
            LOGGER.debug("execute annotaion job.job name:{},job group:{}", job.getName(),
                    job.getGroup());
            method.invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public JobInfo getJob() {
        return job;
    }

    public void setJob(JobInfo job) {
        this.job = job;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
