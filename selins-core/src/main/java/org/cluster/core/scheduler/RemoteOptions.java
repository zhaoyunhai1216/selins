package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.backtype.bean.WorkerState;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
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
        for (int i = 0; i < appResource.getNumWorkers(); i++) {
            List<AssetsState> assets = DefaultScheduler.getAssetsState(appResource.getCategory());
            startWorker(appResource.getId(), i, appResource.getNumWorkers(), assets.get(0));
        }
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void killApplication(String appID) throws Exception {
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < workersState.size(); i++) {
            String host = workersState.get(i).getHost();
            getBrokerService(host).kill(appID, i, workersState.size());
        }
    }

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
        JSONObject masterJson = JSONObject.parseObject(ZkUtils.getAppStore(ZkCurator.getInstance().getZkCurator()));
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
        JSONObject appmetaJson = ZkUtils.getAppMetaJson();
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
        return (ClusterService) Naming.lookup("rmi://" + hostAddress + ":" + ZkUtils.getMapping(host) + "/Broker");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(RemoteOptions.class);
}
