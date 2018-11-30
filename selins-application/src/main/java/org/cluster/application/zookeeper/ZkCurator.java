package org.cluster.application.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/25 17:45
 * @Version: 1.0
 * @Description: TODO
 */
public enum ZkCurator {
    INSTANCE; //枚举实现单例模式, 此为当前对象的单例

    /**
     * zookeeper Curator 连接
     */
    private CuratorFramework curator;

    /**
     * 获得zookeeper连接
     */
    public CuratorFramework getZkCurator() {
        return curator;
    }

    /**
     * 初始化与zk的连接信息
     */
    public void init(String zkConnect) {
        curator = CuratorFrameworkFactory.newClient(zkConnect, 90 * 1000, 30 * 1000, new ExponentialBackoffRetry(500, 10, 3000));
        curator.start();
        expire();
        logger.info("[Zk] init the connection to zk, " + zkConnect);
    }

    /**
     * 初始化与zk的连接信息
     */
    public void expire() {
        curator.getConnectionStateListenable().addListener((x, y) -> {
            if (y == ConnectionState.LOST) {
                logger.error("[Zk] zookeeper sessions expire and applications exit.");
                System.exit(0);
            }
        });
    }

    /**
     * 获得单例
     */
    public static ZkCurator getInstance() {
        return INSTANCE;
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ZkCurator.class);
}
