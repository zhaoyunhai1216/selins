package org.cluster.core.commons;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {

    @Test
    public void init() throws Exception {

    }

    @Test
    public void getInstance() throws Exception {
        Configuration conf = Configuration.getInstance();
        System.out.println(conf.toString());
    }

    @Test
    public void getConf() throws Exception {
       Configuration conf = Configuration.getInstance();
    }

    @Test
    public void getString() throws Exception {
        Configuration conf = Configuration.getInstance();
        System.out.println(conf.getString(Environment.CLUSTER_HOST));
    }

    @Test
    public void getBaseDir() throws Exception {
        System.out.println(Configuration.getProjectDir());
    }
}