package org.cluster.application.tracker;

import org.apache.log4j.Logger;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.utils.UtilCommons;
import org.cluster.application.zookeeper.ZkCurator;
import org.cluster.application.zookeeper.ZkUtils;
import org.cluster.core.cluster.rpc.ClusterService;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/14 10:54
 * @Version: 1.0
 * @Description: TODO
 */
public class LoggerManager {

    /**
     * 获取数据信息
     */
    public static ConcurrentHashMap<String, Long> getLOGer() {
        return LoggerMemory.get();
    }

    /**
     * 添加数据信息
     */
    public static void setLOGer(String type, long arg0) {
        LoggerMemory.set(type, arg0);
    }

    /**
     * 添加指定集合的监听(每分钟输出大小)
     */
    public static void setCollection(String type, Collection collection) {
        CollectionMemory.set(type, collection);
    }

    /**
     * 启动监控信息
     */
    public static void launcher(EnvOptions options) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String data = UtilCommons.getWorkerState(options);
                    ZkUtils.update(ZkCurator.getInstance().getZkCurator(), options.getZkWorkerDir(options.getWorkerID()), data.getBytes());
                    state(options);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
        }, new Date(), 60 * 1000);
    }

    /**
     * 上传状态信息到broker
     *
     * @param options
     * @throws Exception
     */
    private static void state(EnvOptions options) throws Exception {
        Map<String, String> mapping = ZkUtils.getMapping(options);
        String port = mapping.get(options.getOptionValue(Environment.BROKER_HOST));
        if (port == null) return;
        ClusterService service = (ClusterService) Naming.lookup("rmi://" + InetAddress.getByName(options.getOptionValue(Environment.BROKER_HOST)).getHostAddress() + ":" + port + "/Broker");
        service.acceptState(options.getWorkerID(), UtilCommons.getExectorState(options, getLOGer()));
    }

    public static void main(String[] args) throws Exception {
        ClusterService service = (ClusterService) Naming.lookup("rmi://" + InetAddress.getByName("VM_16_2_centos").getHostAddress() + ":" + 8080 + "/Broker");
        //service.acceptState("123", "");
        System.out.println(service.getState());
    }
    /**
     * Log对象.
     */
    public static Logger logger = Logger.getLogger(LoggerManager.class);
}
