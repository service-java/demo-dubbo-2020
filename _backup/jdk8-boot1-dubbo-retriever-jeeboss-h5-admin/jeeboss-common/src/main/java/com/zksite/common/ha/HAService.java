package com.zksite.common.ha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.zksite.common.utils.Reflections;
import com.zksite.common.utils.SpringContextUtil;
import com.zksite.common.utils.zookeeper.ZookeeperClient;
import com.zksite.common.utils.zookeeper.ZookeeperClient.ZKChildrenListener;

/**
 * 主备切换服务<br>
 * 使用<a href="http://curator.apache.org/">curator</a> LeaderSelector实现公平主备切换
 * 需要主备切换服务时，需实现{@link com.zksite.common.ha.HAService.HAExecutor}
 * 
 * @author hanjieHu
 *
 */
public class HAService extends LeaderSelectorListenerAdapter {

    private static final String LOCAL_IP = NetUtils.getLocalHost(); // 本地IP

    private static final Logger LOGGER = LoggerFactory.getLogger(HAService.class);


    private HAExecutor executor;

    private final LeaderSelector leaderSelector;

    private String path;

    private ZookeeperClient zookeeperClient = SpringContextUtil.getBean(ZookeeperClient.class);

    private Condition condition;

    private static final String EXECUTE_METHODD = "hAexecute";

    private static final String STANDBY_PATH = "/standby";

    private static final String RUNNING_PATH = "/running";


    private StandbyListener standbyListener;

    private String standbyPath;

    /**
     * 当executor成为主机时，会使用executor.wait使当前主机持续成为主机，如果需要释放当前主机权限，只需唤醒当前线程
     * 
     * @param executor
     * @param path
     * @param barrier 释放领导权信号
     */
    public HAService(HAExecutor executor, String path, Condition condition) {
        this.executor = executor;
        this.path = path;
        if (path.lastIndexOf("/") == path.length() - 1) {
            this.path = path.substring(0, path.length() - 1);
        }
        leaderSelector = new LeaderSelector(zookeeperClient.getCurator(), getPath(), this);
        leaderSelector.autoRequeue();
        this.condition = condition;
    }


    public void start() {
        joinStandby();
        leaderSelector.start();
    }

    /**
     * 启动并监听备机
     * 
     * @param standbyListener
     */
    public void startAndWatchStandby(StandbyListener standbyListener) {
        this.standbyListener = standbyListener;
        joinStandby();
        leaderSelector.start();
    }


    public List<String> getStandbyList() {
        try {
            List<String> children = zookeeperClient.getChildren(path + STANDBY_PATH);
            List<String> hosts = new ArrayList<String>(children.size());
            for (String string : children) {
                hosts.add(zookeeperClient.get(path + STANDBY_PATH + "/" + string));
            }
            return hosts;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public void colse() {
        leaderSelector.close();
    }

    public void joinStandby() {
        try {
            standbyPath = zookeeperClient.create(getStandbyPath(), LOCAL_IP,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void removeStandby() {
        try {
            zookeeperClient.delete(standbyPath);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String getStandbyPath() {
        return path + STANDBY_PATH + "/" + LOCAL_IP;
    }

    private String getPath() {
        return path + RUNNING_PATH + "/" + LOCAL_IP;
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        PathChildrenCache watchChildren = null;
        try {
            LOGGER.info("host {} {} is now the leader", LOCAL_IP, executor);
            removeStandby();
            if (standbyListener != null) {
                watchChildren = zookeeperClient.watchChildren(path + STANDBY_PATH,
                        new ZKChildrenListener() {

                            @Override
                            public void onUpdate(String childPath, String data) {}

                            @Override
                            public void onRemove(String childPath, String data) {
                                standbyListener.onRemove(childPath, data);
                            }

                            @Override
                            public void onAdd(String childPath, String data) {
                                standbyListener.onJoin(childPath, data);
                            }
                        });
            }
            Reflections.invokeMethodByName(executor, EXECUTE_METHODD, new Object[0]);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        condition.await();
        if (watchChildren != null) {
            watchChildren.close();
        }
        LOGGER.info("host {} will lost leader ", LOCAL_IP);
    }

    public interface HAExecutor {
        void hAexecute();
    }

    public static class StandbyListener {
        public void onJoin(String standbyPath, String data) {};

        public void onRemove(String standbyPath, String data) {};
    }

}
