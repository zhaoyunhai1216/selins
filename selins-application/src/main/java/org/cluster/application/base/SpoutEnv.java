package org.cluster.application.base;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 15:11
 * @Version: 1.0
 * @Description: TODO
 */
public class SpoutEnv extends JSONObject {
    /**
     * 构造方法, 构建组件所需要的环境信息
     */
    public SpoutEnv() {

    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BoltEnv.class);
}

