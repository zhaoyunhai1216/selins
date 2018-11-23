package org.cluster.shell.commands;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.BrokerState;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.utils.TabCommons;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/8 17:16
 * @Version: 1.0
 * @Description: TODO
 */
public class State {
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
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        Map<String, List<String[]>> targets = new HashMap<>();
        for (String child : childs) {
            BrokerState master =BrokerState.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            ClusterService service = (ClusterService) Naming.lookup("rmi://"
                    + InetAddress.getByName(master.getString(Environment.CLUSTER_HOST)).getHostAddress() + ":" + master.getInteger(Environment.CLUSTER_PORT) + "/Broker");
            Map<String, Map<String, Map<String, String>>> state = JSONObject.parseObject(service.getState(),Map.class);
            UtilCommons.megrMap(targets,state.getOrDefault(cliParser.getOptionValue("appID"),new HashMap<>()));
        }
        for (Map.Entry<String, List<String[]>> entry: targets.entrySet()) {
            logger.info(entry.getKey() + "\n" + new TabCommons(entry.getValue()).toString());
        }
    }

    public static void main(String[] args) throws Exception {
        String appID = "ApplicationTest-20";
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        Map<String, List<String[]>> targets = new HashMap<>();
        for (String child : childs) {
            BrokerState master =BrokerState.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            ClusterService service = (ClusterService) Naming.lookup("rmi://"
                    + InetAddress.getByName(master.getString(Environment.CLUSTER_HOST)).getHostAddress() + ":" + master.getInteger(Environment.CLUSTER_PORT) + "/Broker");
            Map<String, Map<String, Map<String, String>>> state = JSONObject.parseObject(service.getState(),Map.class);
            UtilCommons.megrMap(targets,state.getOrDefault(appID,new HashMap<>()));
        }
        for (Map.Entry<String, List<String[]>> entry: targets.entrySet()) {
            logger.info(entry.getKey() + "\n" + new TabCommons(entry.getValue()).toString());
        }
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
    public static Logger logger = LoggerFactory.getLogger(Start.class);
}
