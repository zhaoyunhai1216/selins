package org.cluster.application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cluster.application.base.Application;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.commons.ShutdownHook;
import org.cluster.application.tracker.LoggerManager;
import org.cluster.application.utils.UtilCommons;
import org.cluster.application.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/29 13:35
 * @Version: 1.0
 * @Description: TODO
 */
public class Worker {
    /**
     * 程序入口方法信息
     */
    public static void main(String[] args) throws Exception {
        EnvOptions opt = UtilCommons.getCliParser(args);
        ZkCurator.getInstance().init(opt.getOptionValue(Environment.ZK_CONNECT));
        Application application = (Application) Class.forName(opt.getOptionValue(Environment.APPLICATION_MAIN)).newInstance();
        application.init(opt);
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(opt));
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Worker.class);
}
