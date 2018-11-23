package org.cluster.core.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.cluster.core.Broker;
import org.cluster.core.scheduler.LocalOptions;
import org.cluster.core.utils.EnvCommons;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/26 10:43
 * @Version: 1.0
 * @Description: TODO
 */
public class Configuration extends JSONObject {
    /**
     * 一个Cache实例
     */
    private static Configuration clusterYaml;

    /**
     * 构造方法
     */
    public Configuration(String conf) throws Exception {
        this.put(Environment.START_TIMESTAMP, System.currentTimeMillis());
        EnvCommons.setEnvironment(this);
        this.putAll(new Yaml().loadAs(FileUtils.openInputStream(new File(conf)), JSONObject.class).getInnerMap());
    }

    /**
     * 获得单例
     */
    public static Configuration getInstance() {
        return clusterYaml;
    }

    /**
     * 初始化Yaml配置文件信息
     */
    public static void init(String conf) throws Exception {
        clusterYaml = new Configuration(conf);
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(Environment env) {
        return this.getInteger(env.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(Environment env) {
        return String.valueOf(this.getString(env.key()));
    }

    /**
     * 根据Environment枚举内容, 存放数据到conf
     */
    public Object put(Environment env, Object value) {
        return this.put(env.key(), value);
    }

    /**
     * 获取工程根目录
     */
    public static String getProjectDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Configuration.class);
}
