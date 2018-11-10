package org.cluster.core.scheduler;

import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.cluster.rpc.ClusterServiceImpl;
import org.cluster.core.zookeeper.ZkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/2 14:10
 * @Version: 1.0
 * @Description: 负载均衡及任务分配
 */
public class DefaultScheduler {

    /**
     * 默认调度规划方法, 把指定引用的所有worker平均分配到集群的每个节点, 采用平均分配的模式进行分配
     *
     * @throws Exception
     */
    public void assignments(String appID) throws Exception {

    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
}
