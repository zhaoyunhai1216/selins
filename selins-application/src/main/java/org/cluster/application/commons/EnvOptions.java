package org.cluster.application.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.cluster.application.utils.EnvCommons;
import org.cluster.application.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/30 14:53
 * @Version: 1.0
 * @Description: TODO
 */
public class EnvOptions extends JSONObject {
    /**
     * A constructor Creates a command line. Lists the OptionGroups that are members of this Options instance.
     *
     * @return a Collection of OptionGroup instances.
     */
    public EnvOptions(CommandLine cliParser) throws Exception {
        put(Environment.BROKER_HOST.key(), cliParser.getOptionValue("host", "127.0.0.1"));
        put(Environment.CLUSTER_WORKERUID.key(), UUID.randomUUID().toString());
        put(Environment.CLUSTER_CONF_DIR.key(), cliParser.getOptionValue("yaml"));
        put(Environment.APPLICATION_ID.key(), cliParser.getOptionValue("appID", UUID.randomUUID().toString()));
        put(Environment.APPLICATION_SEQ.key(), cliParser.getOptionValue("seq", "0"));
        put(Environment.APPLICATION_TOATL.key(), cliParser.getOptionValue("total", "1"));
        put(Environment.APPLICATION_MAIN.key(), cliParser.getOptionValue("appClass"));
        EnvCommons.setEnvironment(getOptionValue(Environment.CLUSTER_CONF_DIR.key()));

        for (String key : cliParser.getArgs()) {
            put(key, cliParser.getOptionValue(key));
        }
        this.putAll(UtilCommons.getYaml(getOptionValue(Environment.CLUSTER_CONF_DIR.key())).getInnerMap());
    }

    /*
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(Environment env) {
        return String.valueOf(this.getString(env.key()));
    }

    /**
     * 获取这个work的workID
     */
    public String getWorkerID() {
        return getString(Environment.APPLICATION_ID) + "_" + getString(Environment.APPLICATION_SEQ) + "_" + getString(Environment.APPLICATION_TOATL);
    }

    /**
     * 获取这个work的在zk上存储的路径信息
     */
    public String getZkWorkerDir(String uid) {
        return getString(Environment.ZK_ROOT_DIR) + "/worker/" + uid;
    }

    /**
     * Retrieve the argument, if any, of an option.
     *
     * @param opt          character name of the option
     * @param defaultValue is the default value to be returned if the option
     *                     is not specified
     * @return Value of the argument if option is set, and has an argument,
     * otherwise <code>defaultValue</code>.
     */
    public String getOptionValue(String opt, String defaultValue) {
        String value = getString(String.valueOf(opt));
        return value == null ? defaultValue : value;
    }


    /**
     * Retrieves the array of values, if any, of an option.
     *
     * @param opt character name of the option
     * @return Values of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String getOptionValue(String opt) {
        return getString(String.valueOf(opt));
    }

    /**
     * Retrieves the array of values, if any, of an option.
     *
     * @param env character name of the option
     * @return Values of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String getOptionValue(Environment env) {
        return String.valueOf(this.getString(env.key()));
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(EnvOptions.class);
}
