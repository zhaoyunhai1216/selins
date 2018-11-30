package org.cluster.test;

import org.cluster.application.ApplicationBuilder;
import org.cluster.application.base.Application;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.EnvOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/29 13:45
 * @Version: 1.0
 * @Description: TODO
 */
public class ApplicationTest extends Application {
    /**
     * 程序application定义方法, 用于定义应用的处理结构
     */
    @Override
    public void define(EnvOptions config) throws Exception {
        ApplicationBuilder.setComponent(new SpoutTest(), "spoutName0").start(config, 1);
        ApplicationBuilder.setComponent(new SpoutTest(), "spoutName1").start(config, 1);
    }

    /**
     * 程序入口方法, 用于启动application 应用
     */
    public static void main(String[] args) throws Exception {
        toSubmit(new ApplicationTest(), "local", "--yaml=E:\\工作空间\\selins\\etc\\");
        System.out.println(123);
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(ApplicationTest.class);
}
