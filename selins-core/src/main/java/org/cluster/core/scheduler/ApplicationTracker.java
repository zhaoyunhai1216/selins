package org.cluster.core.scheduler;

import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/9 11:00
 * @Version: 1.0
 * @Description: TODO
 */
public enum ApplicationTracker {
    INSTANCE; //枚举实现单例模式, 此为当前对象的单例


    ApplicationTracker() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                this.tracker();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 获得单例
     */
    public static ApplicationTracker getInstance() {
        return INSTANCE;
    }

    /**
     * 跟踪调度, 调度具体的跟踪内容
     * @throws Exception
     */
    public void tracker() throws Exception {
        if (ZkOptions.isMaster(ZkConnector.getInstance().getZkCurator())) {
            //DefaultScheduler.checkLegacyWorker();
            //DefaultScheduler.checkShutdownWorker();
            DefaultScheduler.checkStartWorker();
        }
    }

    /**
     * 日志定义 Logger
     */
    public Logger logger = LoggerFactory.getLogger(ApplicationTracker.class);
}
