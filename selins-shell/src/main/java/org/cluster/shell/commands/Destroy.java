package org.cluster.shell.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.AppStorePojo;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.rmi.Naming;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/6 15:15
 * @Version: 1.0
 * @Description: TODO
 */
public class Destroy {
    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void exec(String[] args) throws Exception {
        Options opts = getOptions();
        CommandLine cliParser = new GnuParser().parse(opts, args);
        if (!cliParser.hasOption("appID")) {
            logger.info(opts.getOptions().toString());
            return;
        }
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/appstore";
        AppStorePojo appStore = AppStorePojo.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir)));
        AppStoreService service = (AppStoreService) Naming.lookup("rmi://"
                + InetAddress.getByName(appStore.getString(AppStorePojo.Fileds.HOST)).getHostAddress() + ":" + appStore.getInteger(AppStorePojo.Fileds.PORT) + "/AppStore");

        service.destroy(cliParser.getOptionValue("appID"));
        logger.info("[Cluster] The application was successfully destroy. So let's go ahead and look at UI");
    }

    /**
     * 获取命令的参数相关内容的描述信息
     */
    public Options getOptions(){
        Options opts = new Options();
        opts.addOption("appID", true, "application id.");
        return opts;
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Destroy.class);
}
