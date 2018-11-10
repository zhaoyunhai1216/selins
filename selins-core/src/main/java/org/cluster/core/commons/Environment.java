package org.cluster.core.commons;

import org.apache.kafka.clients.consumer.ConsumerConfig;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/24 14:16
 * @Version: 1.0
 * @Description: TODO
 */
public enum Environment {
    CLUSTER_HOST("cluster.host"),
    CLUSTER_PORT("cluster.port"),
    CLUSTER_RACK("cluster.rack"),
    APPLICATION_ID("application.id"),
    APPLICATION_INDEX("application.index"),
    APPLICATION_TOATL("application.total"),
    EXECUTOR_INDEX("exector.index"),
    EXECUTOR_TOATL("exector.total"),
    ZK_CONNECT("cluster.zookeeper.servers"),
    ZK_ROOT_DIR("cluster.zookeeper.root"),
    KAFKA_BOOTSTRAP("kafka.bootstrap");

    private final String variable;

    private Environment(String variable) {
        this.variable = variable;
    }

    public String key() {
        return this.variable;
    }

    @Override
    public String toString() {
        return this.variable;
    }

    public String $() {
        return "$" + this.variable;
    }

    public String $$() {
        return "-" + this.variable;
    }
}
