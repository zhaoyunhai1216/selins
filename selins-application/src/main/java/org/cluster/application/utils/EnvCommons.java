package org.cluster.application.utils;

import org.hyperic.sigar.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EnvCommons {
    /**
     * 设置环境信息，初始化环境库文件，然后加入到java.library.path中
     *
     * @throws IOException
     */
    public static void setEnvironment() throws IOException {
        String classpath = new File("etc/native").getCanonicalPath();
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
    }

    public static void print() {
        Properties props = System.getProperties();
        Runtime r = Runtime.getRuntime();
        System.out.println("JVM可以使用的总内存:    " + r.totalMemory() / 1024 / 1024);
        System.out.println("JVM可以使用的剩余内存:    " + r.freeMemory() / 1024 / 1024);
        System.out.println("JVM可以使用的处理器个数:    " + r.availableProcessors());
        System.out.println("Java的运行环境版本：    " + props.getProperty("java.version"));
        System.out.println("操作系统的名称：    " + props.getProperty("os.name"));
        System.out.println("操作系统的构架：    " + props.getProperty("os.arch"));
        System.out.println("操作系统的版本：    " + props.getProperty("os.version"));

    }

    
    /**
     * 获取当前系统整体本地硬盘容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getTotalFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getTotal();
        }
        return bytes / 1024L / 1024L + "G";
    }

    /**
     * 获取当前系统使用本地硬盘容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getUsedFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getUsed();
        }
        return bytes / 1024L / 1024L + "G";
    }

    /**
     * 获取当前系统剩余本地硬盘容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getFreeFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getFree();
        }
        return bytes / 1024L / 1024L + "G";
    }

    /**
     * 获取当前系统本地硬盘使用率，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getPercentFileSystemStat(Sigar sigar) throws SigarException {
        double percent = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            percent += usage.getUsePercent();
        }
        return percent * 100 + "%";
    }

    /**
     * 获取当前系统读硬盘速度，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getReadFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getDiskReadBytes();
        }
        return bytes / 1024L / 1024L+ "M/s";
    }

    /**
     * 获取当前系统写硬盘速度，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getWriteFileSystemStat(Sigar sigar) throws SigarException {
        long bytes = 0L;
        for (int i = 0; i < sigar.getFileSystemList().length; i++) {
            FileSystemUsage usage = sigar.getFileSystemUsage(sigar.getFileSystemList()[i].getDirName());
            bytes += usage.getDiskWriteBytes();
        }
        return bytes / 1024L / 1024L+ "M/s";
    }
    /**
     * 获取当前系统整体内存容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getTotalMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return sigar.getMem().getTotal() / 1024L / 1024L + "M";
    }

    /**
     * 获取当前系统使用内存容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getUsedMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return sigar.getMem().getUsed() / 1024L / 1024L + "M";
    }

    /**
     * 获取当前系统剩余内存容量，并格式化新的输出格式
     * @param sigar
     * @return
     * @throws Exception
     */
    public static String getFreeMemoryStat(Sigar sigar) throws SigarException {
        // 内存总量
        return sigar.getMem().getFree() / 1024L / 1024L + "M";
    }

    /**
     * 获取当前系统每个核心的使用率，然后计算出系统总体使用率
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
     * @param sigar
     * @return
     * @throws Exception
     */
    public static long getRxNetStat(Sigar sigar) throws SigarException {
        long rxBytes = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getRxBytes();
        }
        return rxBytes;
    }

    /**
     * 获取当前系统所发送的字节数，然后通过差值计算出口带宽使用率
     * @param sigar
     * @return
     * @throws Exception
     */
    public static long getNxNetStat(Sigar sigar) throws SigarException {
        long rxBytes = 0L;
        for (int i = 0; i < sigar.getNetInterfaceList().length; i++) {
            rxBytes += sigar.getNetInterfaceStat(sigar.getNetInterfaceList()[i]).getTxBytes();
        }
        return rxBytes;
    }

    public static void main(String[] args) throws Exception {
        setEnvironment();
        Sigar sigar = new Sigar();
        //System.out.printf(System.getProperty("java.library.path"));
        print();
        System.out.println(getTotalMemoryStat(sigar));
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
    }
}
