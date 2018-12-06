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
    public static void startWorker(String applicationID, int seq, int total, AssetsState assets) throws Exception {
        RemoteOptions.getBrokerService(assets.getHost(), assets.getPort()).start(applicationID, seq, total);
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static void killWorker(String host, String applicationID, int seq, int total) throws Exception {
        RemoteOptions.getBrokerService(host, ZkUtils.getMapping(host)).kill(applicationID, seq, total);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static byte[] getAppResources(String applicationID) throws Exception {
        AppStorePojo appStore = ZkUtils.getAppStore(ZkCurator.getInstance().getZkCurator());
        return getAppstoreService(appStore.getString(AppStorePojo.Fileds.HOST), appStore.getInteger(AppStorePojo.Fileds.PORT)).getResources(applicationID);
    }

    /**
     * 更新状态信息
     *
     * @param applicationID
     * @param state
     * @throws Exception
     */
    public static void updateState(String applicationID, int state) throws Exception {
        AppStorePojo appStore = ZkUtils.getAppStore(ZkCurator.getInstance().getZkCurator());
        getAppstoreService(appStore.getString(AppStorePojo.Fileds.HOST), appStore.getInteger(AppStorePojo.Fileds.PORT)).updateState(applicationID, state);
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
