package org.cluster.core.zookeeper;

import org.cluster.core.commons.Configuration;
import org.cluster.core.commons.Environment;
import org.cluster.core.utils.UtilCommons;
import org.hyperic.sigar.SigarException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZkUtilsTest {

    @Before
    public void setUp() throws Exception {
        Configuration.init(Configuration.getProjectDir() + "/etc/conf/cluster.yaml");
        ZkCurator.getInstance().init(Configuration.getInstance().getString("cluster.zookeeper.servers"));
    }

    @Test
    public void create() {
    }

    @Test
    public void update() {
    }

    @Test
    public void build() {
    }

    @Test
    public void initBrokerState() throws Exception {
        String address = Configuration.getInstance().getString(Environment.CLUSTER_HOST) + ":" + Configuration.getInstance().getString(Environment.CLUSTER_PORT);
        String zkDir = Configuration.getInstance().getString(Environment.ZK_ROOT_DIR) + "/ids/" + address;
        //ZkUtils.build(zkDir, UtilCommons.getBrokerState());
        System.out.println(UtilCommons.getBrokerState());
        ZkCurator.getInstance().getZkCurator().delete().forPath("/cluster/appstore");
    }

    @Test
    public void getMaster() {
    }

    @Test
    public void isMaster() {
    }

    @Test
    public void getMapping() {
    }

    @Test
    public void getMapping1() {
    }

    @Test
    public void getNodes() {
    }

    @Test
    public void getNodeMaps() {
    }

    @Test
    public void getNodeState() {
    }

    @Test
    public void getWorker() {
    }

    @Test
    public void getWorkerID() {
    }

    @Test
    public void getAverageWorkers() {
    }

    @Test
    public void getWorkers() {
    }

    @Test
    public void getWorkerByBroker() {
    }

    @Test
    public void checkWorkerExists() {
    }

    @Test
    public void getAppStore() {
    }

    @Test
    public void getApplications() {
    }

    @Test
    public void getRunningApplicationsID() {
    }

    @Test
    public void getRunningApplications() {
    }

    @Test
    public void getRunningApplications1() {
    }

    @Test
    public void getAppZkResource() {
    }

    @Test
    public void getAppMetaJson() {
    }
}