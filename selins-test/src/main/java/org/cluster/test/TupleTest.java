package org.cluster.test;

import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.utils.UtilCommons;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/26 11:52
 * @Version: 1.0
 * @Description: TODO
 */
public class TupleTest {
    public static void main(String[] args) throws Exception {
        Tuple tuple =  new Tuple();
        tuple.put("123","123");
        byte[] b = UtilCommons.serialize(tuple);
        System.out.println(b.length);
        System.out.println(UtilCommons.unserialize(b,new Tuple()));
    }
}
