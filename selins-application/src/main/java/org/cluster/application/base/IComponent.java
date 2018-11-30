package org.cluster.application.base;


import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.OutputCollector;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/23 17:38
 * @Version: 1.0
 * @Description: TODO
 */
public interface IComponent {
    void prepare(ApplicationContext context, OutputCollector collector);
    default void nextTuple(OutputCollector collector){}
    default void nextTuple(OutputCollector collector, Tuple tuple){}
}
