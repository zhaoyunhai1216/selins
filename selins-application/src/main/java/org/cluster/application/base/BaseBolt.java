package org.cluster.application.base;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.Environment;
import org.cluster.application.commons.OutputCollector;
import org.cluster.application.tracker.EnumLogger;
import org.cluster.application.tracker.LoggerManager;
import org.cluster.application.utils.KfkCommons;
import org.cluster.application.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 11:33
 * @Version: 1.0
 * @Description: TODO
 */
public abstract class BaseBolt implements IComponent {
    private String name;
    private KafkaConsumer<byte[], byte[]> consumer;
    private BoltEnv env;

    public BoltEnv getEnvDeclarer() {
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
    public BaseBolt() {
        this.env = new BoltEnv();
    }

    /**
     * 运行时初始化方法, 初始化运行的必要条件
     *
     * @param context
     */
    public void open(ApplicationContext context, OutputCollector collector) {
        prepare(context, collector);
        String application_id = context.getString(Environment.APPLICATION_ID);
        this.consumer = KfkCommons.getConsumer(context.getString(Environment.CLUSTER_CONF_DIR), application_id, env.getSubscribe());
    }

    /**
     * 拉取数据操作, 通过无限次调用模拟数据流
     *
     * @param collector
     */
    public void nextTuple(ApplicationContext context, OutputCollector collector) {
        ConsumerRecords<byte[], byte[]> records = consumer.poll(10);
        for (ConsumerRecord<byte[], byte[]> record : records) {
            nextTuple(context, collector, record.value());
        }
    }

    /**
     * 拉取数据操作, 通过无限次调用模拟数据流
     *
     * @param collector
     * @param b
     */
    private void nextTuple(ApplicationContext context, OutputCollector collector, byte[] b) {
        String exectorID = context.getExectorID(context.getString(Environment.COMPONENT_NAME));
        try {
            Tuple tuple = UtilCommons.unserialize(b, new Tuple());
            LoggerManager.setLOGer( exectorID + "_" + EnumLogger.EXECUTED.key(), 1L);
            nextTuple(collector, tuple);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            LoggerManager.setLOGer( exectorID + "_" + EnumLogger.FAILED.key(), 1L);
        }
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(BaseBolt.class);
}
