package org.cluster.core.cluster.rpc;

import org.cluster.core.scheduler.*;
import org.cluster.core.commons.StateMemory;
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
public class ClusterServiceImpl extends UnicastRemoteObject implements ClusterService {

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
    protected ClusterServiceImpl() throws RemoteException {
    }

    /**
     * 启动指定得application 应用
     */
    @Override
    public void start(String appID) throws Exception {
        RemoteOptions.updateState(appID, 1);
    }

    /**
     * 启动应用的某个节点
     */
    @Override
    public void start(String appID, int seq, int total) throws Exception {
        LocalOptions.saveWorkerResources(appID, seq, total, RemoteOptions.getAppResources(appID));
        LocalOptions.startWorker(appID, seq, total);
    }

    /**
     * 停止指定得application 应用
     */
    @Override
    public void kill(String appID) throws Exception {
        RemoteOptions.updateState(appID, 0);
        StateMemory.getInstance().remove(appID);
    }

    /**
     * 停止应用的某个节点
     */
    @Override
    public void kill(String appID, int seq, int total) throws Exception {
        LocalOptions.killWorker(appID, seq, total);
    }

    /**
     * 获取本机器的各个worker上报的状态信息
     */
    @Override
    public String getState() throws Exception {
        return StateMemory.getInstance().poll();
    }

    /**
     * 接收本机器的各个worker上报的状态信息
     *
     * @param workerID
     * @param state
     */
    @Override
    public void acceptState(String workerID, String state) throws Exception {
        LocalOptions.acceptState(workerID, state);
    }

    /**
     * 停止应用的某个节点
     */
    @Override
    public void rebalance(String category) throws Exception {
        LocalOptions.rebalance(category);
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
}
