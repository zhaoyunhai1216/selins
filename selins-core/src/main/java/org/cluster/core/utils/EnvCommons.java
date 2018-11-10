package org.cluster.core.utils;


import org.hyperic.sigar.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EnvCommons {

    /**
     * 获取当前系统整体本地硬盘容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static int getTotalFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            if (sigar.getFileSystemList()[i].getType() != 2) continue;
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getTotal();
        }
        return (int)Math.round ((double)bytes / 1024d / 1024d);
    }

    /**
     * 获取当前系统使用本地硬盘容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static int getUsedFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            if (sigar.getFileSystemList()[i].getType() != 2) continue;
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getUsed();
        }
        return(int)Math.round ((double)bytes / 1024d / 1024d);
    }

    /**
     * 获取当前系统剩余本地硬盘容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static int getFreeFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            if (sigar.getFileSystemList()[i].getType() != 2) continue;
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getFree();
        }
        return (int)Math.round ((double)bytes / 1024d / 1024d);
    }

    /**
     * 获取当前系统本地硬盘使用率，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getPercentFileSystemStat(Sigar sigar) throws SigarException {
        return Math.round((double) getUsedFileSystemStat(sigar) / (double) getTotalFileSystemStat(sigar) * 100d) + "%";
    }

    /**
     * 获取当前系统读硬盘速度，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getReadFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            if (sigar.getFileSystemList()[i].getType() != 2) continue;
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getDiskReadBytes();
        }
        return bytes / 1024L / 1024L / 1024L + "M/s";
    }

    /**
     * 获取当前系统写硬盘速度，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getWriteFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            if (sigar.getFileSystemList()[i].getType() != 2) continue;
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getDiskWriteBytes();
        }
        return bytes / 1024L / 1024L / 1024L + "M/s";
    }

    /**
     * 获取当前系统整体内存容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getTotalMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return Math.round((double)sigar.getMem().getTotal() / 1024d / 1024d / 1024d) + "G";
    }

    /**
     * 获取当前系统使用内存容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getUsedMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return Math.round((double)sigar.getMem().getUsed() / 1024d / 1024d / 1024d) + "G";
    }

    /**
     * 获取当前系统剩余内存容量，并格式化新的输出格式
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getFreeMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return Math.round((double)sigar.getMem().getFree() / 1024d / 1024d / 1024d) + "G";
    }

    /**
     * 获取当前系统每个核心的使用率，然后计算出系统总体使用率
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getCpuStat(Sigar sigar) throws SigarException {
        double cpuStat = Arrays.asList(sigar.getCpuPercList()).stream()
                .mapToDouble(x -> x.getCombined()).average().getAsDouble();
        return CpuPerc.format(cpuStat);
    }

    /**
     * 获取当前系统所接收的字节数，然后通过差值计算入口带宽使用率
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static long getRxNetStat(Sigar sigar) throws SigarException, InterruptedException {
        long rxBytes0 = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes0 += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getRxBytes();
        }
        TimeUnit.SECONDS.sleep(1);
        long rxBytes1 = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes1 += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getRxBytes();
        }
        return (rxBytes1 - rxBytes0) / 1 / 1024L / 1024L;
    }

    /**
     * 获取当前系统所发送的字节数，然后通过差值计算出口带宽使用率
     *
     * @param sigar
     * @return
     * @throws Exception
     */
    public static long getNxNetStat(Sigar sigar) throws SigarException, InterruptedException {
        long rxBytes0 = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes0 += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getTxBytes();
        }
        TimeUnit.SECONDS.sleep(1);
        long rxBytes1 = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes1 += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getTxBytes();
        }
        return (rxBytes1 - rxBytes0) / 1 / 1024L / 1024L;
    }

    /**
     * 获取JDK版本
     *
     * @return
     * @throws SigarException
     */
    public static String getJDKVersion() throws SigarException {
        return System.getProperties().getProperty("java.version");
    }

    /**
     * 获取CPU核心数
     *
     * @return
     * @throws SigarException
     */
    public static int getProcessors() throws SigarException {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取操作系统版本
     *
     * @return
     * @throws SigarException
     */
    public static String getOS() throws SigarException {
        return System.getProperties().getProperty("os.name")
                + "_" + System.getProperties().getProperty("os.version")
                + "_" + System.getProperties().getProperty("os.arch");
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

    public static void main(String[] args) throws Exception {
        setEnvironment("E:\\工作空间\\ccinfra-real-cmpt-yn\\etc\\");
        Sigar sigar = new Sigar();
        //System.out.printf(System.getProperty("java.library.path"));
        /*System.out.println(getTotalMemoryStat(sigar));
        System.out.println(getUsedMemoryStat(sigar));
        System.out.println(getFreeMemoryStat(sigar));
        System.out.println(getCpuStat(sigar));
        System.out.println(getRxNetStat(sigar));
        System.out.println(getNxNetStat(sigar));
        System.out.println(getTotalFileSystemStat(sigar));
        System.out.println(getUsedFileSystemStat(sigar));
        System.out.println(getFreeFileSystemStat(sigar));
        System.out.println(getPercentFileSystemStat(sigar));
        System.out.println(getReadFileSystemStat(sigar));
        System.out.println(getWriteFileSystemStat(sigar));
        System.out.println(getJavaVersion());
        System.out.println(getOS());*/
        System.out.println(UtilCommons.getBrokerState());
        sigar.close();
    }

    // 日志 sfl4j 注册
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(EnvCommons.class);
}
