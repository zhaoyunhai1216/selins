package org.cluster.application.executor;

import com.alibaba.fastjson.JSONObject;
import org.cluster.application.commons.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/30 17:25
 * @Version: 1.0
 * @Description: TODO
 */
public enum  ExectorsManager {
    INSTANCE;
    /**
     * exectors集合,包含当前worker的所有执行的exector
     */
    private Map<String, ApplicationContext> exectors;

    /**
     * 构造方法,初始化exectors集合
     */
    ExectorsManager() {
        this.exectors = new ConcurrentHashMap<String, ApplicationContext>();
    }

    // 取得缓存器实例
    public static ExectorsManager getInstance() {
        return INSTANCE;
    }

    /**
     * 获取存放的当前exectors的环境信息
     */
    public Map<String, ApplicationContext> getExectors() {
        return exectors;
    }

    /**
     * 存放当前exectors的环境信息
     */
    public void put(String key,ApplicationContext context){
        exectors.put(key,context);
    }


    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ExectorsManager.class);
}
