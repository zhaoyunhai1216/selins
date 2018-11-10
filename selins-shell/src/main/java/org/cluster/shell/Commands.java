package org.cluster.shell;

import org.cluster.core.commons.Configuration;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.shell.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/6 15:14
 * @Version: 1.0
 * @Description: TODO
 */
public class Commands {
    public static void main(String[] args) throws Exception {
        /**
         * 加载配置文件信息
         */
        Configuration.init(URLDecoder.decode(new Commands().getClass().getResource("/").getFile(),"UTF-8") + "/etc/conf/cluster.yaml");
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkConnector.getInstance().init(Configuration.getInstance().getConf().getString("cluster.zookeeper.servers"));
        /**
         * 执行命令
         */
        switch (args[0]) {
            case "deploy":new Deploy().exec(args);break;
            case "destory":new Destroy().exec(args);break;
            case "start":new Start().exec(args);break;
            case "kill":new Kill().exec(args);break;
            case "list":new Lists().exec(args);break;
            case "state":new State().exec(args);break;
            case "help":new Help().exec(args);break;
            default: new Help().exec(args);break;
        }
    }



    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Commands.class);
}
