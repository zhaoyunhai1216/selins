package org.cluster.application.executor;

import org.cluster.application.base.BaseBolt;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.OutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 14:05
 * @Version: 1.0
 * @Description: TODO
 */
public class IBoltExe implements Runnable {
    private ApplicationContext context;
    private OutputCollector collector;
    private BaseBolt component;

    /**
     * SpoutExector 构造方法
     *
     * @param collector
     * @param component
     */
    public IBoltExe(ApplicationContext context, OutputCollector collector, BaseBolt component) {
        component.open(context, collector);
        this.context = context;
        this.collector = collector;
        this.component = component;
    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        while (true) {
            try {
                component.nextTuple(context, collector);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BaseBolt.class);
}
