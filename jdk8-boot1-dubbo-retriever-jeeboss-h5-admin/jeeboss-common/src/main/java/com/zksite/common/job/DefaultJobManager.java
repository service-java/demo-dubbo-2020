package com.zksite.common.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.mybatis.Page;
import com.zksite.common.utils.zookeeper.ZookeeperClient;
import com.zksite.common.utils.zookeeper.ZookeeperClient.ZKNodeListener;

@Order()
@Component
public class DefaultJobManager implements ApplicationListener<ApplicationContextEvent>, JobManager {

    private static final SchedulerFactory SCHEDULER_FACTORY = new StdSchedulerFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJobManager.class);

    @Autowired
    private ZookeeperClient zookeeperClient;

    private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP

    /**
     * 存放zk path
     */
    private Map<String, AbstractJob> container = new ConcurrentHashMap<>();

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SchedulerListener schedulerListener;

    private Scheduler scheduler;

    private static final String STOP_VAR = "N";// 停止任务并切换主备

    private static final String STOP_NOTSWITCH_VAR = "N_NS";// 停止任务不切换主备

    private static final String START_VAR = "Y";//
    private static final String RESUME_VAR = "RESUME";// 重新启动任务

    private static final String PAUSE_VAR = "PAUSE";// 暂停任务

    private static final String JOB_MANAGER_PREFIX = "/job/manager/";


    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        ApplicationContext applicationContext = (ApplicationContext) event.getSource();
        if (applicationContext.getParent() == null) {
            if (event.getClass().equals(ContextClosedEvent.class)) {// 监听spring容器关闭事件，当容器关闭时，关闭sceduler，实现优雅停机
                try {
                    LOGGER.info("scheduler shutting down.....");
                    getScheduler().shutdown();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } else if (event.getClass().equals(ContextRefreshedEvent.class)) {
                try {
                    scheduler = getScheduler();
                    scheduler.start();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }



    /**
     * 监听job的状态，并做相应处理
     * 
     * @param path
     * @throws Exception
     */
    private void addAndWatcherJob(final String path, AbstractJob job) throws Exception {
        String jobPath = zookeeperClient.create(path, START_VAR, CreateMode.EPHEMERAL_SEQUENTIAL);
        container.put(jobPath, job);
        job.getJob().setPath(jobPath);
        job.getJob().setId(jobPath);
        jobRepository.add(job.getJob());
        zookeeperClient.watchNode(jobPath, START_VAR, new ZKNodeListener() {
            @Override
            public void onChange(String data, NodeCache nodeCache) {
                AbstractJob job = container.get(jobPath);
                switch (data) {
                    case STOP_VAR:
                        try {
                            nodeCache.close();
                            zookeeperClient.delete(jobPath);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        delete(job.getJob());
                        break;
                    case STOP_NOTSWITCH_VAR:
                        try {
                            nodeCache.close();
                            zookeeperClient.delete(jobPath);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        delete(job.getJob(), false);
                        break;
                    case RESUME_VAR:
                        resume(job.getJob());
                        break;
                    case PAUSE_VAR:
                        pause(job.getJob());
                        break;
                }
            }
        });
    }


    public synchronized Scheduler getScheduler() {
        if (scheduler == null) {
            try {
                scheduler = SCHEDULER_FACTORY.getScheduler();
                if (!scheduler.isStarted()) {
                    scheduler.start();
                    scheduler.getListenerManager().addSchedulerListener(schedulerListener);
                }
                return scheduler;
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        return scheduler;
    }

    private String getJobPath(String name, String group) {
        if (StringUtils.isBlank(group)) {
            return JOB_MANAGER_PREFIX + name;
        } else {
            return JOB_MANAGER_PREFIX + group + "/" + name;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#addJob(com.zksite.common.task.AbstractJob,
     * org.quartz.Scheduler)
     */
    @Override
    public void addJob(AbstractJob job, Scheduler scheduler) {
        try {
            scheduler.scheduleJob(job.getJobDetail(), job.getTrigger());
            job.getJob().setHost(LOCAL_IP);
            job.getJob().setValue(START_VAR);
            String jobPath = getJobPath(job.getJob().getName(), job.getJob().getGroup());
            addAndWatcherJob(jobPath, job);
        } catch (Exception e) {
            LOGGER.info("add job error.{}", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#addJob(com.zksite.common.task.AbstractJob)
     */
    @Override
    public void addJob(AbstractJob job) {
        Scheduler scheduler = getScheduler();
        addJob(job, scheduler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#pause(java.lang.String, java.lang.String)
     */
    @Override
    public void pause(JobInfo jobInfo) {
        JobKey jobKey = createJokey(jobInfo.getName(), jobInfo.getGroup());
        try {
            getScheduler().pauseJob(jobKey);
            jobInfo.setValue(PAUSE_VAR);
            jobRepository.update(jobInfo);
        } catch (SchedulerException e) {
            LOGGER.error("pause error.{}", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#delete(java.lang.String, java.lang.String)
     */
    @Override
    public void delete(JobInfo jobInfo) {
        delete(jobInfo, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#delete(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void delete(JobInfo jobInfo, boolean isSwitch) {
        JobKey jobKey = createJokey(jobInfo.getName(), jobInfo.getGroup());
        try {
            getScheduler().deleteJob(jobKey);
            jobRepository.delete(jobInfo);
            LOGGER.info("delete job[group:{},name:{}] switch value:{}", jobInfo.getGroup(),
                    jobInfo.getName(), isSwitch);
            AbstractJob jobBean = container.get(jobInfo.getId());
            if (jobBean != null) {
                jobBean.stop(isSwitch);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#resume(java.lang.String, java.lang.String)
     */
    @Override
    public void resume(JobInfo jobInfo) {
        JobKey jobKey = createJokey(jobInfo.getName(), jobInfo.getGroup());
        try {
            getScheduler().resumeJob(jobKey);
            jobInfo.setValue(START_VAR);
            jobRepository.update(jobInfo);
        } catch (SchedulerException e) {
            LOGGER.error("resume error.{}", e);
        }
    }

    private JobKey createJokey(String name, String group) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException();
        }
        JobKey jobKey = null;
        if (StringUtils.isNotBlank(group)) {
            jobKey = new JobKey(name, group);
        } else {
            jobKey = new JobKey(name);
        }
        return jobKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zksite.common.task.JobManager#list(com.zksite.common.mybatis.Page)
     */
    @Override
    public Page<JobInfo> list(Page<JobInfo> page) throws Exception {
        Page<JobInfo> newPage = jobRepository.list(page);
        boolean flag = false;
        for (JobInfo job : page.getList()) {
            if (!zookeeperClient.exists(job.getPath())) {// 如果有不存在的job，重新获取
                flag = true;
                jobRepository.delete(job);
            }
        }
        if (flag) {
            return list(page);
        }
        return newPage;
    }



    @Override
    public void update(JobInfo jobInfo) {
        jobRepository.update(jobInfo);
    }



    @Override
    public void updateStatus(JobInfo jobInfo) throws Exception {
        zookeeperClient.set(jobInfo.getId(), jobInfo.getValue());
    }


}
