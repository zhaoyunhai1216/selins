package org.cluster.application.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.cluster.application.utils.EnvCommons;
import org.cluster.application.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/21 14:39
 * @Version: 1.0
 * @Description: TODO
 */
public class ApplicationContext extends JSONObject {

    /**
     * 初始化ApplicationContext并返回给调用方
     */
    public static ApplicationContext init(EnvOptions options, String componentName, int seq, int total) throws Exception {
        ApplicationContext context = new ApplicationContext();
        context.fluentPutAll(options.getInnerMap()).put(Environment.COMPONENT_NAME.key(), componentName);
        context.fluentPut(Environment.EXECTORS_SEQ.key(), seq).fluentPut(Environment.EXECTORS_TOTAL.key(), total);
        return context;
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
     * 获取这个work的workID
     */
    public String getExectorID(String componentName) {
        return componentName + "_" + getString(Environment.EXECTORS_SEQ) + "_" + getString(Environment.EXECTORS_TOTAL);
    }

    /**
     * 获取这个work的在zk上存储的路径信息
     */
    public String getZkWorkerDir() {
        return getString(Environment.ZK_ROOT_DIR) + "/worker/" + getWorkerID();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

}
