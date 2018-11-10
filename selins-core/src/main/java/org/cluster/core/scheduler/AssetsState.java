package org.cluster.core.scheduler;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/2 15:03
 * @Version: 1.0
 * @Description: TODO
 */
public class AssetsState implements Comparable<AssetsState> {
    private String brokerID;
    private String host;
    private int port;
    private String category;
    private int workerSize;
    private int cpu;
    private int memory;

    public AssetsState(String brokerID, String host, int port, String category) {
        this.brokerID = brokerID;
        this.host = host;
        this.port = port;
        this.category = category;
    }

    public String getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(String brokerID) {
        this.brokerID = brokerID;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWorkerSize() {
        return workerSize;
    }

    public AssetsState setWorkerSize(int workerSize) {
        this.workerSize = workerSize;
        return this;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }


    @Override
    public int compareTo(AssetsState o) {
        // 比较运行的worker数量
        if (workerSize > o.getWorkerSize()) {
            return 1;
        } else if (workerSize < o.getWorkerSize()) {
            return -1;
        }
        // 比较cpu使用率
        if (cpu > o.getCpu()) {
            return 1;
        } else if (cpu < o.getCpu()) {
            return -1;
        }
        // 比较内存使用率
        if (memory > o.getMemory()) {
            return 1;
        } else if (memory < o.getMemory()) {
            return -1;
        }
        return 0;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
