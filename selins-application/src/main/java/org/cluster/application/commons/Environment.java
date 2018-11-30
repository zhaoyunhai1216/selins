package org.cluster.application.commons;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/24 14:16
 * @Version: 1.0
 * @Description: TODO
 */
public enum Environment {
    BROKER_HOST("broker.host"),
    CLUSTER_CONF_DIR("conf.dir"),
    APPLICATION_ID("application.id"),
    APPLICATION_SEQ("application.seq"),
    APPLICATION_TOATL("application.total"),
    APPLICATION_MAIN("application.main"),
    APPLICATION_CATEGORY("application.category"),
    ZK_CONNECT("cluster.zookeeper.servers"),
    ZK_ROOT_DIR("cluster.zookeeper.root"),
    COMPONENT_NAME("component.name"),
    EXECTORS_SEQ("exector.seq"),
    EXECTORS_TOTAL("exector.total");

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
