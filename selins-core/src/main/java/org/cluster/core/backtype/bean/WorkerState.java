package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/16 10:34
 * @Version: 1.0
 * @Description: TODO
 */
public class WorkerState {
    private String workerId;
    private String host;
    private String process;
    private String category;
    private long startTime;
    private long runtime;
    private String cpu;
    private String memory;
    private int exectors;
    private int threadCount;

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public int getExectors() {
        return exectors;
    }

    public void setExectors(int exectors) {
        this.exectors = exectors;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public String getAppID() {
        return this.getWorkerId().split("\\_")[0];
    }

    public int getSeq() {
        return Integer.parseInt(this.getWorkerId().split("\\_")[1]);
    }

    public int getTotal() {
        return Integer.parseInt(this.getWorkerId().split("\\_")[2]);
    }

    @Override
    public String toString() {
        return "WorkerState{" +
                "workerId='" + workerId + '\'' +
                ", host='" + host + '\'' +
                ", process='" + process + '\'' +
                ", category='" + category + '\'' +
                ", startTime=" + startTime +
                ", runtime=" + runtime +
                ", cpu='" + cpu + '\'' +
                ", memory='" + memory + '\'' +
                ", exectors=" + exectors +
                ", threadCount=" + threadCount +
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
    public static WorkerState parse(String workerJson) {
        return JSONObject.parseObject(workerJson, WorkerState.class);
    }
}
