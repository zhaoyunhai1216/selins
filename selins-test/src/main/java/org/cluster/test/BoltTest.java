package org.cluster.test;

import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.base.BaseBolt;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.OutputCollector;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/25 13:35
 * @Version: 1.0
 * @Description: TODO
 */
public class BoltTest extends BaseBolt {
    @Override
    public void prepare(ApplicationContext context, OutputCollector collector) {
        collector.setProducerSize(context,1);
        System.out.println(1);
    }

    @Override
    public void nextTuple(OutputCollector collector, Tuple tuple) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(tuple.toString());
    }
}
