package org.cluster.core.cluster.rpc;

import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/29 14:32
 * @Version: 1.0
 * @Description: TODO
 */
public class ClusterRMI {
    /**
     * 启用相关远程调用服务，可用于对提交的任务进行控制
     */
    public static void init() throws RemoteException, MalformedURLException, UnknownHostException {
        String host = Configuration.getInstance().getString(Environment.CLUSTER_HOST);
        int port = Configuration.getInstance().getInteger(Environment.CLUSTER_PORT);
        System.setProperty("java.rmi.server.hostname", host);
        LocateRegistry.createRegistry(port);
        Naming.rebind("rmi://" + InetAddress.getByName(host).getHostAddress() + ":" + port + "/Broker", new ClusterServiceImpl());
        logger.info("[Cluster] Rpc [" + host + ":" + port + "] is start-up successful.");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ClusterRMI.class);
}
