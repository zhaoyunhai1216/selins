package org.cluster.core.scheduler;

import org.cluster.core.backtype.bean.AppStorePojo;
import org.cluster.core.backtype.bean.AssetsState;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.rmi.Naming;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/9 14:40
 * @Version: 1.0
 * @Description: TODO
 */
public class RemoteOptions {

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static void startWorker(String appID, int seq, int total, AssetsState assets) throws Exception {
        RemoteOptions.getBrokerService(assets.getHost(), assets.getPort()).start(appID, seq, total);
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static void killWorker(String host, String appID, int seq, int total) throws Exception {
        RemoteOptions.getBrokerService(host, ZkUtils.getMapping(host)).kill(appID, seq, total);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static byte[] getAppResources(String appID) throws Exception {
        AppStorePojo appStore = ZkUtils.getAppStore(ZkCurator.getInstance().getZkCurator());
        return getAppstoreService(appStore.getString(AppStorePojo.Fileds.HOST), appStore.getInteger(AppStorePojo.Fileds.PORT)).getResources(appID);
    }

    /**
     * 更新状态信息
     *
     * @param appID
     * @param state
     * @throws Exception
     */
    public static void updateState(String appID, int state) throws Exception {
        AppStorePojo metaNode = ZkUtils.getAppStore(ZkCurator.getInstance().getZkCurator());
        getAppstoreService(metaNode.getString(AppStorePojo.Fileds.HOST), metaNode.getInteger(AppStorePojo.Fileds.PORT)).updateState(appID, state);
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static AppStoreService getAppstoreService(String host, int port) throws Exception {
        String hostAddress = InetAddress.getByName(host).getHostAddress();
        return (AppStoreService) Naming.lookup("rmi://" + hostAddress + ":" + port + "/AppStore");
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static ClusterService getBrokerService(String host, int port) throws Exception {
        String hostAddress = InetAddress.getByName(host).getHostAddress();
        return (ClusterService) Naming.lookup("rmi://" + hostAddress + ":" + port + "/Broker");
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static ClusterService getBrokerService(String host) throws Exception {
        String hostAddress = InetAddress.getByName(host).getHostAddress();
        return (ClusterService) Naming.lookup("rmi://" + hostAddress + ":" + ZkUtils.getMapping(host) + "/Broker");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(RemoteOptions.class);
}
