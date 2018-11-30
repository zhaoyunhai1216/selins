package org.cluster.application.tracker;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/14 10:58
 * @Version: 1.0
 * @Description: TODO
 */
public class LoggerMemory {
    /**
     * 缓存各个类型的统计信息
     */
    public static Map<String, AtomicLong> targets = new ConcurrentHashMap<String, AtomicLong>();

    /**
     * 添加指定类型的统计信息
     */
    public static void set(String type, long arg) {
        targets.computeIfAbsent(type, x -> new AtomicLong()).getAndAdd(arg);
    }

    /**
     * 收集所有类型的统计数据
     */
    public static ConcurrentHashMap<String, Long> get() {
        ConcurrentHashMap<String, Long> r = new ConcurrentHashMap<String, Long>();
        targets.entrySet().stream().forEach(x -> {
            r.put(x.getKey(), x.getValue().getAndSet(0L));
        });
        return r;
    }

    /**
     * Log对象.
     */
    public static Logger logger = Logger.getLogger(LoggerMemory.class);
}
