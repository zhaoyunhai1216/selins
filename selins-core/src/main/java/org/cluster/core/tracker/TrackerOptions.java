package org.cluster.core.tracker;

import com.alibaba.fastjson.JSONArray;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.cluster.rpc.ClusterService;
import org.cluster.core.scheduler.AssetsState;
import org.cluster.core.scheduler.RemoteOptions;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/9 11:19
 * @Version: 1.0
 * @Description: TODO
 */
public class TrackerOptions {
    /**
     * 检查遗留的异常worker信息,并进行停止操作,防止遗留worker堆积
     */
    public static void shutdownOnLegacy() throws Exception {
        List<AppResource> resources = ZkOptions.getApplications(ZkConnector.getInstance().getZkCurator());
        List<String> applications = resources.stream().filter(x -> x.getState() == 0).map(x -> x.getId()).collect(Collectors.toList());
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < workerJson.size(); i++) {
            String[] params = workerJson.getJSONObject(i).getString("workerId").split("\\_");
            if (applications.contains(params[0])) {
                RemoteOptions.killWorker(workerJson.getJSONObject(i).getString("host"), params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
                logger.info("[Tracker] Legacy worker <" + workerJson.getJSONObject(i).getString("workerId") + "> was found and the kill was completed");
            }
        }
    }


    /**
     * 检测因异常停止的worker, 检查出来后进行调度重启
     */
    public static void checkWorker() throws Exception {
        List<AppResource> applications = ZkOptions.getApplications(ZkConnector.getInstance().getZkCurator()).stream().filter(x -> x.getState() == 1).collect(Collectors.toList());
        List<String> workerList = ZkOptions.getWorkerID(ZkConnector.getInstance().getZkCurator());
        List<AssetsState> assets = UtilCommons.getAssetsState();
        for (AppResource res : applications) {
            for (int i = 0; i < res.getNumWorkers(); i++) {
                if (!workerList.contains(res.getId() + "_" + i + "_" + +res.getNumWorkers())) {
                    RemoteOptions.startWorker(res.getId(), i, res.getNumWorkers(), assets);
                    logger.info("[Tracker] Dead worker <" + res.getId() + "_" + i + "_" + +res.getNumWorkers() + "> was found and the restart was completed");
                }
            }
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ApplicationTracker.class);

}
