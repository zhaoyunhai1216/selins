package org.cluster.application.commons;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.tracker.EnumLogger;
import org.cluster.application.tracker.LoggerManager;
import org.cluster.application.utils.KfkCommons;
import org.cluster.application.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: 赵云海
 * @Date: 2018/9/21 14:39
 * @Version: 1.0
 * @Description: TODO
 */
public class OutputCollector {
    private ApplicationContext context;
    /**
     * 当前订阅每个输入流所在的地址映射，根据流streamId 分发到订阅者各台机器之上
     * key : streamId value : 各个订阅application的地址
     */
    private Map<String, String[]> fileds;

    /**
     * kafka的生产者线程池,由构造方法初始化,KafkaProducer<byte[],byte[]>
     */
    private List<KafkaProducer<byte[], byte[]>> producers;

    /**
     * 构造方法，初始化OutputCollector的各个信息使用
     */
    public OutputCollector(ApplicationContext context) {
        this.context = context;
        //默认kakfaProducer数量1
        this.producers = KfkCommons.getProducer(context.getString(Environment.CLUSTER_CONF_DIR), 1);
        this. fileds = new ConcurrentHashMap<>();
    }

    /**
     * 设置本应用所使用的kakfaProducer数量
     */
    public void setProducerSize(ApplicationContext context, int c) {
        this.producers.addAll(KfkCommons.getProducer(context.getString(Environment.CLUSTER_CONF_DIR), c - 1));
    }

    /**
     * 定义输出流的格式信息
     */
    public void declarer(String streamId, String... fields) {
        fileds.put(streamId, fields);
    }

    /**
     * 提交本次处理后的结果信息,然后分发到订阅者,返回处理成功失败
     */
    public void emit(String streamId, Object... fieldValues) {
        String exectorID = context.getExectorID(context.getString(Environment.COMPONENT_NAME));
        LoggerManager.setLOGer(exectorID + "_" + EnumLogger.EMITTED.key(), 1L);
        Tuple tuple = new Tuple();
        String[] _fileds = fileds.getOrDefault(streamId, new String[0]);
        for (int i = 0; i < Math.min(_fileds.length, fieldValues.length); i++) {
            tuple.put(_fileds[0], fieldValues[i]);
        }
        KfkCommons.send(producers.get(new Random().nextInt(producers.size())), streamId, UtilCommons.serialize(tuple));
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(OutputCollector.class);
}
