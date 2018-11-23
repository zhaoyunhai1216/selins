package org.cluster.shell.commands;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.backtype.bean.BrokerState;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.utils.TabCommons;
import org.cluster.core.zookeeper.ZkCurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
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
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/applications";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"application id", "state", "name", "jvmOpts", "numWorkers", "category"});
        for (String child : childs) {
            AppResource res = AppResource.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            columnValues.add(new String[]{res.getString(AppResource.Fileds.ID), (res.getInteger(AppResource.Fileds.STATE) == 0 ? "Deploy" : "Running")
                    , res.getString(AppResource.Fileds.NAME), res.getString(AppResource.Fileds.JVM_OPTS), String.valueOf(res.getInteger(AppResource.Fileds.NUM_WORKERS)), res.getString(AppResource.Fileds.CATEGORY)});
        }
        logger.info("Applications\n" + new TabCommons(columnValues).toString());
    }

    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void worker(String[] args) throws Exception {
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/worker";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"worker id", "process", "host", "startTime", "runtime", "exectors", "threadCount", "cpu", "memory"});
        for (String child : childs) {
            JSONObject json = JSONObject.parseObject(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            columnValues.add(new String[]{json.getString("workerId"), json.getString("process")
                    , json.getString("host"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(json.getLong("startTime"))), String.valueOf(json.getInteger("runtime").intValue() / 60000)
                    , String.valueOf(json.getInteger("exectors").intValue()), String.valueOf(json.getInteger("threadCount").intValue()), json.getString("cpu"), json.getString("memory")
            });
        }
        logger.info("Workers\n" + new TabCommons(columnValues).toString());
    }

    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void broker(String[] args) throws Exception {
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        List<String[]> columnValues = new ArrayList<>();
        columnValues.add(new String[]{"host", "port", "start.timestamp", "rack", "category", "vCores", "memory", "hdd", "jdk"});
        for (String child : childs) {
            BrokerState state = BrokerState.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            columnValues.add(new String[]{state.getString(Environment.CLUSTER_HOST), state.getString(Environment.CLUSTER_PORT), state.getString(Environment.START_TIMESTAMP)
                    , state.getString(Environment.CLUSTER_RACK), state.getString(Environment.CLUSTER_CATEGORY), state.getString(Environment.VCORES_USED)+"%/"+state.getString(Environment.VCORES_TOTAL)
                    , state.getString(Environment.MEMOTY_USED)+"/"+ state.getString(Environment.MEMOTY_TOTAL)+ "G", state.getString(Environment.HDD_USED) + "/" +state.getString(Environment.HDD_TOTAL) + "G", state.getString(Environment.JAVA_VERSION)});
        }
        logger.info("Brokers\n" + new TabCommons(columnValues).toString());
    }

    public static void main(String[] args) throws Exception {
        /**
         * 加载配置文件信息
         */
        Configuration.init("/Users/zhaoyunhai/程序/selins//etc/conf/cluster.yaml");
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));

        new Lists().worker(args);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Lists.class);
}
