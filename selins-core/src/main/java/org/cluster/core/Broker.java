package org.cluster.core;

import org.cluster.core.cluster.rpc.ClusterRMI;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.scheduler.ApplicationTracker;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/9 14:32
 * @Version: 1.0
 * @Description: 集群统一管理者
 */
public class Broker {
    /**
     * 程序主方法, 负责调度及启动程序
     */
    public static void main(String[] args) throws Exception {
        /**
         * 加载配置文件信息
         */
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkCurator.getInstance().init(Configuration.getInstance().getString(Environment.ZK_CONNECT));
        /**
         * 构建本集群的Master节点的zookeeper路径信息
         */
        ZkUtils.initBrokerState();
        /**
         * 启用相关远程调用服务，可用于对提交的任务进行控制
         */
        ClusterRMI.init();
        /**
         * 启动跟踪器
         */
        ApplicationTracker.getInstance();
        logger.info("[Cluster] Broker [" + Configuration.getInstance().getString(Environment.CLUSTER_HOST)
                + ":" + Configuration.getInstance().getInteger(Environment.CLUSTER_PORT) + "] is start-up successful.");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Broker.class);
}
