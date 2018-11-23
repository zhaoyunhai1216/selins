package org.cluster.application.backtype.tuple;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/21 11:21
 * @Version: 1.0
 * @Description: TODO
 */
public class BrokerState {
    public enum Fileds {
        HOST("cluster.host"), PORT("cluster.port");

        private String var;

        Fileds(String var) {
            this.var = var;
        }

        public String key() {
            return this.var;
        }
    }

    private JSONObject var;

    public BrokerState(JSONObject var) {
        this.var = var;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(BrokerState.Fileds fileds) {
        return var.getInteger(fileds.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(BrokerState.Fileds fileds) {
        return String.valueOf(var.getString(fileds.key()));
    }

    /**
     * 转换成json字符串
     */
    @Override
    public String toString() {
        return var.toJSONString();
    }

    /**
     * 由json解析成MetaNode 对象
     *
     * @param jsonString
     * @return
     */
    public static BrokerState parse(String jsonString) {
        return new BrokerState(JSONObject.parseObject(jsonString));
    }
}
