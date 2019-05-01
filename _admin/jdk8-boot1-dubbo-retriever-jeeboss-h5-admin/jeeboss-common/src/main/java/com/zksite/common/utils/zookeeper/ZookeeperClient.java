package com.zksite.common.utils.zookeeper;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * zookeeper客户端<br/>
 * 基于<a href="http://curator.apache.org/">Curator</a>封装了常用操作
 *
 */
public class ZookeeperClient implements Closeable, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    private CuratorFramework client = null;

    protected ZookeeperClient(String zookeeperAddress) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // session timeout默认为60秒，客户端断掉后n秒后zk中数据才清除，太长了
        client = CuratorFrameworkFactory.builder().connectString(zookeeperAddress)
                .retryPolicy(retryPolicy).connectionTimeoutMs(10 * 1000).sessionTimeoutMs(10 * 1000)
                .build();
        client.start();
    }

    public boolean create(String path, String value) throws Exception {
        return create(path, value, CreateMode.PERSISTENT) == null;
    }

    public boolean createEphemeral(String path, String value) throws Exception {
        return create(path, value, EPHEMERAL) == null;
    }

    public String create(String path, String value, CreateMode createMode) throws Exception {
        try {
            return client.create().creatingParentsIfNeeded().withMode(createMode).forPath(path,
                    value.getBytes());
        } catch (NodeExistsException e) {
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    public String get(String path) throws Exception {
        try {
            return new String(client.getData().forPath(path));
        } catch (NoNodeException e) {
            return null;
        }
    }

    /**
     * @deprecated
     * @param path
     * @param defaultValue
     * @param watcher
     * @return
     * @throws Exception
     */
    public String getAndWatch(String path, String defaultValue, Watcher watcher) throws Exception {
        if (!this.exists(path)) {
            this.create(path, defaultValue);
        }

        return new String(client.getData().usingWatcher(watcher).forPath(path));
    }

    public boolean exists(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    public int getVersion(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat == null ? -1 : stat.getVersion();
    }

    public List<String> getChildren(String path) throws Exception {
        try {
            return client.getChildren().forPath(path);
        } catch (NoNodeException e) {
            return Collections.emptyList();
        }
    }

    public boolean set(String path, String value) throws Exception {
        return set(path, value, -1);
    }

    public boolean set(String path, String value, int version) throws Exception {
        try {
            client.setData().withVersion(version).forPath(path, value.getBytes());
            return true;
        } catch (NoNodeException e) {
            return false;
        }
    }

    public void delete(String path) throws Exception {
        delete(path, -1);
    }

    public void delete(String path, int version) throws Exception {
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version)
                    .forPath(path);
        } catch (NoNodeException e) {
            // ignore
        }
    }

    public void watchNode(String path, String defaultValue, final ZKNodeListener listener)
            throws Exception {
        final NodeCache nodeCache = new NodeCache(client, path, false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                listener.onChange(new String(nodeCache.getCurrentData().getData()), nodeCache);
            }
        });
        nodeCache.start();
        if (!this.exists(path) && defaultValue != null) {
            this.create(path, defaultValue);
        }
    }

    public PathChildrenCache watchChildren(String parentPath, final ZKChildrenListener listener)
            throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(client, parentPath, true);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        listener.onAdd(data.getPath(), new String(data.getData()));
                        break;
                    case CHILD_REMOVED:
                        listener.onRemove(data.getPath(), new String(data.getData()));
                        break;
                    case CHILD_UPDATED:
                        listener.onUpdate(data.getPath(), new String(data.getData()));
                        break;
                    default:
                        break;
                }
            }
        });
        childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
        return childrenCache;
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Override
    public void destroy() throws Exception {
        close();
        logger.info("Zookeeper Client destoried.");
    }

    public CuratorFramework getCurator() {
        return client;
    }

    public interface ZKNodeListener {
        public void onChange(String data, NodeCache nodeCache);
    }

    public interface ZKChildrenListener {
        public void onAdd(String childPath, String data);

        public void onUpdate(String childPath, String data);

        public void onRemove(String childPath, String data);
    }

}
