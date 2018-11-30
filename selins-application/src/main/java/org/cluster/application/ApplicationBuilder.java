package org.cluster.application;
;
import org.cluster.application.base.BaseSpout;
import org.cluster.application.executor.BoltDeclarer;
import org.cluster.application.base.BaseBolt;
import org.cluster.application.executor.SpoutDeclarer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/23 17:29
 * @Version: 1.0
 * @Description: TODO
 */
public class ApplicationBuilder {
    /**
     * 设置组件 spout
     */
    public static SpoutDeclarer setComponent(BaseSpout component,String name) {
        component.setName(name);
        return new SpoutDeclarer(component);
    }

    /**
     * 设置组件 bolt
     */
    public static BoltDeclarer setComponent(BaseBolt component,String name) {
        component.setName(name);
        return new BoltDeclarer(component);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ApplicationBuilder.class);
}
