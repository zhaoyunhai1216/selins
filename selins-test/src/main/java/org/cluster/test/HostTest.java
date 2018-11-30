package org.cluster.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/1 18:04
 * @Version: 1.0
 * @Description: TODO
 */
public class HostTest {
    public static void main(String[] args) throws UnknownHostException {
        System.out.println(InetAddress.getByName("VM_16_2_centos1").getHostAddress());
    }
}
