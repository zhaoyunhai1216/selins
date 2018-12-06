package org.cluster.core.commons;

import org.apache.kafka.clients.consumer.ConsumerConfig;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/24 14:16
 * @Version: 1.0
 * @Description: TODO
 */
public enum Environment {
    START_TIMESTAMP("start.timestamp"),
    JAVA_VERSION("java.version"),
    SYSTEM_INFO("system.info"),
    CLUSTER_HOST("cluster.host"),
    CLUSTER_PORT("cluster.port"),
    CLUSTER_RACK("cluster.rack"),
    DEPLOY_CATEGORY("deploy.category"),
    YAML_DIR("yaml"),
    VCORES_TOTAL("total.vCores"),
    MEMOTY_TOTAL("total.memory"),
    HDD_TOTAL("total.hdd"),
    VCORES_USED("used.vCores"),
    MEMOTY_USED("used.memory"),
    HDD_USED("used.hdd"),
    ZK_CONNECT("cluster.zookeeper.servers"),
    ZK_ROOT_DIR("cluster.zookeeper.root"),
    KAFKA_BOOTSTRAP("kafka.bootstrap"),
    WORKER_DIR("cluster.worker.dir"),
    LOG_DIR("cluster.log.dir"),
    APPSTORE_PORT("cluster.appstore.port"),
    APPSTORE_DIR("cluster.appstore.dir");

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
