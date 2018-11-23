package org.cluster.shell.commands;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.BrokerState;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.rmi.Naming;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/6 15:16
 * @Version: 1.0
 * @Description: TODO
 */
public class Kill {
    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void exec(String[] args) throws Exception {
        Options opts = getOptions();
        CommandLine cliParser = new GnuParser().parse(opts, args);
        if (!cliParser.hasOption("appID")) {
            System.out.println(opts.getOptions());
            return;
        }
        BrokerState master = ZkUtils.getMaster(ZkCurator.getInstance().getZkCurator());
        ClusterService service = (ClusterService) Naming.lookup("rmi://"
                + InetAddress.getByName(master.getString(Environment.CLUSTER_HOST)).getHostAddress() + ":" + master.getInteger(Environment.CLUSTER_PORT) + "/Broker");
        service.kill(cliParser.getOptionValue("appID"));
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
    public static Logger logger = LoggerFactory.getLogger(Kill.class);
}
