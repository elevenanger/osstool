package cn.anger.cli;

import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.OssFactory;
import cn.anger.ossservice.services.config.OssConfigurationStore;
import cn.anger.ossservice.services.model.ListAllObjectRequest;
import com.amazonaws.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static cn.anger.cli.OssCli.oss;
import static com.amazonaws.util.StringUtils.*;

/**
 * @author Anger
 * created on 2023/4/20
 * oss 命令行工具客户端
 */
@Command(
        name = "osscli",
        mixinStandardHelpOptions = true,
        version = "osscli 1.0",
        description = "对象存储命令行工具",
        subcommands = {
                ListCommand.class,
                DeleteCommands.class,
                PutCommand.class,
                GetCommand.class,
                BatchCommands.class
        })
public class OssCli {

    static final AtomicReference<Oss> oss = new AtomicReference<>();

    @Command(name = "init", description = "初始化 oss 客户端")
    void initOssClient(@Parameters String configId) {
        oss.set(OssFactory.getInstance(OssConfigurationStore.getOne(configId)));
        System.out.println("init oss client success, client info => " +
                        configId + " " +
                        oss.get().getCurrentConfiguration().getType() + " " +
                        oss.get().getCurrentConfiguration().getEndPoint());
    }

    public static void main(String[] args) {
        CommandLine commandLine =
            new CommandLine(new OssCli());
        commandLine.execute("-h");
        commandLine.execute("init", "mac_15");
        commandLine.execute("ls");
        commandLine.execute("ls", "-b", "local");
        commandLine.execute("ls", "conf");
        commandLine.execute("put", "bucket", "test1", "test2");
        commandLine.execute("delete", "bucket", "test1", "test2");
        commandLine.execute("put", "object", "/Users/liuanglin/data/oss/upload/oss_test.jpeg", "-b", "local");
        commandLine.execute("delete", "object", "oss_test.jpeg", "-b", "local");
    }
}

@Command(name = "ls",
        description = "展示资源列表")
class ListCommand implements Runnable {

    @Option(names = "-b")
    String bucket;

    @Option(names = "--prefix", defaultValue = "")
    String prefix;

    @Option(names = "conf")
    boolean conf;

    @Override
    public void run() {
        if (conf)
            System.out.println(OssConfigurationStore.getAllAsString());
        else if (hasValue(bucket)) {
            ListAllObjectRequest request = new ListAllObjectRequest(bucket, prefix);
            System.out.println(oss.get().listAllObjects(request));
        }
        else
            System.out.println(oss.get().listBuckets());
    }
}

@Command(name = "delete", description = "删除操作")
class DeleteCommands implements Runnable {

    @Option(names = {"bucket"})
    boolean bucket;

    @Parameters(arity = "1..")
    String[] params;

    @CommandLine.ArgGroup(exclusive = false)
    ObjectArgs objectArgs;

    static class ObjectArgs {
        @Option(names = {"object"})
        boolean object;

        @Option(names = {"-b"})
        String targetBucket;
    }

    @Override
    public void run() {
        if (Objects.nonNull(objectArgs) &&
                objectArgs.object &&
                hasValue(objectArgs.targetBucket))
            Arrays.stream(params)
                    .map(o -> oss.get().deleteObject(objectArgs.targetBucket, o))
                    .forEach(System.out::println);
        if (bucket)
            Arrays.stream(params)
                    .map(o -> oss.get().deleteBucket(o))
                    .map(r -> r.getBucket().concat(" has deleted."))
                    .forEach(System.out::println);
    }
}

@Command(name = "put", description = "put相关操作")
class PutCommand implements Runnable {

    @CommandLine.ArgGroup(exclusive = false)
    BucketArgs bucketArgs;

    static class BucketArgs {
        @Option(names = {"bucket"}) boolean bucket;
        @Parameters(arity = "1..") String[] buckets;
    }

    @CommandLine.ArgGroup(exclusive = false)
    ObjectArgs objectArgs;

    static class ObjectArgs {
        @Option(names = {"object"}) boolean object;
        @Option(names = {"-b"}) String targetBucket;
        @Parameters(arity = "1..") File[] objects;
    }

    @Override
    public void run() {
        if (Objects.nonNull(bucketArgs) &&
                bucketArgs.bucket && Objects.nonNull(bucketArgs.buckets))
            Arrays.stream(bucketArgs.buckets)
                    .map(b -> oss.get().createBucket(b).getBucket().getName().concat(" created."))
                    .forEach(System.out::println);
        else if (Objects.nonNull(objectArgs) &&
                objectArgs.object &&
                Objects.nonNull(objectArgs.objects) &&
                StringUtils.hasValue(objectArgs.targetBucket))
            Arrays.stream(objectArgs.objects)
                    .map(o -> oss.get().putObject(objectArgs.targetBucket, o))
                    .map(r -> r.getKey() + " has put to " + objectArgs.targetBucket)
                    .forEach(System.out::println);
    }
}

@Command(name = "get", description = "get 相关操作")
class GetCommand implements Runnable {

    @Option(names = {"-b"}, required = true)
    String bucket;

    @Option(names = {"--local-path"}, required = true)
    String localPath;

    @Option(names = {"--rule"}, defaultValue = "")
    String rule;

    @Parameters(arity = "1..")
    String[] objects;

    @Override
    public void run() {
        Arrays.stream(objects)
                .map(key -> oss.get().downloadObject(bucket, key, localPath, rule))
                .forEach(System.out::println);
    }
}

@Command(name = "batch", description = "批量操作")
class BatchCommands implements Runnable {

    @Option(names = {"-b"}, required = true)
    String bucket;

    @Option(names = {"--local-path"})
    String localPath;

    @Option(names = "--prefix", defaultValue = "")
    String prefix;

    @Option(names = {"--rule"}, defaultValue = "")
    String rule;

    @CommandLine.ArgGroup
    Operation operation;

    static class Operation {
        @Option(names = {"get"}, required = true)
        boolean get;

        @Option(names = {"put"}, required = true)
        boolean put;

        @Option(names = {"delete"}, required = true)
        boolean delete;
    }

    @Override
    public void run() {
        if (operation.get && hasValue(localPath))
            System.out.println(oss.get().batchDownload(bucket, localPath, prefix, rule));
        else if (operation.delete)
            System.out.println(oss.get().batchDelete(bucket, prefix));
        else if (operation.put && hasValue(localPath))
            System.out.println(oss.get().batchUpload(bucket, localPath));
    }
}