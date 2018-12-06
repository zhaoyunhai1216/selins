package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.cluster.core.backtype.bean.WorkerState;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.commons.StateMemory;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/9 14:40
 * @Version: 1.0
 * @Description: TODO
 */
public class LocalOptions {

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void startWorker(String applicationID, int seq, int total) throws Exception {
        String[] parameters = UtilCommons.getWorkerParameters(applicationID, seq, total);
        UtilCommons.startCommand(applicationID + "_" + seq + "_" + total, parameters);
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void killWorker(String applicationID, int seq, int total) throws Exception {
        UtilCommons.killCommand(getProcessID(applicationID, seq, total));
        LocalOptions.deleteWorkerResources(applicationID, seq, total);
    }

    /**
     * 在zookeeper集群中，查询指定worker, 并获取worker的执行进程的进程编号.
     */
    public static String getProcessID(String applicationID, int seq, int total) throws Exception {
        WorkerState workerState = ZkUtils.getWorker(applicationID + "_" + seq + "_" + total);
        return workerState.getString(WorkerState.Fileds.PROCESS);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static void saveWorkerResources(String applicationID, int seq, int total, byte[] repo) throws Exception {
        String workerDir = Configuration.getInstance().getString(Environment.WORKER_DIR);
        FileUtils.deleteDirectory(new File(workerDir + applicationID));
        UtilCommons.unZipDirectory(workerDir + applicationID + "_" + seq + "_" + total, repo);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static void deleteWorkerResources(String applicationID, int seq, int total) throws Exception {
        String workDir = Configuration.getInstance().getString(Environment.WORKER_DIR);
        FileUtils.deleteDirectory(new File(workDir + applicationID + "_" + seq + "_" + total));
    }


    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void rebalance(String category) throws Exception {
        DefaultScheduler.checkWorkerLocation(category);
        DefaultScheduler.rebalanceWorkers(category);
    }

    /**
     * 接收本机器的各个worker上报的状态信息, 并缓存到StateMemory中, 可以进行查询信息
     */
    public static void acceptState(String workerID, JSONObject state) throws Exception {
        for (String component : state.keySet()) {
            acceptState(workerID.split("\\_")[0], component, state.getJSONArray(component));
        }
    }

    /**
     * 接收本机器的各个worker上报的状态信息, 并缓存到StateMemory中, 可以进行查询信息
     */
    public static void acceptState(String applicationID, String componentID, JSONArray state) throws Exception {
        for (int i = 0; i < state.size(); i++) {
            StateMemory.getInstance().offer(applicationID, componentID, state.getJSONObject(i));
        }
    }


    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(RemoteOptions.class);
}
