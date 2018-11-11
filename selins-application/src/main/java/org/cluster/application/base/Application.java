package org.cluster.application.base;

import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.tracker.LoggerManager;
import org.cluster.application.utils.UtilCommons;
import org.cluster.application.zookeeper.ZkCurator;
import org.cluster.application.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/24 10:57
 * @Version: 1.0
 * @Description: TODO
 */
public abstract class Application {

    /**
     * 程序application定义方法, 用于定义应用的处理结构
     */
    public void init(EnvOptions options) throws Exception {
        ZkUtils.build(options.getZkWorkerDir(options.getOptionValue(Environment.CLUSTER_WORKERUID)), UtilCommons.getWorkerState(options));
        LoggerManager.launcher(options);
        define(options);
        System.out.println(options.getOptionValue(Environment.CLUSTER_WORKERUID));
        ZkUtils.update(ZkCurator.getInstance().getZkCurator(), options.getZkWorkerDir(options.getOptionValue(Environment.CLUSTER_WORKERUID)), UtilCommons.getWorkerState(options).getBytes());
    }

    /**
     * 程序application定义方法, 用于定义应用的处理结构
     */
    public abstract void define(EnvOptions config) throws Exception;

    /**
     * 提交application应用到集群,当cluster 为 local时,运行本地模式
     */
    public static void toSubmit(Application application, String cluster, String... args) throws Exception {
        EnvOptions options = UtilCommons.getCliParser(args);
        ZkCurator.getInstance().init(options.getOptionValue(Environment.ZK_CONNECT));
        application.init(options);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Application.class);
}
