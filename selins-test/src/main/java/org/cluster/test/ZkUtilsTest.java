package org.cluster.test;

import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.utils.UtilCommons;
import org.cluster.application.zookeeper.ZkCurator;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/31 09:59
 * @Version: 1.0
 * @Description: TODO
 */
public class ZkUtilsTest {
    public static void main(String[] args) throws Exception {
        args = new String[]{"-yaml=E://工作空间/ccinfra-real-cmpt-yn/etc/"};
        EnvOptions opt = UtilCommons.getCliParser(args);
        ZkCurator.getInstance().init(opt.getOptionValue(Environment.ZK_CONNECT));
        ZkCurator.getInstance().getZkCurator().delete().forPath("/jcluster/components");
    }
}
