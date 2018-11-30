package org.cluster.core.commons;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/8 16:23
 * @Version: 1.0
 * @Description: TODO
 */
public enum StateMemory {
    INSTANCE; //枚举实现单例模式, 此为当前对象的单例

    /**
     * 缓存各个类型的统计信息
     */
    public Map<String, Map<String, Map<String, String>>> targets;

    StateMemory() {
        targets = new ConcurrentHashMap<String, Map<String, Map<String, String>>>();
    }

    /**
     * 获得单例
     */
    public static StateMemory getInstance() {
        return INSTANCE;
    }

    /**
     * 存储
     */
    public void offer(String appID, String componentID, String exectorID, String state) {
        targets.computeIfAbsent(appID, x -> new ConcurrentHashMap<>()).computeIfAbsent(componentID, x -> new ConcurrentHashMap<>()).put(exectorID, state);
    }

    /**
     * 删除
     */
    public void remove(String appID) {
        targets.remove(appID);
    }

    /**
     * 获取
     */
    public String poll(){
        return JSONObject.toJSON(targets).toString();
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(StateMemory.class);
}
