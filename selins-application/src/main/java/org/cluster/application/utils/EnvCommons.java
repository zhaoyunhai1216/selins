package org.cluster.application.utils;


import org.hyperic.sigar.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class EnvCommons {

    /**
     * 获取单个进程的CPU使用情况, 传入进程号,返回所使用的cpu占用率
     *
     * @return
     * @throws SigarException
     */
    public static String getCpuStat() throws SigarException {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        return CpuPerc.format(new Sigar().getProcCpu(pid).getPercent());
    }

    /**
     * 获取单个进程的CPU使用情况, 传入进程号,返回所使用的cpu占用率
     *
     * @return
     * @throws SigarException
     */
    public static String getMemStat() throws SigarException {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024L / 1024L / 1024L + "G";
    }

    /**
     * 设置环境信息，初始化环境库文件，然后加入到java.library.path中
     *
     * @throws IOException
     */
    public static void setEnvironment(String conf) throws Exception {
        String classpath = new File(conf + "/native").getCanonicalPath();
        String path = System.getProperty("java.library.path");
        if (path.contains(classpath)) {
            return;
        }
        if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0) {
            path += ":" + classpath;
        } else {
            path += ";" + classpath;
        }
        System.setProperty("java.library.path", path);
        logger.info("[ENV] Application init library successful.");
    }

    // 日志 sfl4j 注册
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(KfkCommons.class);
}
