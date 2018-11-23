package org.cluster.test;

import org.cluster.core.commons.Configuration;
import org.cluster.core.zookeeper.ZkCurator;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/16 17:11
 * @Version: 1.0
 * @Description: TODO
 */
public class Test {
    public static void main(String[] args) throws Exception {
        /**
         * 加载配置文件信息
         */
        Configuration.init("E:\\工作空间\\ccinfra-real-cmpt-yn\\etc\\conf\\cluster.yaml");
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));

       //DefaultScheduler.getAssetsState("default");

    }
}
