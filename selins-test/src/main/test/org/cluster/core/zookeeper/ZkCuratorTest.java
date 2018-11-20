package org.cluster.core.zookeeper;

import org.cluster.core.commons.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZkCuratorTest {

    @Before
    public void setUp() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
    }

    @Test
    public void getZkCurator() {
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));
        ZkCurator.getInstance().getZkCurator();
    }

    @Test
    public void init() {
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));
    }

    @Test
    public void expire() {
    }

    @Test
    public void getInstance() {
        ZkCurator.getInstance();
    }
}