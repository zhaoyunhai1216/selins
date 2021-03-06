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
    void deploy(String jvmOpts, String clazz, int numWorkers, String category, byte[] repo) throws Exception;

    /**
     * 销毁指定得application 应用
     */
    void destroy(String applicationID) throws Exception;

    /**
     * 同步获取指定的应用执行文件夹
     */
    byte[] getResources(String applicationID) throws Exception;

    /**
     * application 应用更新状态
     */
    void updateState(String applicationID, int state) throws Exception;
}
