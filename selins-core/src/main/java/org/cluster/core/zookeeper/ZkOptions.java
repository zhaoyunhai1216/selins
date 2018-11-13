package org.cluster.core.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.scheduler.AssetsState;
import org.cluster.core.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/9 14:43
 * @Version: 1.0
 * @Description: TODO
 */
public class ZkOptions {
    /**
     * 创建zookeeper节点目录
     */
    public static boolean create(CuratorFramework curator, String path, byte[] data, CreateMode mode) {
        // 创建服务根目录
        try {
            curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
            return true;
        } catch (KeeperException.NodeExistsException e) {
            return false;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建zookeeper节点目录
     */
    public static boolean update(CuratorFramework curator, String path, byte[] data) {
        // 创建服务根目录
        try {
            curator.setData().forPath(path, data);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 构建Application的zookeeper路径
     */
    public static void build(String zkDir, String data) throws Exception {
        for (int i = 0; i < 60; i++) {
            boolean state = ZkOptions.create(ZkConnector.getInstance().getZkCurator(), zkDir, data.getBytes(), CreateMode.EPHEMERAL);
            if (!state) {
                logger.info("[Cluster] retry after 1 second.");
                TimeUnit.MILLISECONDS.sleep(1000);
                continue;
            } else {
                logger.info("[Cluster] Zk Connecter [" + zkDir + "] is registered successfully.");
                return;
            }
        }
        throw new Exception("[Cluster] Zk Connecter [" + zkDir + "] is registered unsuccessfully.");
    }

    /**
     * 构建Master的zookeeper路径
     */
    public static void initZkTrackerDir() throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/ids/" + Configuration.getInstance().getConf().getString("brokerID");
        ZkOptions.build(zkDir, UtilCommons.getBrokerState());
    }

    /**
     * 构建Master的zookeeper路径
     */
    public static void updateBrokerState() throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/ids/" + Configuration.getInstance().getConf().getString("brokerID");
        ZkOptions.update(ZkConnector.getInstance().getZkCurator(), zkDir, UtilCommons.getBrokerState().getBytes());
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static String getMaster(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = curator.getChildren().forPath(zkDir);
        if (childs.size() == 0) {
            throw new Exception("No master node exists.");
        }
        JSONObject masterJson = new JSONObject();
        long min_timestamp = Long.MAX_VALUE;
        for (String child : childs) {
            JSONObject json = JSONObject.parseObject(new String(curator.getData().forPath(zkDir + "/" + child)));
            if (json.getLong("startTime") < min_timestamp) {
                masterJson = json;
            }
        }
        return masterJson.toJSONString();
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static boolean isMaster(CuratorFramework curator) throws Exception {
        String host = JSONObject.parseObject(getMaster(curator)).getString("host");
        String port = JSONObject.parseObject(getMaster(curator)).getString("port");
        return Configuration.getInstance().getConf().getString("cluster.host").equals(host) && Configuration.getInstance().getConf().getString("cluster.port").equals(port);
    }

    /**
     * 获取节点对应关系信息
     *
     * @return
     * @throws Exception
     */
    public static String getMapping(String host) throws Exception {
        Map<String, String> mapping = ZkOptions.getMapping();
        String port = mapping.get(host);
        if (port == null) throw new Exception("This node is not available.");
        return port;
    }

    /**
     * 获取节点对应关系信息
     *
     * @return
     * @throws Exception
     */
    public static Map<String, String> getMapping() throws Exception {
        HashMap<String, String> mapping = new HashMap<>();
        JSONArray nodeJson = ZkOptions.getNodes(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < nodeJson.size(); i++) {
            mapping.put(nodeJson.getJSONObject(i).getString("host"), nodeJson.getJSONObject(i).getString("port"));
        }
        return mapping;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static JSONArray getNodes(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/ids";
        List<String> childs = curator.getChildren().forPath(zkDir);
        if (childs.size() == 0) {
            throw new Exception("No node exists.");
        }
        JSONArray nodeJson = new JSONArray();
        for (String child : childs) {
            JSONObject brokerJSON = JSONObject.parseObject(new String(curator.getData().forPath(zkDir + "/" + child)));
            brokerJSON.put("brokerID", child);
            nodeJson.add(brokerJSON);
        }
        return nodeJson;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<AssetsState> getNodeState(CuratorFramework curator) throws Exception {
        JSONArray nodes = ZkOptions.getNodes(ZkConnector.getInstance().getZkCurator());
        List<AssetsState> nodeStates = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeStates.add(new AssetsState(nodes.getJSONObject(i).getString("brokerID"), nodes.getJSONObject(i).getString("host")
                    , nodes.getJSONObject(i).getInteger("port"), nodes.getJSONObject(i).getString("category")));
        }
        return nodeStates;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static JSONObject getWorker(CuratorFramework curator, String workerID) throws Exception {
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        for (int i = 0; i < workerJson.size(); i++) {
            if (workerJson.getJSONObject(i).getString("workerId").equals(workerID)) {
                return workerJson.getJSONObject(i);
            }
        }
        throw new Exception("<" + workerID + "> This node is not available.");
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<String> getWorkerID(CuratorFramework curator) throws Exception {
        JSONArray workerJson = ZkOptions.getWorkers(ZkConnector.getInstance().getZkCurator());
        List<String> workerList = new ArrayList<>();
        for (int i = 0; i < workerJson.size(); i++) {
            workerList.add(workerJson.getJSONObject(i).getString("workerId"));
        }
        return workerList;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static JSONArray getWorkers(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/worker";
        List<String> childs = curator.getChildren().forPath(zkDir);
        JSONArray nodeJson = new JSONArray();
        for (String child : childs) {
            nodeJson.add(JSONObject.parseObject(new String(curator.getData().forPath(zkDir + "/" + child))));
        }
        return nodeJson;
    }

    /**
     * 检查节点是否存在
     */
    public static boolean checkWorkerExists(CuratorFramework curator, String workerUid) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/worker/" + workerUid;
        return curator.checkExists().forPath(zkDir) != null;
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static String getAppStore(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appmeta";
        return JSONObject.parseObject(new String(curator.getData().forPath(zkDir))).toJSONString();
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<AppResource> getApplications(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appstore";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<AppResource> applications = new ArrayList<>();
        for (String child : childs) {
            applications.add(JSONObject.parseObject(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child), AppResource.class));

        }
        return applications;
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<String> getRunningApplicationsID(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appstore";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<String> applications = new ArrayList<>();
        for (String child : childs) {
            AppResource res = JSONObject.parseObject(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child), AppResource.class);
            if (res.getState() != 1) continue;
            applications.add(res.getId());

        }
        return applications;
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<AppResource> getRunningApplications(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appstore";
        List<String> childs = ZkConnector.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<AppResource> applications = new ArrayList<>();
        for (String child : childs) {
            AppResource res = JSONObject.parseObject(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child), AppResource.class);
            if (res.getState() != 1) continue;
            applications.add(res);

        }
        return applications;
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static AppResource getAppZkResource(String appid) throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appstore/" + appid;
        return JSONObject.parseObject(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir), AppResource.class);
    }

    /**
     * @return
     * @throws Exception
     */
    public static JSONObject getAppMetaJson() throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appmeta";
        return JSONObject.parseObject(new String(ZkConnector.getInstance().getZkCurator().getData().forPath(zkDir)));
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ZkOptions.class);
}
