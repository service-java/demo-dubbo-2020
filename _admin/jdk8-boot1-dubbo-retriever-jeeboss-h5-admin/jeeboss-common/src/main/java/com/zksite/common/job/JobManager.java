package com.zksite.common.job;

import org.quartz.Scheduler;

import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;


public interface JobManager {

    /**
     * 添加job，并监控job的状态变化
     * 
     * @param job
     * @param scheduler
     */
    void addJob(AbstractJob job, Scheduler scheduler);

    /**
     * 添加一个job
     * 
     * @param job
     */
    void addJob(AbstractJob job);

    /**
     * 暂停job
     * 
     * @param jobInfo
     */
    void pause(JobInfo jobInfo);

    /**
     * 删除一个JOB<br/>
     * 若果此JOB是一个HA JOB，此操作会触发主备切换
     * 
     * @param jobInfo
     */
    void delete(JobInfo jobInfo);

    /**
     * 删除job,指定是否主备切换
     * 
     * @param jobInfo
     * @param isSwitch
     */
    void delete(JobInfo jobInfo, boolean isSwitch);

    /**
     * 恢复JOB
     * 
     * @param jobInfo
     */
    void resume(JobInfo jobInfo);

    /**
     * 更新job信息
     * 
     * @param jobInfo
     */
    void update(JobInfo jobInfo);

    Page<JobInfo> list(Page<JobInfo> page) throws Exception;

    /**
     * 更新job状态
     * 
     * @param jobInfo
     * @throws Exception 
     */
    void updateStatus(JobInfo jobInfo) throws Exception;

}
