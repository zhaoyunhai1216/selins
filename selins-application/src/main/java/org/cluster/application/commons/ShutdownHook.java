package org.cluster.application.commons;

import org.cluster.application.Worker;
import org.cluster.application.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/23 11:10
 * @Version: 1.0
 * @Description: TODO
 */
public class ShutdownHook extends Thread {
    private EnvOptions options;

    /**
     * 构造方法
     */
    public ShutdownHook(EnvOptions options) {
        this.options = options;
    }

    /**
     * 进程停止时需要立即释放zookeeper资源
     */
    @Override
    public void run() {
        try {
            String zkDir = options.getZkWorkerDir(options.getWorkerID());
            ZkCurator.getInstance().getZkCurator().delete().forPath(zkDir);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage(), e);
        }
        logger.info("Destroy zookeeper heartbeat connection,Worker Shutting down .");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
}
