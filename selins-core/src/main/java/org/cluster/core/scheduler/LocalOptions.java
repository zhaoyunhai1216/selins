package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.StateMemory;
import org.cluster.core.utils.UtilCommons;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
    public static void startWorker(String appID, int seq, int total) throws Exception {
        String workDir = Configuration.getInstance().getConf().getString("cluster.worker.dir") + appID + "_" + seq + "_" + total;
        AppResource res = ZkOptions.getAppZkResource(appID);
        String host = Configuration.getInstance().getConf().getString("cluster.host");
        UtilCommons.startCommand(Configuration.getBaseDir() + "/bin", workDir, host, appID, res.getAppMain(), res.getJvmOpts(), seq, total, Configuration.getBaseDir() + "/etc");
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static void killWorker(String appID, int seq, int total) throws Exception {
        JSONObject workerJson = null;
        try {
            workerJson = ZkOptions.getWorker(ZkConnector.getInstance().getZkCurator(), appID + "_" + seq + "_" + total);
            UtilCommons.killCommand(workerJson.getString("process"));
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        LocalOptions.deleteWorkerResources(appID, seq, total);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static void saveWorkerResources(String appID, int seq, int total, byte[] zipBytes) throws Exception {
        String workdir = Configuration.getInstance().getConf().getString("cluster.worker.dir");
        FileUtils.deleteDirectory(new File(workdir + appID));
        UtilCommons.unZipDirectory(workdir + appID + "_" + seq + "_" + total, zipBytes);
    }

    /**
     * 在集群的appstore中, 同步application的运行文件信息,然后解压存储到本地用于启动运行
     */
    public static void deleteWorkerResources(String appID, int seq, int total) throws Exception {
        String workDir = Configuration.getInstance().getConf().getString("cluster.worker.dir");
        FileUtils.deleteDirectory(new File(workDir + appID + "_" + seq + "_" + total));
    }

    /**
     * 接收本机器的各个worker上报的状态信息
     *
     * @param workerID
     * @param state
     * @throws Exception
     */
    public static void acceptState(String workerID, String state) throws Exception {
        JSONObject stateJson = JSONObject.parseObject(state);
        for (String key : stateJson.keySet()) {
            JSONArray exectorJson = stateJson.getJSONArray(key);
            for (int i = 0; i < exectorJson.size(); i++) {
                String exectorID = exectorJson.getJSONObject(i).getString("seq") + "-" + exectorJson.getJSONObject(i).getString("total");
                StateMemory.getInstance().offer(workerID.split("\\_")[0], key, exectorID, exectorJson.getJSONObject(i).toJSONString());
            }
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(RemoteOptions.class);
}
