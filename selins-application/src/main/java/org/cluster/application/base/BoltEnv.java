package org.cluster.application.base;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/24 10:34
 * @Version: 1.0
 * @Description: TODO
 */
public class BoltEnv extends JSONObject {
    private ConcurrentSkipListSet<String> subList;

    public ArrayList<String> getSubscribe() {
        return new ArrayList(subList);
    }

    public void subscribe(Collection<String> subList) {
        this.subList.addAll(subList);
    }

    /**
     * 构造方法, 构建组件所需要的环境信息
     */
    public BoltEnv() {
        this.subList = new ConcurrentSkipListSet();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BoltEnv.class);
}
