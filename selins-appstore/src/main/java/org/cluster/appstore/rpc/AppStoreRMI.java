package org.cluster.appstore.rpc;

import org.cluster.core.commons.Configuration;
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
public class AppStoreRMI {
    /**
     * 启用相关远程调用服务，可用于对提交的任务进行控制
     */
    public static void init() throws RemoteException, MalformedURLException, UnknownHostException {
        String host = Configuration.getInstance().getString("cluster.host");
        int port = Configuration.getInstance().getInteger("cluster.appstore.port");
        System.setProperty("java.rmi.server.hostname", host);
        LocateRegistry.createRegistry(port);
        Naming.rebind("rmi://"+InetAddress.getByName(host).getHostAddress()+":" + port + "/AppStore", new AppStoreServiceImpl());
        logger.info("[Cluster] RMI [" + host + ":" + port + "] is start-up successfully.");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(AppStoreRMI.class);
}
