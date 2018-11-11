package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.List;

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
    public static void startApplication(AppResource appResource) throws Exception {
        List<AssetsState> assets = DefaultScheduler.getAssetsState();
        for (int i = 0; i < appResource.getNumWorkers(); i++) {
            startWorker(appResource.getId(), i, appResource.getNumWorkers(), assets);
        }
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void killApplication(String appID) throws Exception {
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < workerJson.size(); i++) {
            String host = workerJson.getJSONObject(i).getString("host");
            getBrokerService(host).kill(appID, i, workerJson.size());
        }
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static void startWorker(String appID, int seq, int total, List<AssetsState> assets) throws Exception {
        int index = seq % assets.size();
        RemoteOptions.getBrokerService(assets.get(index).getHost(), assets.get(index).getPort()).start(appID, seq, total);
    }

    /**
     * 重新启动worker,调用远程Broker接口,调度重启worker
     */
    public static void killWorker(String host, String appID, int seq, int total) throws Exception {
        RemoteOptions.getBrokerService(host, Integer.parseInt(ZkOptions.getMapping(host))).kill(appID, seq, total);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static byte[] getAppResources(String appID) throws Exception {
        JSONObject masterJson = JSONObject.parseObject(ZkOptions.getAppStore(ZkConnector.getInstance().getZkCurator()));
        return getAppstoreService(masterJson.getString("host"), masterJson.getInteger("port")).getResources(appID);
    }

    /**
     * 更新状态信息
     *
     * @param appID
     * @param state
     * @throws Exception
     */
    public static void updateState(String appID, int state) throws Exception {
        JSONObject appmetaJson = ZkOptions.getAppMetaJson();
        getAppstoreService(appmetaJson.getString("host"), appmetaJson.getInteger("port")).updateState(appID, state);
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
        return (ClusterService) Naming.lookup("rmi://" + hostAddress + ":" + ZkOptions.getMapping(host) + "/Broker");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(RemoteOptions.class);
}
