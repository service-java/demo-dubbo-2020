package com.zksite.common.job;

import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;

@Component
public class SchedulerListener extends SchedulerListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerListener.class);

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void schedulerShutdown() {
        // 当scheduler关闭完成时，将持久化的信息删除
        Page<JobInfo> page = jobRepository.list(null);
        LOGGER.info("deleting job info.....");
        for (JobInfo job : page.getList()) {
            jobRepository.delete(job);
        }
    }


}
