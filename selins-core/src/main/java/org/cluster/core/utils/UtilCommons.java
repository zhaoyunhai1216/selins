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
import org.cluster.core.commons.Environment;
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
        return new JSONObject().fluentPutAll(Configuration.getInstance().getInnerMap())
                .fluentPut(Environment.VCORES_USED.key(), EnvCommons.getCpuStat())
                .fluentPut(Environment.MEMOTY_USED.key(), EnvCommons.getUsedMemoryStat())
                .fluentPut(Environment.HDD_USED.key(), EnvCommons.getUsedFileSystemStat()).toJSONString();
    }


    public static void startCommand(String workerId, String[] args) throws Exception {
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
        while (thread.isAlive() && !ZkUtils.checkWorkerExists(ZkCurator.getInstance().getZkCurator(), workerId)) {
            TimeUnit.SECONDS.sleep(1);
            logger.info("[Env] Worker <" + workerId + "> is starting, please wait.");
        }
        logger.info("[Env] Worker <" + workerId + "> is start finish.");
    }

    public static void killCommand(String processID) throws Exception {
        // Set the necessary command to execute the application master
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);
        vargs.add("kill -15 " + processID + "");
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


    public static String[] getWorkerParameters(String applicationID, int seq, int total) throws Exception {
        String workerDir = Configuration.getInstance().getString(Environment.WORKER_DIR);
        AppResource resource = ZkUtils.getAppZkResource(applicationID);
        String host = Configuration.getInstance().getString(Environment.CLUSTER_HOST);
        return new String[]{workerDir, applicationID + "_" + seq + "_" + total, resource.getString(AppResource.Fileds.JVM_OPTS), "--host=" + host
                , "--appID=" + resource.getString(AppResource.Fileds.ID), "--class=" + resource.getString(AppResource.Fileds.CLASS)
                , "--seq=" + seq, "--total=" + total, "--category=" + resource.getString(AppResource.Fileds.CATEGORY), "--yaml=" + Configuration.getProjectDir() + "/etc"};
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
