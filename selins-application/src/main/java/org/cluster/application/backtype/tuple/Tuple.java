package org.cluster.application.backtype.tuple;

import com.alibaba.fastjson.JSONObject;
import org.cluster.application.ApplicationBuilder;
import org.cluster.application.utils.UtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/16 15:01
 * @Version: 1.0
 * @Description: TODO
 */
public class Tuple {
    private Map<String,Object> fileds;
    /*
     * 构造方法
     */
    public Tuple() {
        this.fileds = new HashMap<String,Object>();
    }

    /**
     * 存储数据到当前实例中
     */
    public void put(String k, Object v){
        fileds.put(k,v);
    }

    /**
     * 获取当前实例中某个字段的值
     */
    public Object get(String k){
        return fileds.get(k);
    }

    /**
     * 获取当前实例中某个字段的值
     */
    public Set<String> keys(){
        return fileds.keySet();
    }

    @Override
    public String toString() {
        return fileds.toString();
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Tuple.class);
}
