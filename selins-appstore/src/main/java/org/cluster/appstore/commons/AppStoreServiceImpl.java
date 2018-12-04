package org.cluster.appstore.commons;

import org.cluster.appstore.utils.UtilCommons;
import org.cluster.core.cluster.rpc.AppStoreService;
import org.cluster.core.zookeeper.ZkSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
        String applicationID = UtilCommons.getApplicationID(clazz);
        OptionsFactory.createResources(applicationID, repo);
        OptionsFactory.createZkResources(applicationID, clazz, jvmOpts, numWorkers, category);
        logger.info("The application <" + applicationID + "> was successfully deploy");
    }

    /**
     * 销毁指定得application 应用
     */
    @Override
    public void destroy(String applicationID) throws Exception {
        OptionsFactory.deleteResources(applicationID);
        OptionsFactory.deleteZkResources(applicationID);
        logger.info("The application <" + applicationID + "]> was successfully destroy");
    }

    /**
     * 同步获取指定的应用执行文件夹
     *
     * @param applicationID
     */
    @Override
    public byte[] getResources(String applicationID) throws Exception {
        byte[] repo = OptionsFactory.searchResources(applicationID);
        logger.info("The application <" + applicationID + "]> was successfully sync.");
        return repo;
    }

    /**
     * application 应用更新状态
     *
     * @param applicationID
     * @param state
     */
    @Override
    public void updateState(String applicationID, int state) throws Exception {
        OptionsFactory.updateState(applicationID, state);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(AppStoreServiceImpl.class);
}
