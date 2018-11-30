package org.cluster.core.cluster.rpc;

import java.rmi.Remote;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/29 14:32
 * @Version: 1.0
 * @Description: TODO
 */
public interface AppStoreService extends Remote {
    /**
     * 提交指定的app到集群Master中进行调度分发
     */
    void deploy(String jvmOpts, String appMain, int numWorkers, String category, byte[] b) throws Exception;

    /**
     * 销毁指定得application 应用
     */
    void destroy(String appID) throws Exception;

    /**
     * 同步获取指定的应用执行文件夹
     */
    byte[] getResources(String appID) throws Exception;

    /**
     * application 应用更新状态
     */
    void updateState(String appID, int state) throws Exception;
}
