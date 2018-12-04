package org.cluster.core.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/12/3 15:04
 * @Version: 1.0
 * @Description: TODO
 */
public enum ZkSequence {
    INSTANCE; //枚举实现单例模式, 此为当前对象的单例

    private String path;

    ZkSequence() {
        path = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/seqid";
        ZkUtils.create(ZkCurator.getInstance().getZkCurator(), path, new byte[0], CreateMode.PERSISTENT);
    }

    /**
     * 获得zookeeper连接
     */
    public static ZkSequence getZkSequence() {
        return INSTANCE;
    }

    /**
     * 获取唯一id信息
     */
    public int getId() throws Exception {
        return ZkCurator.getInstance().getZkCurator().setData().forPath(path, new byte[0]).getVersion();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ZkSequence.class);
}
