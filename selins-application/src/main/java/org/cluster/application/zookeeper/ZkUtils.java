package org.cluster.application.zookeeper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                continue;
            } else {
                logger.info("[Cluster] Zk Connecter [" + zkDir + "] is registered successfully.");
                return;
            }
        }
        throw new Exception("[Cluster] Zk Connecter [" + zkDir + "] is registered unsuccessfully.");
    }

    /**
     * 获取节点对应关系信息
     *
     * @return
     * @throws Exception
     */
    public static Map<String, String> getMapping(EnvOptions options) throws Exception {
        HashMap<String, String> mapping = new HashMap<>();
        JSONArray nodeJson = ZkUtils.getNodes(ZkCurator.getInstance().getZkCurator(), options);
        for (int i = 0; i < nodeJson.size(); i++) {
            mapping.put(nodeJson.getJSONObject(i).getString("host"), nodeJson.getJSONObject(i).getString("port"));
        }
        return mapping;
    }

    /**
     * 获取目前正在服务得节点信息
     */
    public static JSONArray getNodes(CuratorFramework curator, EnvOptions options) throws Exception {
        String zkDir = options.getOptionValue(Environment.ZK_ROOT_DIR) + "/ids";
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
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ZkUtils.class);
}
