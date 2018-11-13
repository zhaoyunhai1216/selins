package org.cluster.test;

import com.alibaba.fastjson.JSONObject;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.commons.Configuration;
import org.cluster.core.scheduler.AssetsState;
import org.cluster.core.scheduler.DefaultScheduler;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.List;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/5 14:05
 * @Version: 1.0
 * @Description: TODO
 */
public class StartRpcTets {
    public static void main(String[] args) throws Exception {
        /**
         * 加载配置文件信息
         */
        Configuration.init("E:\\工作空间\\ccinfra-real-cmpt-yn\\etc\\conf\\cluster.yaml");
        /**
         * 负责启动连接zookeeper,传入zookeeper地址
         */
        ZkConnector.getInstance().init(Configuration.getInstance().getConf().getString("cluster.zookeeper.servers"));

        //JSONObject json = JSONObject.parseObject(ZkOptions.getMaster(ZkConnector.getInstance().getZkCurator()));
        //ClusterService service = (ClusterService) Naming.lookup("rmi://"
        //        + InetAddress.getByName(json.getString("host")).getHostAddress() + ":" + json.getInteger("port") + "/Broker");
        //service.start("ecef5606-8686-4207-ad09-e438164cb0e7");
        List<AssetsState> assets = DefaultScheduler.getAssetsState();
        System.out.println("end");
    }
}
