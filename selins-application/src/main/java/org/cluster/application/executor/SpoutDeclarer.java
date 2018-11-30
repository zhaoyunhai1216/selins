package org.cluster.application.executor;

import org.apache.commons.beanutils.BeanUtils;
import org.cluster.application.base.BaseBolt;
import org.cluster.application.base.BaseSpout;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.commons.OutputCollector;
import org.cluster.application.utils.UtilCommons;
import org.cluster.application.zookeeper.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 15:18
 * @Version: 1.0
 * @Description: TODO
 */
public class SpoutDeclarer {
    /**
     * 组件详细内容,组件定义
     */
    private BaseSpout component;

    /**
     * 构造方法,初始化一个应用组件
     */
    public SpoutDeclarer(BaseSpout component) {
        this.component = component;
    }

    /*
     * 根据模板克隆一个新的组件信息, 已被多线程多实例进行访问,每个线程一个实例
     */
    public BaseSpout getIComponent() throws Exception {
        BaseSpout component = this.component.getClass().newInstance();
        return component;
    }

    /**
     * 在当前节点启动需要运行的组件信息,并指定运行的并行度信息
     */
    public void start(EnvOptions options, int parallelism_hint) throws Exception {
        for (int i = 0; i < parallelism_hint; i++) {
            ApplicationContext context = ApplicationContext.init(options, component.getName(), i, parallelism_hint);
            ExectorsManager.getInstance().put(context.getExectorID(component.getName()),context);
            new Thread(new ISpoutExe(context, new OutputCollector(context), getIComponent())).start();
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BoltDeclarer.class);
}

