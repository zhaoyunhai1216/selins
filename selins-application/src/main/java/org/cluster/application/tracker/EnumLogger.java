package org.cluster.application.tracker;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/29 16:18
 * @Version: 1.0
 * @Description: TODO
 */
public enum EnumLogger {
    EXECUTED("executed"),
    EMITTED("emitted"),
    FAILED("failed");

    private final String variable;

    private EnumLogger(String variable) {
        this.variable = variable;
    }

    public String key() {
        return this.variable;
    }

    @Override
    public String toString() {
        return this.variable;
    }
}
