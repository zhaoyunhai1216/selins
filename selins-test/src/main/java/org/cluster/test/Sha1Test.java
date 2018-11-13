package org.cluster.test;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/13 11:17
 * @Version: 1.0
 * @Description: TODO
 */
public class Sha1Test {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("0ef0e602-9d4c-4529-9da7-797695f144da");
        String sign0 = DigestUtils.md5Hex("ApplicationTest"+ System.currentTimeMillis());
        System.out.println(sign0);
        TimeUnit.MILLISECONDS.sleep(1);
        sign0 = DigestUtils.md5Hex("ApplicationTest"+ System.currentTimeMillis());
        System.out.println(sign0);
        TimeUnit.MILLISECONDS.sleep(1);
        sign0 = DigestUtils.md5Hex("ApplicationTest"+ System.currentTimeMillis());
        System.out.println(sign0);
            TimeUnit.MILLISECONDS.sleep(1);
        String sign1 = DigestUtils.md5Hex("0ef0e602-9d4c-4529-9da7-797695f144da_0_2");
        System.out.println(sign1);
        sign1 = DigestUtils.md5Hex("0ef0e602-9d4c-4529-9da7-797695f144da_2_2");
        System.out.println(UUID.randomUUID().toString());
    }
}
