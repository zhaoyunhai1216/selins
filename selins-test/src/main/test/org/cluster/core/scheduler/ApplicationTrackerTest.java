package org.cluster.core.scheduler;

import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.zookeeper.ZkCurator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationTrackerTest {

    @Test
    public void tracker() throws Exception {
        DefaultScheduler.checkLegacyWorker();
        DefaultScheduler.checkShutdownWorker();
        DefaultScheduler.checkStartWorker();
    }
}