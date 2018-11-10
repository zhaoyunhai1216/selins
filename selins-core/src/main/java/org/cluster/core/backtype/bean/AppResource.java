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
    private String id;
    private String name;
    private String jvmOpts;
    private String appMain;
    private int numWorkers;
    private int state;
    private String category;

    public AppResource(String id, String name, String jvmOpts, String appMain, int numWorkers, int state, String category) {
        this.id = id;
        this.name = name;
        this.jvmOpts = jvmOpts;
        this.appMain = appMain;
        this.numWorkers = numWorkers;
        this.state = state;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJvmOpts() {
        return jvmOpts;
    }

    public void setJvmOpts(String jvmOpts) {
        this.jvmOpts = jvmOpts;
    }

    public String getAppMain() {
        return appMain;
    }

    public void setAppMain(String appMain) {
        this.appMain = appMain;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "AppResource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", jvmOpts='" + jvmOpts + '\'' +
                ", appMain='" + appMain + '\'' +
                ", numWorkers=" + numWorkers +
                ", state=" + state +
                ", category='" + category + '\'' +
                '}';
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
