package org.cluster.application.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cluster.application.backtype.tuple.Tuple;
import org.cluster.application.base.BaseBolt;
import org.cluster.application.commons.ApplicationContext;
import org.cluster.application.commons.EnvOptions;
import org.cluster.application.commons.Environment;
import org.cluster.application.executor.ExectorsManager;
import org.cluster.application.tracker.EnumLogger;
import org.cluster.application.tracker.LoggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) arg1.getClass());
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
     * 获取当前时间
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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

    /**
     * 进行程序克隆一个新的IComponent对象
     */
    public static BaseBolt getIComponent(BaseBolt declarer) throws Exception {
        return (BaseBolt) BeanUtils.cloneBean(declarer);
    }


    /**
     * 构造方法
     */
    public static JSONObject getYaml(String conf) throws IOException {
        return new Yaml().loadAs(FileUtils.openInputStream(new File(conf + "/conf/cluster.yaml")), JSONObject.class);
    }

    /**
     * 获取某个范围的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 获取本机可用端口号
     */
    public static int getUsablePort() throws IOException {
        ArrayList<Integer> ports = new ArrayList();
        for (int i = 10000; i < 65536; i++) {
            ports.add(i);
        }
        while (!ports.isEmpty()) {
            Socket socket = null;
            int index = new Random().nextInt(ports.size());
            int port = ports.get(index).intValue();
            try {
                socket = new Socket(InetAddress.getByName("127.0.0.1"), port);
                ports.remove(index);
                socket.close();
            } catch (IOException e) {
                return port;
            }
        }
        throw new IOException("There is no available port on this machine.");

    }

    public static void main(String[] args) throws IOException {
        System.out.println(getUsablePort());
    }

    /**
     * 格式化启动application 应用参数
     *
     * @param args
     * @return
     */
    public static EnvOptions getCliParser(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        Options opts = new Options().addOption("host", true, "The host.")
                .addOption("category", true, "The category.").addOption("appID", true, "The Application id.")
                .addOption("class", true, "The Application id.").addOption("seq", true, "The help parameter.")
                .addOption("total", true, "The JVM parameter.").addOption("yaml", true, "Yaml configuration file.");
        return new EnvOptions(new GnuParser().parse(opts, args));
    }

    /**
     * 获取当前系统状态, 并格式化成jsonString 后返回
     */
    public static String getWorkerState(EnvOptions config) throws Exception {
        JSONObject jsonString = new JSONObject()
                .fluentPut("workerId", config.getWorkerID()).fluentPut("host", config.getString(Environment.BROKER_HOST))
                .fluentPut("category", config.getOptionValue(Environment.APPLICATION_CATEGORY))
                .fluentPut("process", ManagementFactory.getRuntimeMXBean().getName().split("@")[0])
                .fluentPut("runtime", ManagementFactory.getRuntimeMXBean().getUptime())
                .fluentPut("startTime", ManagementFactory.getRuntimeMXBean().getStartTime())
                .fluentPut("cpu", EnvCommons.getCpuStat()).fluentPut("memory", EnvCommons.getMemStat())
                .fluentPut("exectors", ExectorsManager.getInstance().getExectors().size())
                .fluentPut("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        return jsonString.toJSONString();
    }

    /**
     * 获取当前系统状态, 并格式化成jsonString 后返回
     */
    public static String getExectorState(EnvOptions config, ConcurrentHashMap<String, Long> state) throws Exception {
        Map<String, ApplicationContext> exectors = ExectorsManager.getInstance().getExectors();
        Map<String,JSONArray> comonentMap = new HashMap<>();
        for (Map.Entry<String, ApplicationContext> entry : exectors.entrySet()) {
            String[] keys = entry.getKey().split("\\_");
            JSONObject jsonState = new JSONObject()
                    .fluentPut("workerID", config.getWorkerID())
                    .fluentPut("host", config.getOptionValue(Environment.BROKER_HOST))
                    .fluentPut("seq", keys[1]).fluentPut("total", keys[2])
                    .fluentPut("runtime", ManagementFactory.getRuntimeMXBean().getUptime())
                    .fluentPut("executed", state.getOrDefault(entry.getKey() + "_" + EnumLogger.EXECUTED.key(), 0L))
                    .fluentPut("emitted", state.getOrDefault(entry.getKey() + "_" + EnumLogger.EMITTED.key(), 0L))
                    .fluentPut("failed", state.getOrDefault(entry.getKey() + "_" + EnumLogger.FAILED.key(), 0L));
            comonentMap.computeIfAbsent(keys[0],x->new JSONArray()).add(jsonState);
        }
        return JSONObject.toJSON(comonentMap).toString();
    }

    /**
     * 获取当前系统状态, 并格式化成jsonString 后返回
     */
    public static JSONArray getExectorState1(EnvOptions config, long timestamp, ConcurrentHashMap<String, Long> state) throws Exception {
        Map<String, ApplicationContext> exectors = ExectorsManager.getInstance().getExectors();
        JSONArray exectorState = new JSONArray();
        for (Map.Entry<String, ApplicationContext> entry : exectors.entrySet()) {
            String[] keys = entry.getKey().split("\\_");
            JSONObject jsonState = new JSONObject().fluentPut("component", keys[0]).fluentPut("seq", keys[1]).fluentPut("total", keys[2])
                    .fluentPut("executed", state.getOrDefault(entry.getKey() + "_" + EnumLogger.EXECUTED.key(), 0L))
                    .fluentPut("emitted", state.getOrDefault(entry.getKey() + "_" + EnumLogger.EMITTED.key(), 0L))
                    .fluentPut("failed", state.getOrDefault(entry.getKey() + "_" + EnumLogger.FAILED.key(), 0L));
            exectorState.add(jsonState);
        }
        return exectorState;
    }

    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(UtilCommons.class);
}
