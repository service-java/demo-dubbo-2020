package com.zksite.common.job;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.zksite.common.job.annotation.Task;
import com.zksite.common.job.model.JobInfo;

@Component
public class AnnotaionJobRegistry
        extends AbstractAnnotationRegistry<Task, AnnotationJob> {

    /**
     * 生成job信息
     * 
     * @param method
     * @param instanceBeanName
     * @param registry
     * @return
     */
    public JobInfo generateJobInfo(Method method) {
        Task task = method.getAnnotation(Task.class);
        JobInfo job = new JobInfo();
        job.setCron(task.cron());
        job.setGroup(task.group());
        job.setInterval(task.interval());
        job.setIsHAEnable(task.isHAEnable());
        job.setIsHAStandby(task.isHAStandby());
        job.setName(task.name());
        job.setGroup(task.group());
        job.setTimeUnit(task.timeUnit());
        job.setRepeat(task.repeat());
        return job;
    }


}

