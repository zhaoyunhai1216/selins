package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/16 10:34
 * @Version: 1.0
 * @Description: TODO
 */
public class WorkerState {
    public enum Fileds {
        WORKER_ID("workerId"),
        HOST("host"),
        PROCESS("process"),
        CATEGORY("category"),
        START_TIMESTAMP("startTime"),
        RUN_TIME("runtime"),
        CPU_USAGE("cpu"),
        MEMORY_USAGE("memory"),
        EXECTORS("exectors"),
        THREAD_COUNT("threadCount");

        private String var;

        Fileds(String var) {
            this.var = var;
        }

        public String key() {
            return this.var;
        }
    }

    private JSONObject var;

    public WorkerState(JSONObject var) {
        this.var = var;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(WorkerState.Fileds fileds) {
        return var.getInteger(fileds.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(WorkerState.Fileds fileds) {
        return String.valueOf(var.getString(fileds.key()));
    }

    /**
     * 转换成json字符串
     */
    @Override
    public String toString() {
        return var.toJSONString();
    }

    /**
     * 由json解析成MetaNode 对象
     */
    public static WorkerState parse(String jsonString) {
        return new WorkerState(JSONObject.parseObject(jsonString));
    }

    /**
     * 获取appID
     */
    public String getAppID() {
        return getString(Fileds.WORKER_ID).split("\\_")[0];
    }

    /**
     * 获取当前app的worker的序列号
     */
    public int getSeq() {
        return Integer.parseInt(getString(Fileds.WORKER_ID).split("\\_")[1]);
    }

    /**
     * 获取当前app总共的worker数量
     */
    public int getTotal() {
        return Integer.parseInt(getString(Fileds.WORKER_ID).split("\\_")[2]);
    }
}
