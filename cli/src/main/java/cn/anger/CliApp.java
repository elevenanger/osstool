package cn.anger;

import cn.anger.cli.OssCli;
import picocli.CommandLine;

/**
 * @author Anger
 * created on 2023/4/19
 */
public class CliApp {
    public static void main(String[] args) {
        int exit = new CommandLine(new OssCli()).execute(args);
        System.exit(exit);
    }
}