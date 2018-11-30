package org.cluster.application.base;

import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.Environment;
import org.cluster.application.commons.OutputCollector;
import org.cluster.application.tracker.EnumLogger;
import org.cluster.application.tracker.LoggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 15:11
 * @Version: 1.0
 * @Description: TODO
 */
public abstract class BaseSpout implements IComponent {
    private String name;
    private SpoutEnv env;

    public SpoutEnv getEnvDeclarer() {
        return env;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 构造方法,初始化组件并构建初始化环境
     */
    public BaseSpout() {
        this.env = new SpoutEnv();
    }

    /**
     * 运行时初始化方法, 初始化运行的必要条件
     *
     * @param context
     */
    public void open(ApplicationContext context, OutputCollector collector) {
        prepare(context, collector);
    }


    /**
     * 拉取数据操作, 通过无限次调用模拟数据流
     *
     * @param collector
     */
    public void nextTuple(ApplicationContext context, OutputCollector collector) {
        String exectorID = context.getExectorID(context.getString(Environment.COMPONENT_NAME));
        try {
            this.nextTuple(collector);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            LoggerManager.setLOGer(exectorID + "_" + EnumLogger.FAILED.key(), 1L);
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BaseBolt.class);
}

