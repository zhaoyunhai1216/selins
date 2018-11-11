package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONArray;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.cluster.rpc.ClusterServiceImpl;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/2 14:10
 * @Version: 1.0
 * @Description: 负载均衡及任务分配
 */
public class DefaultScheduler {

    /**
     * 检查遗留的异常worker信息,并进行停止操作,防止遗留worker堆积
     */
    public static void checkShutdownWorker() throws Exception {
        List<String> applications = ZkOptions.getRunningApplicationsID(ZkConnector.getInstance().getZkCurator());
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < workerJson.size(); i++) {
            String[] params = workerJson.getJSONObject(i).getString("workerId").split("\\_");
            if (applications.contains(params[0])) {
                RemoteOptions.killWorker(workerJson.getJSONObject(i).getString("host"), params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
                logger.info("[Tracker] Shutdown Worker <" + workerJson.getJSONObject(i).getString("workerId") + "> was found and the kill was completed");
            }
        }
    }


    /**
     * 检测因异常停止的worker, 检查出来后进行调度重启
     */
    public static void checkStartWorker() throws Exception {
        List<AppResource> applications = ZkOptions.getRunningApplications(ZkConnector.getInstance().getZkCurator());
        List<String> workerList = ZkOptions.getWorkerID(ZkConnector.getInstance().getZkCurator());
        List<AssetsState> assets = DefaultScheduler.getAssetsState();
        for (AppResource res : applications) {
            for (int i = 0; i < res.getNumWorkers(); i++) {
                if (!workerList.contains(res.getId() + "_" + i + "_" + +res.getNumWorkers())) {
                    RemoteOptions.startWorker(res.getId(), i, res.getNumWorkers(), assets);
                    logger.info("[Tracker] Start Worker <" + res.getId() + "_" + i + "_" + +res.getNumWorkers() + "> was found and the start was completed");
                }
            }
        }
    }

    /**
     * 检测因异常重复多余的worker, 检查出来后进行调度重启
     */
    public static void checkLegacyWorker() throws Exception {
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        HashSet<String> workerSet = new HashSet<>();
        for (int i = 0; i < workerJson.size(); i++) {
            String[] params = workerJson.getJSONObject(i).getString("workerId").split("\\_");
            if(workerSet.contains(workerJson.getJSONObject(i).getString("workerId"))){
                RemoteOptions.killWorker(workerJson.getJSONObject(i).getString("host"), params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
                logger.info("[Tracker] Legacy Worker <" + workerJson.getJSONObject(i).getString("workerId") + "> was found and the kill was completed");
            }
        }
    }

    /**
     * 获取所有可以运行程序的节点, 并返回排序后的节点列表信息, 供分配任务使用
     *h
     * @return List
     * @throws Exception
     */
    public static List<AssetsState> getAssetsState() throws Exception {
        List<AssetsState> states = ZkOptions.getNodeState(ZkConnector.getInstance().getZkCurator());
        HashMap<String, List<String>> workerMap = new HashMap<>();
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < workerJson.size(); i++) {
            workerMap.computeIfAbsent(workerJson.getJSONObject(i).getString("ids"), x -> new ArrayList<>()).add(workerJson.getJSONObject(i).toJSONString());
        }
        return states.stream().filter(x -> x.getCategory().equals("default")).map(x -> x.setWorkerSize(workerMap.getOrDefault(x.getBrokerID(), new ArrayList<>()).size())).sorted().collect(Collectors.toList());
    }


    /**
     * 检测因异常重复多余的worker, 检查出来后进行调度重启
     */
    public static void rebalanceWorkers(String category) throws Exception {
        //暂时未做实现
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(DefaultScheduler.class);
}
