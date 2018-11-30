package org.cluster.shell.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: 赵云海
 * @Date: 2018/11/7 11:02
 * @Version: 1.0
 * @Description: TODO
 */
public class Help {
    /**
     * 命令主函数, 执行shell命令后调用此方法
     */
    public void exec(String[] args) throws Exception {
        Options opts = new Options();
        opts.addOption("command", true, "command.");
        CommandLine cliParser = new GnuParser().parse(opts, args);
        if (!cliParser.hasOption("command")) {
            logger.info(
                    "\nCommands:\n" +
                            "        deploy\n" +
                            "        destroy\n" +
                            "        start\n" +
                            "        kill\n" +
                            "        state\n" +
                            "        rebalance --category = <?>\n" +
                            "        list --command = <application/worker/broker>\n" +
                            "Help:\n" +
                            "        help\n" +
                            "        help --command = deploy\n");
        }else {
            switch (cliParser.getOptionValue("command")) {
                case "deploy":logger.info(new Deploy().getOptions().getOptions().toString());
                case "destroy":logger.info(new Destroy().getOptions().getOptions().toString());
                case "start":logger.info(new Start().getOptions().getOptions().toString());
                case "kill":logger.info(new Kill().getOptions().getOptions().toString());
            }
        }
        return;
    }

    public static void main(String[] args) throws Exception {
        new Help().exec(args);
    }
    /**
     * 日志定义 Logger
     */
    public static Logger logger = LoggerFactory.getLogger(Start.class);
}
