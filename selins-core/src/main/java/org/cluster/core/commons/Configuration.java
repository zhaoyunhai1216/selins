package org.cluster.core.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.cluster.core.scheduler.LocalOptions;
import org.cluster.core.utils.EnvCommons;
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
public class Configuration {
    /**
     * 一个Cache实例
     */
    private static Configuration clusterYaml;
    /**
     * 集群配置文件信息
     */
    private JSONObject conf;

    /**
     * 构造方法
     */
    public Configuration(String conf) throws Exception {
        EnvCommons.setEnvironment(this.getClass().getResource("/").getFile() + "/etc");
        this.conf = new JSONObject();
        try {
            this.conf = new Yaml().loadAs(FileUtils.openInputStream(new File(conf)), JSONObject.class);
            this.conf.put("yaml", conf);
            this.conf.put("brokerID", UUID.randomUUID().toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
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
     * 获取配置文件信息
     *
     * @return JSONObject
     */
    public JSONObject getConf() {
        return conf;
    }

    /**
     * 获取工程根目录
     */
    public static String getBaseDir() {
        return new Object().getClass().getResource("/").getFile();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Configuration.class);
}
