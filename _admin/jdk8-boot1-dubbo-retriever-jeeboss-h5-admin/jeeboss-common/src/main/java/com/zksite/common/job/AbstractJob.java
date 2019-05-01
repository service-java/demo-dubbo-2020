package com.zksite.common.job;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.NotSupportedException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.zksite.common.ha.Condition;
import com.zksite.common.ha.HAService;
import com.zksite.common.ha.HAService.HAExecutor;
import com.zksite.common.ha.HAService.StandbyListener;
import com.zksite.common.job.model.JobInfo;
import com.zksite.common.utils.Reflections;
import com.zksite.common.utils.zookeeper.ZookeeperClient;
import com.zksite.common.utils.zookeeper.ZookeeperClient.ZKChildrenListener;

/**
 * 定时任务bean<br>
 * <br>
 * <strong>支持HA主备切换 </strong>
 * 
 * @author hanjieHu
 *
 */
public abstract class AbstractJob
        implements ApplicationListener<ApplicationContextEvent>, org.quartz.Job, HAExecutor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Condition condition = new Condition();

    private volatile boolean isLeader = false;

    private volatile boolean isRunning;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private ZookeeperClient zookeeperClient;

    private Trigger trigger;

    private JobDetail jobDetail;

    private JobInfo job = new JobInfo();

    private HAService haService;// ha切换服务

    private static final String HA_BASIC_PATH = "/jeeboss/ha/job/";
    private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP
    private static final String JOB_BEAN = "job_bean";

    private static final String METHOD_NAME = "action";

    /**
     * 生成job信息，通过覆盖此方法，构建一个完整的job
     * 
     * @return
     */
    protected abstract JobInfo generateJob();

    /**
     * 定时任务具体执行内容
     */
    protected abstract void action();

    /**
     * 当job关闭时触发
     */
    protected void onStop() {};

    /**
     * job准备启动时触发
     */
    protected void onStart() {};

    /**
     * 当定时任务执行异常时回调
     * 
     * @param throwable
     */
    protected void onException(Throwable throwable) {};

    public void start() {
        createJob();
        standby();
    }

    /**
     * 停止定时任务
     * 
     * @param isHASwitch 是否释放主机权限
     */
    public void stop(boolean isHASwitch) {
        // 如果当前不是主机，停止时，什么都不用做
        if (!isLeader && job.getIsHAEnable() && job.getIsHAStandby()) {
            logger.debug("{} is not master no need to stop", this);
            return;
        }
        isRunning = false;
        onStop();
        if (isHASwitch) {
            if (haService != null) {
                condition.signal();
                haService.joinStandby();
            }
        }
    }

    public void onApplicationEvent(ApplicationContextEvent event) {
        // 容器启动完成，启动定时任务
        ApplicationContext source = (ApplicationContext) event.getSource();
        if (ContextRefreshedEvent.class.getName().equals(event.getClass().getName())
                && source.getParent() == null) {
            start();
        } else if (ContextClosedEvent.class.getName().equals(event.getClass().getName())) {
            stop(true);
        }
    }

    /**
     * 全体待命
     */
    private void standby() {
        if (job.getIsHAEnable()) {// 使用高可用主备模式
            if (job.getIsHAStandby()) {// 开启主备模式
                // hAStandby();
                newHAstandby();
            } else {// 全活模式
                isRunning = true;
                allAlive();
            }
        } else {
            isRunning = true;
            jobManager.addJob(this);
        }
    }

    /**
     * 使用curator提供的公平主备切换
     */
    private void newHAstandby() {
        String runningPath = getHaPath();
        haService = new HAService(this, runningPath, condition);
        haService.startAndWatchStandby(new StandbyListener() {

            @Override
            public void onJoin(String standbyPath, String data) {// 当有备机加入时，更新备机列表
                logger.info("standby join:{}", data);
                job.setStandbyList(haService.getStandbyList());
                jobManager.update(job);
            }

            @Override
            public void onRemove(String standbyPath, String data) {// 备机删除
                logger.info("standby remove:{}", data);
                job.setStandbyList(haService.getStandbyList());
                jobManager.update(job);
            }
        });
    }

    @Override
    public void hAexecute() {
        isLeader = true;
        isRunning = true;
        logger.info("Job [{}]({}) now is running as Master.", job.getName(), LOCAL_IP);
        onStart();
        jobManager.addJob(this);
    }

    /**
     * 主备模式：争抢临时节点，通过选举出master来决定是否将job添加到任务管理器
     */
    @SuppressWarnings("unused")
    private void hAStandby() {
        String runningPath = getHaPath() + "/running";
        try {
            // 通过争抢创建同一ZK临时节点running来选举出master
            boolean isMaster = zookeeperClient.createEphemeral(runningPath, LOCAL_IP);
            if (isMaster) {
                // OH YEAH!LET IS GO TO TU TU TU!
                String date = null;
                if (trigger.getNextFireTime() != null) {
                    date = DateFormatUtils.format(trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss");
                }
                logger.info("starting job [{}] ,next run time on [{}]", job.getName(), date);
                onStart();
                jobManager.addJob(this);
                logger.info("Job [{}]({}) is running as Master.", job.getName(), LOCAL_IP);
                return;
            }
            // OH SHIT,WE ARE STANDBY
            joinStandBy();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 全活模式：所有job添加到任务管理器.<br>
     * 创建临时节点，当有job宕机时，打印警告信息
     * 
     * @throws Exception
     */
    private void allAlive() {
        String haPath = getHaPath();
        try {
            zookeeperClient.watchChildren(haPath, new ZKChildrenListener() {
                @Override
                public void onAdd(String childPath, String data) {
                    logger.info("Welcome [{}] to join the HA/all-alive as Master.",
                            childPath.substring(HA_BASIC_PATH.length()));
                }

                @Override
                public void onUpdate(String childPath, String data) {}

                @Override
                public void onRemove(String childPath, String data) {
                    logger.warn("{} {} job is shut down on {}", job.getGroup(), job.getName(),
                            LOCAL_IP);
                }
            });
            jobManager.addJob(this);
            zookeeperClient.createEphemeral(haPath + "/" + LOCAL_IP,
                    String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


    }

    private void joinStandBy() throws Exception {
        String standbyPath = getHaPath() + "/standBy/" + LOCAL_IP;
        // 加入待命节点
        zookeeperClient.watchChildren(getHaPath() + "/standBy", new ZKChildrenListener() {
            @Override
            public void onAdd(String childPath, String data) {
                logger.info("Welcome [{}] to join the HA as Follower.", childPath);
            }

            @Override
            public void onUpdate(String childPath, String data) {}

            @Override
            public void onRemove(String childPath, String data) {}
        });
        zookeeperClient.watchChildren(getHaPath() + "/running", new ZKChildrenListener() {
            @Override
            public void onAdd(String childPath, String data) {}

            @Override
            public void onUpdate(String childPath, String data) {}

            @Override
            public void onRemove(String childPath, String data) {
                if (childPath.endsWith("/running")) {
                    logger.warn("{} {} job is shut down", job.getGroup(), job.getName());
                    standby();
                }
            }

        });
        zookeeperClient.createEphemeral(standbyPath, String.valueOf(System.currentTimeMillis()));
    }

    private String getHaPath() {
        if (StringUtils.isBlank(job.getGroup())) {
            return HA_BASIC_PATH + job.getName();
        } else {
            return HA_BASIC_PATH + job.getGroup() + "/" + job.getName();
        }
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            ScheduleBuilder<? extends Trigger> scheduleBuilder =
                    context.getTrigger().getScheduleBuilder();
            if (scheduleBuilder instanceof SimpleScheduleBuilder) {// 如果是一个重复执行的任务，当执行原本执行的时间超过执行间隔，不执行
                long scheduledFireTime = context.getScheduledFireTime().getTime();// 获取原本执行时间
                long now = new Date().getTime();
                Trigger t = context.getTrigger();
                if (t instanceof SimpleTriggerImpl) {
                    SimpleTriggerImpl stl = (SimpleTriggerImpl) t;
                    if (now - scheduledFireTime > stl.getRepeatInterval()) {
                        return;
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("{} job execute now", this);
            }
            Object instance = context.getMergedJobDataMap().get(JOB_BEAN);
            Reflections.invokeMethodByName(instance, METHOD_NAME, new Object[0]);
        } catch (Exception e) {
            onException(e);
        }
    }

    private void createJob() {
        this.job = generateJob();
        if (job == null) {
            throw new IllegalStateException();
        }
        trigger = buildTrigger();// 构建触发器
        jobDetail = buildJobDetail();// 构建JOB
        jobDetail.getJobDataMap().put(JOB_BEAN, this);// 因为quartz通过反射方式回调execute，所以需要把当前实例保存，用于在execute方法获取当前bean，避免npe
    }

    private JobDetail buildJobDetail() {
        JobBuilder jobBuilder = JobBuilder.newJob(this.getClass());
        if (StringUtils.isBlank(job.getGroup())) {
            jobBuilder.withIdentity(job.getName());
        } else {
            jobBuilder.withIdentity(job.getName(), job.getGroup());
        }
        return jobBuilder.build();
    }

    /**
     * 根据指定的定时任务构建Trigger
     * 
     * @return
     */
    private Trigger buildTrigger() {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        if (StringUtils.isBlank(job.getGroup())) {
            triggerBuilder.withIdentity(job.getName());
        } else {
            triggerBuilder.withIdentity(job.getName(), job.getGroup());
        }
        if (StringUtils.isNotBlank(job.getCron())) {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(job.getCron())
                    .withMisfireHandlingInstructionDoNothing());// 任务暂停恢复后执行下一个周期的任务
        } else {
            triggerBuilder.startNow();
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            if (job.getTimeUnit().equals(TimeUnit.HOURS)) {
                simpleScheduleBuilder.withIntervalInHours(job.getInterval());
            } else if (job.getTimeUnit().equals((TimeUnit.SECONDS))) {
                simpleScheduleBuilder.withIntervalInSeconds(job.getInterval());
            } else if (job.getTimeUnit().equals(TimeUnit.MINUTES)) {
                simpleScheduleBuilder.withIntervalInMinutes(job.getInterval());
            } else if (job.getTimeUnit().equals(TimeUnit.MILLISECONDS)) {
                simpleScheduleBuilder.withIntervalInMilliseconds(job.getInterval());
            } else {
                throw new NotSupportedException();
            }
            if (job.getRepeat() == -1) {
                simpleScheduleBuilder.repeatForever();
            } else {
                simpleScheduleBuilder.withRepeatCount(job.getRepeat());
            }
            triggerBuilder.withSchedule(simpleScheduleBuilder);
        }
        return triggerBuilder.build();
    }


    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public JobInfo getJob() {
        return this.job;
    }

    public List<String> getStandbyList() {
        if (haService != null) {
            return haService.getStandbyList();
        }
        return Collections.emptyList();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String toString() {
        if (StringUtils.isBlank(this.job.getGroup())) {
            return this.job.getName();
        } else {
            return this.job.getGroup() + "-" + this.job.getName();
        }
    }


}
