package org.cluster.core.utils;

import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilCommonsTest {

    @Test
    public void getWorkerParameters() throws Exception {
        UtilCommons.getWorkerParameters("ApplicationTest-1", 1, 3);
    }
}