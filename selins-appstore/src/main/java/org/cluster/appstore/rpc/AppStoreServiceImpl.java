package org.cluster.appstore.rpc;

import com.alibaba.fastjson.JSONObject;
import org.cluster.appstore.commons.AppsOptions;
import org.cluster.appstore.utils.UtilCommons;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/29 14:37
 * @Version: 1.0
 * @Description: TODO
 */
public class AppStoreServiceImpl extends UnicastRemoteObject implements AppStoreService {

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     *
     * <p>The object is exported with a server socket
     * created using the {@link} class.
     *
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    protected AppStoreServiceImpl() throws RemoteException {
    }

    /**
     * 提交指定的app到集群Nimbus中进行调度分发
     */
    @Override
    public void deploy(String jvmOpts, String clazz, int numWorkers, String category, byte[] repo) throws Exception {
        String appID = UtilCommons.getAppName(clazz) + "-" + UtilCommons.getId();
        AppsOptions.createResources(appID, repo);
        AppsOptions.createZkResources(appID, clazz, jvmOpts, numWorkers, category);
        logger.info("The application <" + appID + "> was successfully deploy");
    }

    /**
     * 销毁指定得application 应用
     */
    @Override
    public void destroy(String appID) throws Exception {
        AppsOptions.deleteResources(appID);
        AppsOptions.deleteZkResources(appID);
        logger.info("The application <" + appID + "]> was successfully destroy");
    }

    /**
     * 同步获取指定的应用执行文件夹
     *
     * @param appID
     */
    @Override
    public byte[] getResources(String appID) throws Exception {
        byte[] b = AppsOptions.searchResources(appID);
        logger.info("The application <" + appID + "]> was successfully sync.");
        return b;
    }

    /**
     * application 应用更新状态
     *
     * @param appID
     * @param state
     */
    @Override
    public void updateState(String appID, int state) throws Exception {
        AppsOptions.updateState(appID, state);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(AppStoreServiceImpl.class);
}
