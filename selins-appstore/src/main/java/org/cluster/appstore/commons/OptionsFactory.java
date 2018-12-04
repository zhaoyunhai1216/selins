package org.cluster.appstore.commons;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.CreateMode;
import org.cluster.appstore.utils.UtilCommons;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
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
public class OptionsFactory {
    /**
     * 发布应用文件到本地,进行持久化保存, 分发时进行同步处理, 同步到其他运算节点
     *
     * @param applicationID
     * @param repo
     * @throws IOException
     */
    public static void createResources(String applicationID, byte[] repo) throws IOException {
        String dir = Configuration.getInstance().getString(Environment.APPSTORE_DIR) + "/" + applicationID + ".zip";
        FileUtils.writeByteArrayToFile(new File(dir), repo);
    }

    /**
     * 删除本地缓存的应用文件, 永久化删除,删除后不能再次恢复
     *
     * @param applicationID
     */
    public static void deleteResources(String applicationID) {
        String dir = Configuration.getInstance().getString(Environment.APPSTORE_DIR) + "/" + applicationID + ".zip";
        FileUtils.deleteQuietly(new File(dir));
    }

    /**
     * 读取本地缓存的应用文件信息, 获取到二进制byte数组, 后做后续处理
     *
     * @param applicationID
     */
    public static byte[] searchResources(String applicationID) throws IOException {
        String dir = Configuration.getInstance().getString(Environment.APPSTORE_DIR) + "/" + applicationID + ".zip";
        return FileUtils.readFileToByteArray(new File(dir));
    }

    /**
     * 发布应用文件到zookeeper,进行持久化保存, 分发时进行同步处理, 同步到其他运算节点
     *
     * @param applicationID
     * @throws IOException
     */
    public static boolean createZkResources(String applicationID, String clazz, String jvmOpts, int numWorkers, String category) {
        String dir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications/" + applicationID;
        AppResource resource = new AppResource(applicationID, UtilCommons.getAppName(clazz), jvmOpts, clazz, numWorkers, 0, category);
        return ZkUtils.create(ZkCurator.getInstance().getZkCurator(), dir, resource.toString().getBytes(), CreateMode.PERSISTENT);
    }

    /**
     * 删除ZK缓存的应用文件, 永久化删除,删除后不能再次恢复
     *
     * @param applicationID
     */
    public static void deleteZkResources(String applicationID) throws Exception {
        String dir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications/" + applicationID;
        ZkCurator.getInstance().getZkCurator().delete().forPath(dir);
    }

    /**
     * 更新状态信息
     *
     * @param applicationID
     * @param state
     * @throws Exception
     */
    public static void updateState(String applicationID, int state) throws Exception {
        String dir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/applications/" + applicationID;
        AppResource resource = AppResource.parse(new String(ZkCurator.getInstance().getZkCurator().getData().forPath(dir)));
        resource.put(AppResource.Fileds.STATE, state);
        ZkUtils.update(ZkCurator.getInstance().getZkCurator(), dir, resource.toString().getBytes());
    }
}
