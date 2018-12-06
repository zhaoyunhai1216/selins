package org.cluster.core.zookeeper;

import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZkCuratorTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getZkCurator() {
        ZkCurator.getInstance().getZkCurator();
    }

    @Test
    public void init() {
    }

    @Test
    public void expire() {
    }

    @Test
    public void getInstance() {
        ZkCurator.getInstance();
    }
}