package org.cluster.appstore;

import org.cluster.appstore.commons.AppStoreRMI;
import org.cluster.appstore.utils.UtilCommons;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/17 17:02
 * @Version: 1.0
 * @Description: 本节点主要缓存所有的应用静态文件及相关依赖信息
 */
public class AppStore {

    /**
     * 程序主方法, 负责调度及启动程序
     */
    public static void main(String[] args) throws Exception {
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkCurator.getInstance().init(Configuration.getInstance().getString(Environment.ZK_CONNECT));
        /**
         * 构建本集群的Master节点的zookeeper路径信息
         */
        UtilCommons.initAppstoreDir();
        /**
         * 启用相关远程调用服务，可用于对提交的任务进行控制
         */
        AppStoreRMI.init();
        logger.info("[Cluster] AppStore [" + Configuration.getInstance().getString(Environment.CLUSTER_HOST)
                + ":" + Configuration.getInstance().getInteger(Environment.APPSTORE_PORT) + "] is bootup successfully.");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(AppStore.class);
}
