package org.cluster.appstore.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.CreateMode;
import org.cluster.appstore.utils.UtilCommons;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;

import java.io.File;
import java.io.IOException;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/2 10:37
 * @Version: 1.0
 * @Description: TODO
 */
public class AppsOptions {
    /**
     * 发布应用文件到本地,进行持久化保存, 分发时进行同步处理, 同步到其他运算节点
     *
     * @param appID
     * @param b
     * @throws IOException
     */
    public static void createResources(String appID, byte[] b) throws IOException {
        String url = Configuration.getInstance().getString("cluster.appstore.dir") + "/" + appID + ".zip";
        FileUtils.writeByteArrayToFile(new File(url), b);
    }

    /**
     * 删除本地缓存的应用文件, 永久化删除,删除后不能再次恢复
     *
     * @param appID
     */
    public static void deleteResources(String appID) {
        String url = Configuration.getInstance().getString("cluster.appstore.dir") + "/" + appID + ".zip";
        FileUtils.deleteQuietly(new File(url));
    }

    /**
     * 读取本地缓存的应用文件信息, 获取到二进制byte数组, 后做后续处理
     *
     * @param appID
     */
    public static byte[] searchResources(String appID) throws IOException {
        String url = Configuration.getInstance().getString("cluster.appstore.dir") + "/" + appID + ".zip";
        return FileUtils.readFileToByteArray(new File(url));
    }

    /**
     * 发布应用文件到zookeeper,进行持久化保存, 分发时进行同步处理, 同步到其他运算节点
     *
     * @param appID
     * @throws IOException
     */
    public static boolean createZkResources(String appID, String mainClass, String jvmOpts, int numWorkers, String category) {
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/applications/" + appID;
        AppResource meta = new AppResource(appID, UtilCommons.getAppName(mainClass), jvmOpts, mainClass, numWorkers, 0, category);
        return ZkUtils.create(ZkCurator.getInstance().getZkCurator(), zkDir, meta.toString().getBytes(), CreateMode.PERSISTENT);
    }

    /**
     * 删除ZK缓存的应用文件, 永久化删除,删除后不能再次恢复
     *
     * @param appID
     */
    public static void deleteZkResources(String appID) throws Exception {
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/applications/" + appID;
        ZkCurator.getInstance().getZkCurator().delete().forPath(zkDir);
    }

    /**
     * 更新状态信息
     *
     * @param appID
     * @param state
     * @throws Exception
     */
    public static void updateState(String appID, int state) throws Exception {
        String zkDir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/applications/" + appID;
        JSONObject json = JSONObject.parseObject(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(zkDir)));
        json.put("state", state);
        ZkUtils.update(ZkCurator.getInstance().getZkCurator(), zkDir, json.toJSONString().getBytes());
    }
}
