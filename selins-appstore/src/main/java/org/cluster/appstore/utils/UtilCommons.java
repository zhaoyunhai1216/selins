package org.cluster.appstore.utils;

import org.cluster.core.backtype.bean.AppStorePojo;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkSequence;
import org.cluster.core.zookeeper.ZkUtils;
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
     *
     * @param clazz
     */
    public static String getAppName(String clazz) {
        return clazz.split("\\.")[clazz.split("\\.").length - 1];
    }

    /**
     * 根据mianclass 的描述, 获取Appname信息
     *
     * @param clazz
     */
    public static String getApplicationID(String clazz) throws Exception {
        return  UtilCommons.getAppName(clazz) + "-" + ZkSequence.getZkSequence().getId();
    }


    /**
     * 构建Master的zookeeper路径
     */
    public static void initAppstoreDir() throws Exception {
        String dir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/appstore";
        ZkUtils.build(dir, new AppStorePojo().toString());
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(UtilCommons.class);
}
