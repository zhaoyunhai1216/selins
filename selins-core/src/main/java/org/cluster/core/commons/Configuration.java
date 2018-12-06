package org.cluster.core.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.cluster.core.utils.EnvCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;
/**
 * @Auther: 赵云海
 * @Date: 2018/9/26 10:43是
 * @Version: 1.0
 * @Description: TODO
 */
public enum Configuration {
    INSTANCE(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");

    private JSONObject var;

    /**
     * 构造方法
     */
    Configuration(String conf) {
        try {
            init(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void init(String conf) throws Exception {
        var = new JSONObject();
        var.put(Environment.START_TIMESTAMP.key(), System.currentTimeMillis());
        EnvCommons.setEnvironment(this);
        var.putAll(new Yaml().loadAs(FileUtils.openInputStream(new File(conf)), JSONObject.class).getInnerMap());
    }

    /**
     * 获得单例
     */
    public static Configuration getInstance() {
        return INSTANCE;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(Environment env) {
        return var.getInteger(env.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(Environment env) {
        return var.getString(env.key());
    }

    /**
     * 根据Environment枚举内容, 存放数据到conf
     */
    public Object put(Environment env, Object value) {
        return var.put(env.key(), value);
    }

    /**
     * 获取工程根目录
     */
    public static String getProjectDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 以map形式展示内容信息
     */
    public Map<String, Object> getInnerMap() {
        return var.getInnerMap();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Configuration.class);
}
