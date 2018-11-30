package org.cluster.core.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.backtype.bean.BrokerState;
import org.cluster.core.backtype.bean.AppStore;
import org.cluster.core.backtype.bean.WorkerState;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.backtype.bean.AssetsState;
import org.cluster.core.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/9 14:43
 * @Version: 1.0
 * @Description: TODO
 */
public class ZkUtils {
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
            boolean state = ZkUtils.create(ZkCurator.getInstance().getZkCurator(), zkDir, data.getBytes(), CreateMode.EPHEMERAL);
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
    public static void initBrokerState() throws Exception {
        String address = Configuration.getInstance().getString(Environment.CLUSTER_HOST) + ":" + Configuration.getInstance().getString(Environment.CLUSTER_PORT);
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/ids/" + address;
        ZkUtils.build(zkDir, UtilCommons.getBrokerState());
    }

    /**
     * 更新节点得zookeeper存储内容到最新
     */
    public static void updateBrokerState() throws Exception {
        String address = Configuration.getInstance().getString(Environment.CLUSTER_HOST) + ":" + Configuration.getInstance().getString(Environment.CLUSTER_PORT);
        String zkDir =  Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/ids/" + address;
        ZkUtils.update(ZkCurator.getInstance().getZkCurator(),zkDir, UtilCommons.getBrokerState().getBytes());
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static BrokerState getMaster(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/ids";
        List<String> childs = curator.getChildren().forPath(zkDir);
        if (childs.size() == 0) {
            throw new Exception("No master node exists.");
        }
        BrokerState master = null;
        long min_czxid = Long.MAX_VALUE;
        for (String child : childs) {
            Stat stat = curator.checkExists().forPath(zkDir + "/" + child);
            if (stat == null) continue;
            JSONObject json = JSONObject.parseObject(new String(curator.getData().forPath(zkDir + "/" + child)));
            if (stat.getCzxid() < min_czxid) {
                master = BrokerState.parse(json.toJSONString());
            }
        }
        return master;
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static boolean isMaster(CuratorFramework curator) throws Exception {
        String host = getMaster(curator).getString(Environment.CLUSTER_HOST);
        int port = getMaster(curator).getInteger(Environment.CLUSTER_PORT);
        return Configuration.getInstance().getString(Environment.CLUSTER_HOST).equals(host) && Configuration.getInstance().getInteger(Environment.CLUSTER_PORT)== port;
    }

    /**
     * 获取节点对应关系信息
     *
     * @return
     * @throws Exception
     */
    public static int getMapping(String host) throws Exception {
        Map<String, Integer> mapping = ZkUtils.getMapping();
        if (!mapping.containsKey(host)) throw new Exception("This node is not available.");
        return mapping.get(host);
    }

    /**
     * 获取节点对应关系信息
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Integer> getMapping() throws Exception {
        HashMap<String, Integer> mapping = new HashMap<>();
        List<BrokerState> brokersState = ZkUtils.getNodes(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < brokersState.size(); i++) {
            mapping.put(brokersState.get(i).getString(Environment.CLUSTER_HOST), brokersState.get(i).getInteger(Environment.CLUSTER_PORT));
        }
        return mapping;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<BrokerState> getNodes(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/ids";
        List<String> childs = curator.getChildren().forPath(zkDir);
        if (childs.size() == 0) {
            throw new Exception("No node exists.");
        }
        List<BrokerState> brokersState = new ArrayList<BrokerState>();
        for (String child : childs) {
            brokersState.add(BrokerState.parse(new String(curator.getData().forPath(zkDir + "/" + child))));
        }
        return brokersState;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static Map<String, BrokerState> getNodeMaps(CuratorFramework curator) throws Exception {
        List<BrokerState> brokersState = ZkUtils.getNodes(ZkCurator.getInstance().getZkCurator());
        HashMap<String, BrokerState> nodeMaps = new HashMap<String, BrokerState>();
        for (int i = 0; i < brokersState.size(); i++) {
            nodeMaps.put(brokersState.get(i).getString(Environment.CLUSTER_HOST), brokersState.get(i));
        }
        return nodeMaps;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<AssetsState> getNodeState(CuratorFramework curator) throws Exception {
        List<BrokerState> brokersState = ZkUtils.getNodes(ZkCurator.getInstance().getZkCurator());
        List<AssetsState> nodeStates = new ArrayList<>();
        for (int i = 0; i < brokersState.size(); i++) {
            nodeStates.add(new AssetsState(brokersState.get(i).getString(Environment.CLUSTER_HOST), brokersState.get(i).getInteger(Environment.CLUSTER_PORT), brokersState.get(i).getString(Environment.CLUSTER_CATEGORY)));
        }
        return nodeStates;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static WorkerState getWorker(CuratorFramework curator, String workerID) throws Exception {
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        for (int i = 0; i < workersState.size(); i++) {
            if (workersState.get(i).getString(WorkerState.Fileds.WORKER_ID).equals(workerID)) {
                return workersState.get(i);
            }
        }
        throw new Exception("<" + workerID + "> The worker has been destroyed.");
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<String> getWorkerID(CuratorFramework curator) throws Exception {
        List<WorkerState> workersState = ZkUtils.getWorkers(ZkCurator.getInstance().getZkCurator());
        List<String> workerList = new ArrayList<>();
        for (int i = 0; i < workersState.size(); i++) {
            workerList.add(workersState.get(i).getString(WorkerState.Fileds.WORKER_ID));
        }
        return workerList;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static double getAverageWorkers(CuratorFramework curator, String category) throws Exception {
        List<BrokerState> brokersState = ZkUtils.getNodes(curator);
        List<AppResource> applications = ZkUtils.getRunningApplications(curator, category);
        return applications.stream().mapToDouble(x -> x.getInteger(AppResource.Fileds.NUM_WORKERS)).sum() / brokersState.size();
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static List<WorkerState> getWorkers(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/worker";
        List<String> childs = curator.getChildren().forPath(zkDir);
        List<WorkerState> workers = new ArrayList<>();
        for (String child : childs) {
            workers.add(WorkerState.parse(new String(curator.getData().forPath(zkDir + "/" + child))));
        }
        return workers;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static Map<String, WorkerState> getWorkerByBroker(CuratorFramework curator) throws Exception {
        List<WorkerState> workersState = ZkUtils.getWorkers(curator);
        HashMap<String, WorkerState> workerMaps = new HashMap<>();
        for (int i = 0; i < workersState.size(); i++) {
            workerMaps.put(workersState.get(i).getString(WorkerState.Fileds.WORKER_ID), workersState.get(i));
        }
        return workerMaps;
    }

    /**
     * 检查节点是否存在
     */
    public static boolean checkWorkerExists(CuratorFramework curator, String workerUid) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/worker/" + workerUid;
        return curator.checkExists().forPath(zkDir) != null;
    }

    /**
     * 获取目前正在服务得主节点信息
     */
    public static AppStore getAppStore(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/appstore";
        return AppStore.parse(new String(curator.getData().forPath(zkDir)));
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<AppResource> getApplications(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<AppResource> applications = new ArrayList<>();
        for (String child : childs) {
            applications.add(JSONObject.parseObject(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child), AppResource.class));

        }
        return applications;
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<String> getRunningApplicationsID(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<String> applications = new ArrayList<>();
        for (String child : childs) {
            AppResource res = AppResource.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            if (res.getInteger(AppResource.Fileds.STATE) != 1) continue;
            applications.add(res.getString(AppResource.Fileds.ID));

        }
        return applications;
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<AppResource> getRunningApplications(CuratorFramework curator) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications";
        List<String> childs = ZkCurator.getInstance().getZkCurator().getChildren().forPath(zkDir);
        ArrayList<AppResource> applications = new ArrayList<>();
        for (String child : childs) {
            AppResource res = AppResource.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir + "/" + child)));
            if (res.getInteger(AppResource.Fileds.STATE) != 1) continue;
            applications.add(res);

        }
        return applications;
    }

    /**
     * 获取所有部署的application应用列表信息, 然后返回信息列表，提供给后续使用
     */
    public static List<AppResource> getRunningApplications(CuratorFramework curator, String category) throws Exception {
        List<AppResource> applications = ZkUtils.getRunningApplications(curator);
        return applications.stream().filter(x -> x.getString(AppResource.Fileds.CATEGORY).equals(category)).collect(Collectors.toList());
    }

    /**
     * 在zookeeper集群中, 获取application的运行参数信息, 然后保存到内容中用于调度提供参数
     */
    public static AppResource getAppZkResource(String appid) throws Exception {
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications/" + appid;
        return JSONObject.parseObject(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir), AppResource.class);
    }


    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ZkUtils.class);
}
