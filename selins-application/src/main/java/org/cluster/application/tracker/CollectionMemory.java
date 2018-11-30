package org.cluster.application.tracker;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/14 14:06
 * @Version: 1.0
 * @Description: TODO
 */
public class CollectionMemory {
    /**
     * 缓存各个类型的统计信息
     */
    public static Map<String, Collection> targets = new ConcurrentHashMap<String, Collection>();

    /**
     * 添加指定类型的统计信息
     */
    public static void set(String type, Collection collection) {
        targets.putIfAbsent(type, collection);
    }

    /**
     * 收集所有类型的统计数据
     */
    public static ConcurrentHashMap<String, Integer> get() {
        ConcurrentHashMap<String, Integer> r = new ConcurrentHashMap<String, Integer>();
        targets.entrySet().stream().forEach(x -> {
            r.put(x.getKey(), x.getValue().size());
        });
        return r;
    }

    /**
     * Log对象.
     */
    public static Logger logger = Logger.getLogger(LoggerMemory.class);
}
