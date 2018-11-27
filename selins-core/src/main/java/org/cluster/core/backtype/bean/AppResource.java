package org.cluster.core.backtype.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/16 17:33
 * @Version: 1.0
 * @Description: TODO
 */
public class AppResource implements Serializable {
    public enum Fileds {
        ID("id"),
        NAME("name"),
        JVM_OPTS("jvmOpts"),
        CLASS("class"),
        NUM_WORKERS("numWorkers"),
        STATE("state"),
        CATEGORY("category");

        private String var;

        Fileds(String var) {
            this.var = var;
        }

        public String key() {
            return this.var;
        }
    }

    private JSONObject var;

    /**
     * 初始化构造方法
     */
    public AppResource(String id, String name, String jvmOpts, String appMain, int numWorkers, int state, String category) {
        this(new JSONObject());
        put(Fileds.ID, id);
        put(Fileds.NAME, name);
        put(Fileds.JVM_OPTS, jvmOpts);
        put(Fileds.CLASS, appMain);
        put(Fileds.NUM_WORKERS, numWorkers);
        put(Fileds.STATE, state);
        put(Fileds.CATEGORY, category);
    }

    public AppResource(JSONObject var) {
        this.var = var;
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public int getInteger(AppResource.Fileds fileds) {
        return var.getInteger(fileds.key());
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public String getString(AppResource.Fileds fileds) {
        return String.valueOf(var.getString(fileds.key()));
    }

    /**
     * 根据Environment枚举内容, 获取上下文环境中的内容.
     */
    public void put(AppResource.Fileds fileds, Object o) {
        var.put(fileds.key(), o);
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
    public static AppResource parse(String jsonString) {
        return new AppResource(JSONObject.parseObject(jsonString));
    }
}
