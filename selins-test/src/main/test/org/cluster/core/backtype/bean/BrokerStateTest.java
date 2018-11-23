package org.cluster.core.backtype.bean;

import org.cluster.core.commons.Environment;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BrokerStateTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parse() {
        String s = "{\"cluster.zookeeper.root\":\"/cluster\",\"cluster.host\":\"127.0.0.1\",\"cluster.appstore.dir\":\"D://appstore/\",\"system.info\":\"Windows 10_10.0_amd64\",\"java.version\":\"1.8.0_151\",\"cluster.zookeeper.servers\":\"62.234.114.155:2181\",\"start.timestamp\":1542770923300,\"used.memory\":7,\"cluster.port\":8080,\"cluster.rack\":\"rack0\",\"cluster.worker.dir\":\"D://workers/\",\"cluster.appstore.port\":2060,\"used.hdd\":97,\"total.vCores\":8,\"total.memory\":16,\"total.hdd\":1050,\"used.vCores\":\"25.4\",\"cluster.category\":\"default\",\"cluster.log.dir\":\"D://logs/\"}";
        BrokerState state = BrokerState.parse(s);
        System.out.println(Arrays.toString(Environment.values()));
    }

}