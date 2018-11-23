package org.cluster.core.scheduler;

import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.backtype.bean.AssetsState;
import org.cluster.core.backtype.bean.BrokerState;
import org.cluster.core.backtype.bean.WorkerState;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
        List<String> applications = ZkUtils.getRunningApplicationsID(ZkCurator.getInstance().getZkCurator());
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < workersState.size(); i++) {
            if (!applications.contains(workersState.get(i).getAppID())) {
                RemoteOptions.killWorker(workersState.get(i).getString(WorkerState.Fileds.HOST), workersState.get(i).getAppID(), workersState.get(i).getSeq(), workersState.get(i).getTotal());
                logger.info("[Tracker] Shutdown Worker <" + workersState.get(i).getString(WorkerState.Fileds.WORKER_ID) + "> was found and the kill was completed");
            }
        }
    }


    /**
     * 检测因异常停止的worker, 检查出来后进行调度重启
     */
    public static void checkStartWorker() throws Exception {
        List<AppResource> applications = ZkUtils.getRunningApplications(ZkCurator.getInstance().getZkCurator());
        List<String> workerList = ZkUtils.getWorkerID(ZkCurator.getInstance().getZkCurator());
        for (AppResource res : applications) {
            for (int i = 0; i < res.getInteger(AppResource.Fileds.NUM_WORKERS); i++) {
                if (!workerList.contains(res.getString(AppResource.Fileds.ID) + "_" + i + "_" + +res.getInteger(AppResource.Fileds.NUM_WORKERS))) {
                    RemoteOptions.startWorker(res.getString(AppResource.Fileds.ID), i, res.getInteger(AppResource.Fileds.NUM_WORKERS), DefaultScheduler.getAssetsState(res.getString(AppResource.Fileds.CATEGORY)).get(0));
                    logger.info("[Tracker] Start Worker <" + res.getString(AppResource.Fileds.ID) + "_" + i + "_" + +res.getInteger(AppResource.Fileds.NUM_WORKERS) + "> was found and the start was completed");
                }
            }
        }
    }

    /**
     * 检测因异常重复多余的worker, 检查出来后进行调度重启
     */
    public static void checkLegacyWorker() throws Exception {
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        HashSet<String> workerSet = new HashSet<>();
        for (int i = 0; i < workersState.size(); i++) {
            if (workerSet.contains(workersState.get(i).getString(WorkerState.Fileds.WORKER_ID))) {
                RemoteOptions.killWorker(workersState.get(i).getString(WorkerState.Fileds.HOST), workersState.get(i).getAppID(), workersState.get(i).getSeq(), workersState.get(i).getTotal());
                logger.info("[Tracker] Legacy Worker <" + workersState.get(i).getString(WorkerState.Fileds.WORKER_ID) + "> was found and the kill was completed");
            }
        }
    }

    /**
     * 获取所有可以运行程序的节点, 并返回排序后的节点列表信息, 供分配任务使用
     * h
     *
     * @return List
     * @throws Exception
     */
    public static List<AssetsState> getAssetsState(String category) throws Exception {
        List<AssetsState> states = ZkUtils.getNodeState(ZkCurator.getInstance().getZkCurator());
        HashMap<String, List<WorkerState>> workerMap = new HashMap<>();
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < workersState.size(); i++) {
            workerMap.computeIfAbsent(workersState.get(i).getString(WorkerState.Fileds.HOST), x -> new ArrayList<>()).add(workersState.get(i));
        }
        return states.stream().filter(x -> x.getCategory().equals(category)).map(x -> x.setWorkers(workerMap.getOrDefault(x.getHost(), new ArrayList<>()))).sorted().collect(Collectors.toList());
    }

    /**
     * 检测应用worker 运行位置信息,如果运行位置错误, 那么将kill掉错误得worker
     */
    public static void checkWorkerLocation(String category) throws Exception {
        Map<String, BrokerState> nodeMaps = ZkUtils.getNodeMaps(ZkCurator.getInstance().getZkCurator());
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < workersState.size() && workersState.get(i).getString(WorkerState.Fileds.CATEGORY).equals(category); i++) {
            if (nodeMaps.containsKey(workersState.get(i).getString(WorkerState.Fileds.HOST)) && !workersState.get(i).getString(WorkerState.Fileds.CATEGORY).equals(nodeMaps.get(workersState.get(i).getString(WorkerState.Fileds.HOST)).getString(Environment.CLUSTER_CATEGORY))) {
                RemoteOptions.killWorker(workersState.get(i).getString(WorkerState.Fileds.HOST), workersState.get(i).getAppID(), workersState.get(i).getSeq(), workersState.get(i).getTotal());
                logger.info("[Tracker] Check position error worker <" + workersState.get(i).getString(WorkerState.Fileds.WORKER_ID) + "> was found and the kill was completed");
            }
        }
    }


    /**
     * 检测因异常重复多余的worker, 检查出来后进行调度重启
     */
    public static void rebalanceWorkers(String category) throws Exception {
        double average = ZkUtils.getAverageWorkers(ZkCurator.getInstance().getZkCurator(), category);
        for (AssetsState assetsState : DefaultScheduler.getAssetsState(category)) {
            for (int i = 0; i < (int)Math.floor(assetsState.getWorkerSize() - average); i++) {
                WorkerState workerState = assetsState.getWorkers().get(new Random().nextInt(assetsState.getWorkerSize()));
                RemoteOptions.killWorker(workerState.getString(WorkerState.Fileds.HOST), workerState.getAppID(), workerState.getSeq(), workerState.getTotal());
                logger.info("[Tracker] Rebalance worker <" + workerState.getString(WorkerState.Fileds.WORKER_ID) + "> was found and the kill was completed");
            }
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(DefaultScheduler.class);
}
