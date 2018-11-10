package org.cluster.core.cluster.rpc;

import java.io.IOException;
import java.rmi.Remote;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/29 14:32
 * @Version: 1.0
 * @Description: TODO
 */
public interface ClusterService extends Remote {
    /**
     * 启动指定得application 应用
     */
    void start(String appID) throws Exception;

    /**
     * 停止指定得application 应用
     */
    void kill(String appID) throws Exception;

    /**
     * 启动应用的某个节点
     */
    void start(String appID, int index, int total) throws Exception;

    /**
     * 停止应用的某个节点
     */
    void kill(String appid, int index, int total) throws Exception;

    /**
     * 获取本机器的各个worker上报的状态信息
     */
    String getState() throws Exception;

    /**
     * 接收本机器的各个worker上报的状态信息
     */
    void acceptState(String workerID, String state) throws Exception;
}
