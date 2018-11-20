package org.cluster.core.commons;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {

    @Test
    public void init() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
    }

    @Test
    public void getInstance() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        Configuration conf = Configuration.getInstance();
        System.out.println(conf.toString());
    }

    @Test
    public void getConf() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        Configuration conf = Configuration.getInstance();
        System.out.println(conf.toJSONString());
    }

    @Test
    public void getString() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        Configuration conf = Configuration.getInstance();
        System.out.println(conf.getString("cluster.host"));
    }

    @Test
    public void getBaseDir() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        System.out.println(Configuration.getProjectDir());
    }
}