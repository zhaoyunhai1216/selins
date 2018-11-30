package org.cluster.application.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 10:49
 * @Version: 1.0
 * @Description: kafka工具类
 */
public class KfkCommons {
    /**
     * 在集群中获取一个kafka 的 KafkaConsumer 实例
     */
    public static KafkaConsumer<byte[], byte[]> getConsumer(String conf, String groupID, List<String> topic) {
        Properties props = UtilCommons.getProperties(conf + "/resources/consumer.properties");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(topic);
        logger.info("Create a new consumer connection ->" + props.getProperty("bootstrap.servers"));
        return consumer;
    }


    /**
     * 在集群中获取一个kafka 的 KafkaProducer 实例
     */
    public static List<KafkaProducer<byte[], byte[]>> getProducer(String conf, int count) {
        Properties props = UtilCommons.getProperties(conf + "/resources/producer.properties");
        List<KafkaProducer<byte[], byte[]>> producers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            producers.add(new KafkaProducer<>(props));
            logger.info("Create a new producer connection ->" + props.getProperty("bootstrap.servers"));
        }
        return producers;
    }

    /*
     * @Date 16:14 2018/9/13
     * @Author 赵云海
     * @Param
     * @return
     * @Description 发送数据信息
     **/
    public static void send(KafkaProducer<byte[], byte[]> producer, String topic, byte[] value) {
        producer.send(new ProducerRecord<byte[], byte[]>(topic, value));
    }

    /*
     * @Date 16:14 2018/9/13
     * @Author 赵云海
     * @Param
     * @return
     * @Description 发送数据信息
     **/
    public static void send(KafkaProducer<byte[], byte[]> producer, String topic, byte[] key, byte[] value) {
        producer.send(new ProducerRecord<byte[], byte[]>(topic, key, value));
    }

    // 日志 sfl4j 注册
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(KfkCommons.class);
}
