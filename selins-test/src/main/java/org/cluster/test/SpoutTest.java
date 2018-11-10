package org.cluster.test;

import org.cluster.application.base.BaseSpout;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.OutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 16:19
 * @Version: 1.0
 * @Description: TODO
 */
public class SpoutTest extends BaseSpout {
    @Override
    public void prepare(ApplicationContext context, OutputCollector collector) {
        collector.setProducerSize(context,1);
        collector.declarer("test","name","age");
        logger.info("SpoutTest open");
    }

    /**
     * 拉取数据操作, 通过无限次调用模拟数据流
     */
    @Override
    public void nextTuple(OutputCollector collector) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        collector.emit("test","Timi","23");
        System.out.println("spout nextTuple");
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(SpoutTest.class);
}
