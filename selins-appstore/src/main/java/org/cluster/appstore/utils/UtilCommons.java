package org.cluster.appstore.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.CreateMode;
import org.cluster.core.commons.Configuration;
import org.cluster.core.zookeeper.ZkConnector;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/18 10:16
 * @Version: 1.0
 * @Description: TODO
 */
public class UtilCommons {
    /**
     * 根据mianclass 的描述, 获取Appname信息
     * @param mainClass
     */
    public static String getAppName(String mainClass){
        return mainClass.split("\\.")[mainClass.split("\\.").length - 1];
    }

    /**
     * 构建Master的zookeeper路径
     */
    public static void initZkMetaDir() throws Exception {
        String zkDir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/appmeta";
        ZkOptions.build(zkDir, new JSONObject()
                .fluentPut("host", Configuration.getInstance().getConf().getString("cluster.host"))
                .fluentPut("port", Configuration.getInstance().getConf().getInteger("cluster.appstore.port"))
                .fluentPut("startTime", System.currentTimeMillis()).toJSONString());
    }

    /**
     * 获取唯一id信息
     * @return
     * @throws Exception
     */
    public static int getId() throws Exception {
        String dir = Configuration.getInstance().getConf().getString("cluster.zookeeper.root") + "/seq";
        String seqJson = "{\"timestamp\":"+System.currentTimeMillis()+"}";
        ZkOptions.create(ZkConnector.getInstance().getZkCurator(),dir,seqJson.getBytes(), CreateMode.PERSISTENT);
        return ZkConnector.getInstance().getZkCurator().setData().forPath(dir, seqJson.getBytes()).getVersion();
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(UtilCommons.class);
}
