package org.cluster.shell.commands;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.utils.TabCommons;
import org.cluster.core.zookeeper.ZkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/6 15:16
 * @Version: 1.0
 * @Description: TODO
 */
public class Lists {
    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void exec(String[] args) throws Exception {
        Options opts = new Options();
        opts.addOption("command", true, "command.");
        CommandLine cliParser = new GnuParser().parse(opts, args);
        switch (cliParser.getOptionValue("command")) {
            case "application":
                application(args);
                break;
            case "worker":
                worker(args);
                break;
            case "broker":
                broker(args);
                break;
        }
    }

    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void application(String[] args) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appstore";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"application id", "state", "name", "jvmOpts", "numWorkers", "category"});
        for (String child : childs) {
            AppResource res = JSONObject.parseObject(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child), AppResource.class);
            columnValues.add(new String[]{res.getId(), (res.getState() == 0 ? "Deploy" : "Running"), res.getName(), res.getJvmOpts(), String.valueOf(res.getNumWorkers()),res.getCategory()});
        }
        logger.info("Applications\n" + new TabCommons(columnValues).toString());
    }

    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void worker(String[] args) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/worker";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"worker id", "process", "host", "port", "startTime", "exectors"});
        for (String child : childs) {
            JSONObject json = JSONObject.parseObject(new String(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            columnValues.add(new String[]{json.getString("workerId"), json.getString("process")
                    , json.getString("host"), json.getString("port"), json.getString("startTime"), String.valueOf(json.getInteger("exectors").intValue())});
        }
        logger.info("Workers\n" + new TabCommons(columnValues).toString());
    }

    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void broker(String[] args) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"host", "port", "startTime", "rack", "category", "processors", "memory", "hdd", "jdk"});
        for (String child : childs) {
            JSONObject json = JSONObject.parseObject(new String(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            columnValues.add(new String[]{json.getString("host"), json.getString("port"), json.getString("startTime"), json.getString("rack")
                    , json.getString("category"), json.getString("processors"), json.getString("totalMemory"), json.getString("totalFileSystem") + "G", json.getString("jdk")});
        }
        logger.info("Brokers\n" + new TabCommons(columnValues).toString());
    }


    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Lists.class);
}
