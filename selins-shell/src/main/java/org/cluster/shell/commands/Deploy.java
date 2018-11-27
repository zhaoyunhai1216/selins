package org.cluster.shell.commands;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.AppStore;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.commons.Configuration;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.rmi.Naming;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/6 15:13
 * @Version: 1.0
 * @Description: TODO
 */
public class Deploy {
    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void exec(String[] args) throws Exception {
        Options opts = getOptions();
        CommandLine cliParser = new GnuParser().parse(opts, args);
        if (!cliParser.hasOption("jvmOpts") || !cliParser.hasOption("class") || !cliParser.hasOption("numWorkers") || !cliParser.hasOption("category") || !cliParser.hasOption("path")) {
            logger.info(opts.getOptions().toString());
            return;
        }
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/appstore";
        AppStore appStore = AppStore.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir)));
        AppStoreService service = (AppStoreService) Naming.lookup("rmi://"
                + InetAddress.getByName(appStore.getString(AppStore.Fileds.HOST)).getHostAddress() + ":" + appStore.getInteger(AppStore.Fileds.PORT) + "/AppStore");
        byte[] b = UtilCommons.zipDirectory(new File(cliParser.getOptionValue("path")), "/");
        service.deploy(cliParser.getOptionValue("jvmOpts"), cliParser.getOptionValue("class"), Integer.parseInt(cliParser.getOptionValue("numWorkers")), cliParser.getOptionValue("category"), b);
        logger.info("[Cluster] The application was successfully submitted. So let's go ahead and look at UI");
    }

    /**
     * 获取命令的参数相关内容的描述信息
     */
    public Options getOptions() {
        Options opts = new Options();
        opts.addOption("jvmOpts", true, "application java options.");
        opts.addOption("class", true, "application main class.");
        opts.addOption("numWorkers", true, "work parallelism.");
        opts.addOption("category", true, "application category.");
        opts.addOption("jars", true, "Application file path.");
        return opts;
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Deploy.class);
}
