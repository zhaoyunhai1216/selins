package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/16 11:50
 * @Version: 1.0
 * @Description: TODO
 */
public class BrokerState {
    private String baseDir;
    private String host;
    private int port;
    private String rack;
    private String category;
    private String jdk;
    private String startTime;
    private String processors;
    private String totalMemory;
    private String totalFileSystem;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJdk() {
        return jdk;
    }

    public void setJdk(String jdk) {
        this.jdk = jdk;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getProcessors() {
        return processors;
    }

    public void setProcessors(String processors) {
        this.processors = processors;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getTotalFileSystem() {
        return totalFileSystem;
    }

    public void setTotalFileSystem(String totalFileSystem) {
        this.totalFileSystem = totalFileSystem;
    }

    @Override
    public String toString() {
        return "BrokerState{" +
                "baseDir='" + baseDir + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", rack='" + rack + '\'' +
                ", category='" + category + '\'' +
                ", jdk='" + jdk + '\'' +
                ", startTime='" + startTime + '\'' +
                ", processors='" + processors + '\'' +
                ", totalMemory='" + totalMemory + '\'' +
                ", totalFileSystem='" + totalFileSystem + '\'' +
                '}';
    }

    /**
     * 转换成json字符串
     *
     * @return
     */
    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 由json解析成WorkerState 对象
     *
     * @param workerJson
     * @return
     */
    public static BrokerState parse(String workerJson) {
        return JSONObject.parseObject(workerJson, BrokerState.class);
    }
}
