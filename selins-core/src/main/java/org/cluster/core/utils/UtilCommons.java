package org.cluster.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.CreateMode;
import org.cluster.core.backtype.bean.AppResource;
import org.cluster.core.commons.Configuration;
import org.cluster.core.zookeeper.ZkCurator;
import org.cluster.core.zookeeper.ZkUtils;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Auther: 赵云海
 * @Date: 2018/10/16 15:06
 * @Version: 1.0
 * @Description: TODO
 */
public class UtilCommons {
    /*
     * 压缩数据 (Snappy)
     */
    public static byte[] compress(byte[] arg0) throws IOException {
        return Snappy.compress(arg0);
    }

    /*
     * 解压缩数据 (Snappy)
     */
    public static byte[] uncompress(byte[] arg0) throws IOException {
        return Snappy.uncompress(arg0);
    }

    /*
     * 压缩数据 (Kryo)
     */
    public static <T> byte[] serialize(T arg0) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) arg0.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(4096);
        return ProtostuffIOUtil.toByteArray(arg0, schema, buffer);
    }

    /*
     * 解压缩数据 (Kryo)
     */
    public static <T> T unserialize(byte[] arg0, T arg1) throws Exception {
        @SuppressWarnings("unchecked")
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) Object.class.getClass());
        ProtostuffIOUtil.mergeFrom(arg0, arg1, schema);
        return arg1;
    }


    /**
     * 压缩文件夹里的文件
     * 起初不知道是文件还是文件夹--- 统一调用该方法
     */
    private static void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            UtilCommons.zipDirectory(file, out, basedir);
        } else {
            UtilCommons.zipFile(file, out, basedir);
        }
    }

    /**
     * 压缩单个文件
     */
    private static void zipFile(File srcfile, ZipOutputStream out, String basedir) {
        if (!srcfile.exists()) {
            return;
        }
        byte[] buf = new byte[1024];
        try (FileInputStream in = new FileInputStream(srcfile)) {
            int len;
            out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 压缩文件夹
     */
    private static void zipDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }


    /**
     * 压缩文件
     *
     * @param srcfile
     */
    public static byte[] zipDirectory(File srcfile, String rootDir) throws IOException {
        try (ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
             ZipOutputStream out = new ZipOutputStream(outBytes)) {
            if (srcfile.isFile()) {
                zipFile(srcfile, out, rootDir);
            } else {
                File[] list = srcfile.listFiles();
                for (int i = 0; i < list.length; i++) {
                    compress(list[i], out, rootDir);
                }
            }
            out.finish(); // 结束压缩流的写入.
            return outBytes.toByteArray();
        }
    }

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     */
    @SuppressWarnings("rawtypes")
    public static void unZipDirectory(String rootDir, byte[] zipBytes) throws IOException {
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry = null;
            while ((zipEntry = in.getNextEntry()) != null) {
                try (OutputStream out = FileUtils.openOutputStream(new File(rootDir + zipEntry.getName()))) {
                    IOUtils.copy(in, out);
                }
            }
        }
    }

    /**
     * 获取当前节点状态,并返回
     *
     * @return
     * @throws SigarException
     */
    public static String getBrokerState() throws SigarException {
        return new JSONObject()
                .fluentPut("baseDir", Configuration.getInstance().getString("cluster.zookeeper.root"))
                .fluentPut("host", Configuration.getInstance().getString("cluster.host"))
                .fluentPut("port", Configuration.getInstance().getInteger("cluster.port"))
                .fluentPut("category", Configuration.getInstance().getString("cluster.category"))
                .fluentPut("rack", Configuration.getInstance().getString("cluster.rack"))
                .fluentPut("startTime", ManagementFactory.getRuntimeMXBean().getStartTime())
                .fluentPut("jdk", EnvCommons.getJDKVersion())
                .fluentPut("usedVCores", EnvCommons.getCpuStat())
                .fluentPut("usedMemory", EnvCommons.getUsedMemoryStat())
                .fluentPut("usedHDD", EnvCommons.getUsedFileSystemStat())
                .fluentPut("totalVCores", EnvCommons.getProcessors())
                .fluentPut("totalMemory", EnvCommons.getTotalMemoryStat())
                .fluentPut("totalHDD", EnvCommons.getTotalFileSystemStat()).toJSONString();
    }

    /**
     * 读取配置文件
     */
    public static Properties getProperties(String args) {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(args));
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void startCommand(String workId, String[] args) throws Exception {
        // Set the necessary command to execute the application master
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);
        vargs.add("cd " + Configuration.getProjectDir() + "/bin;");
        vargs.add("sh ./worker-start.sh");
        vargs.addAll(Arrays.asList(args));
        // Get final commmand
        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }
        logger.info(command.toString());
        Thread thread = new Thread(() -> {
            execCommand(command.toString());
        });
        thread.setDaemon(true);
        thread.start();
        while (thread.isAlive() && !ZkUtils.checkWorkerExists(ZkCurator.getInstance().getZkCurator(), workId)) {
            TimeUnit.SECONDS.sleep(1);
            logger.info("[Env] Worker <" + workId + "> is starting, please wait.");
        }
        logger.info("[Env] Worker <" + workId + "> is start finish.");
    }

    public static void killCommand(String processID) throws Exception {
        // Set the necessary command to execute the application master
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);
        vargs.add("kill -9 " + processID + "");
        // Get final commmand
        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }
        logger.info(command.toString());
        UtilCommons.execCommand(command.toString());
    }

    public static void execCommand(String command) {
        Process p = null;
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                p = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
            } else {
                p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            }
            p.waitFor();
            logger.info(IOUtils.toString(p.getInputStream(), "GBK"));
            logger.info(IOUtils.toString(p.getErrorStream(), "GBK"));
            logger.info("[ENV] Command <" + command + "> is destroy.");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    public static String[] getWorkerParameters(String appID, int seq, int total) throws Exception {
        String workDir = Configuration.getInstance().getString("cluster.worker.dir");
        AppResource res = ZkUtils.getAppZkResource(appID);
        String host = Configuration.getInstance().getString("cluster.host");
        return new String[]{workDir, appID + "_" + seq + "_" + total, res.getJvmOpts(), "--host=" + host, "--appID=" + res.getId()
                , "--appMain=" + res.getAppMain(), "--seq=" + seq, "--total=" + total, "--category=" + res.getCategory(), "--yaml=" + Configuration.getProjectDir() + "/etc"};
    }

    /**
     * 合并map 信息
     *
     * @return
     */
    public static void megrMap(Map<String, List<String[]>> map0, Map<String, Map<String, String>> map1) {
        for (Map.Entry<String, Map<String, String>> component_entry : map1.entrySet()) {
            if (!map0.containsKey(component_entry.getKey())) {
                map0.put(component_entry.getKey(), new ArrayList<>());
            }
            map0.get(component_entry.getKey()).add(new String[]{"id", "host", "runtime", "emitted", "executed", "failed"});
            for (Map.Entry<String, String> exector_entry : component_entry.getValue().entrySet()) {
                JSONObject json = JSONObject.parseObject(exector_entry.getValue());
                map0.get(component_entry.getKey()).add(new String[]{exector_entry.getKey(), json.getString("host")
                        , json.getInteger("runtime").intValue() / 60 / 1000 + " Minutes"
                        , json.getString("emitted")
                        , json.getString("executed")
                        , json.getString("failed")});
            }
        }

    }

    /**
     * 获取唯一id信息
     *
     * @return
     * @throws Exception
     */
    public static int getId() throws Exception {
        String dir = Configuration.getInstance().getString("cluster.zookeeper.root") + "/seq";
        String seqJson = "{\"timestamp\":" + System.currentTimeMillis() + "}";
        ZkUtils.create(ZkCurator.getInstance().getZkCurator(), dir, seqJson.getBytes(), CreateMode.PERSISTENT);
        return ZkCurator.getInstance().getZkCurator().setData().forPath(dir, seqJson.getBytes()).getVersion();
    }

    public static void main(String[] args) throws Exception {
        //byte[] b = UtilCommons.zipDirectory(new File("D:\\cluster-test-1.0-SNAPSHOT"), "/");
        //UtilCommons.unZipDirectory("D://result/", b);
        UtilCommons.execCommand("cd /APP/cluster-delins-1.0-SNAPSHOT//bin; sh ./worker-start.sh \"/APP/cluster-delins-1.0-SNAPSHOT/workDir/workers/bae5812e-bc9e-4d54-8859-60018c37c8db\" \"37aa2325-7d52-42e0-b3a2-08a5b9480eaf\"  \"bae5812e-bc9e-4d54-8859-60018c37c8db\" \"org.cluster.test.ApplicationTest\" \"-Xmx256M\" \"0\" \"1\" \"/APP/cluster-delins-1.0-SNAPSHOT/etc/\"");
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(UtilCommons.class);
}
