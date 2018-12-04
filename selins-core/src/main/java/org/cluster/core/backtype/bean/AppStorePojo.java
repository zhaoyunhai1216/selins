package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;
import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/21 11:21
 * @Version: 1.0
 * @Description: TODO
 */
public class AppStorePojo {
    public enum Fileds {
        HOST("appstore.host"), PORT("appstore.port"), START_TIMESTAMP("start.timestamp");

        private String var;

        Fileds(String var) {
            this.var = var;
        }

        public String key() {
            return this.var;
        }
    }

    private JSONObject var;

    public AppStorePojo() {
        this.put(Fileds.HOST, Configuration.getInstance().getString(Environment.CLUSTER_HOST));
        this.put(Fileds.PORT, Configuration.getInstance().getInteger(Environment.APPSTORE_PORT));
        this.put(Fileds.START_TIMESTAMP, System.currentTimeMillis());
    }

    public AppStorePojo(JSONObject var) {
        this.var = var;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(AppStorePojo.Fileds fileds) {
        return var.getInteger(fileds.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(AppStorePojo.Fileds fileds) {
        return String.valueOf(var.getString(fileds.key()));
    }

    /**
     * 根据Environment枚举内容, 存放数据到conf
     */
    public Object put(AppStorePojo.Fileds fileds, Object value) {
        return var.put(fileds.key(), value);
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
     */
    public static AppStorePojo parse(String jsonString) {
        return new AppStorePojo(JSONObject.parseObject(jsonString));
    }
}
