package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/21 11:21
 * @Version: 1.0
 * @Description: TODO
 */
public class AppStore {
    public enum Fileds {
        HOST("host"), PORT("port"), START_TIMESTAMP("start.timestamp");

        private String var;

        Fileds(String var) {
            this.var = var;
        }

        public String key() {
            return this.var;
        }
    }

    private JSONObject var;

    public AppStore(JSONObject var) {
        this.var = var;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(AppStore.Fileds fileds) {
        return var.getInteger(fileds.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(AppStore.Fileds fileds) {
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
     */
    public static AppStore parse(String jsonString) {
        return new AppStore(JSONObject.parseObject(jsonString));
    }
}
