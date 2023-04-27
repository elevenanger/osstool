package cn.anger.cli;

import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.OssFactory;
import cn.anger.ossservice.services.config.OssConfigurationStore;
import com.amazonaws.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static cn.anger.cli.OssCli.oss;
import static com.amazonaws.util.StringUtils.hasValue;

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

    static final Consumer<Oss> SHOW_OSS_INFO = o ->
            System.out.println(
                "oss 客户端初始化成功 => " +
                    o.getCurrentConfiguration().getType() + " " +
                    o.getCurrentConfiguration().getEndPoint());

    static {
        if (Objects.nonNull(OssFactory.getInstance())) {
            oss.set(OssFactory.getInstance());
            SHOW_OSS_INFO.accept(oss.get());
        }
    }

    @Command(name = "init", description = "通过配置初始化 oss 客户端")
    void initOssClient(@Parameters String configId) {
        oss.set(OssFactory.getInstance(OssConfigurationStore.getOne(configId)));
        SHOW_OSS_INFO.accept(oss.get());
    }

}

@Command(name = "ls",
        description = "展示资源列表")
class ListCommand implements Runnable {

    @Parameters(description = "桶名 指定桶名则列出桶中的对象，不指定桶名则列出所有桶",
                arity = "0..1")
    String bucket;

    @Option(names = "--prefix",
            defaultValue = "",
            description = "对象前缀")
    String prefix;

    @Option(names = "conf",
            description = "列出所有配置信息")
    boolean conf;

    @Override
    public void run() {
        if (conf)
            System.out.println(OssConfigurationStore.getAllAsString());
        else if (hasValue(bucket))
            System.out.println(oss.get().listAllObjects(bucket, prefix));
        else
            System.out.println(oss.get().listBuckets());
    }
}

@Command(name = "delete", description = "删除操作")
class DeleteCommands implements Runnable {

    @Option(names = {"bucket"},
            description = "删除桶")
    boolean bucket;

    @Parameters(arity = "1..",
            description = "参数数量可以为多个")
    String[] params;

    @CommandLine.ArgGroup(exclusive = false)
    ObjectArgs objectArgs;

    static class ObjectArgs {
        @Option(names = {"object"},
                description = "删除对象")
        boolean object;

        @Option(names = {"-b"}, description = "对象所在桶")
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
                    .map(r -> r.getBucket().concat(" 删除成功"))
                    .forEach(System.out::println);
    }
}

@Command(name = "put", description = "put相关操作")
class PutCommand implements Runnable {

    @CommandLine.ArgGroup(exclusive = false)
    BucketArgs bucketArgs;

    static class BucketArgs {
        @Option(names = {"bucket"},
                description = "创建桶")
        boolean bucket;
        @Parameters(arity = "1..",
                    description = "创建的桶名，可以为多个")
        String[] buckets;
    }

    @CommandLine.ArgGroup(exclusive = false)
    ObjectArgs objectArgs;

    static class ObjectArgs {
        @Option(names = {"object"},
                description = "上传对象")
        boolean object;
        @Option(names = {"-b"},
                description = "目标桶")
        String targetBucket;
        @Parameters(arity = "1..",
                    description = "上传的文件，可以为多个")
        File[] objects;
    }

    @Override
    public void run() {
        if (Objects.nonNull(bucketArgs) &&
                bucketArgs.bucket && Objects.nonNull(bucketArgs.buckets))
            Arrays.stream(bucketArgs.buckets)
                    .map(b -> oss.get().createBucket(b).getBucket().getName().concat(" 创建成功"))
                    .forEach(System.out::println);
        else if (Objects.nonNull(objectArgs) &&
                objectArgs.object &&
                Objects.nonNull(objectArgs.objects) &&
                StringUtils.hasValue(objectArgs.targetBucket))
            Arrays.stream(objectArgs.objects)
                    .map(o -> oss.get().putObject(objectArgs.targetBucket, o))
                    .map(r -> r.getKey() + " put 至 " + objectArgs.targetBucket)
                    .forEach(System.out::println);
    }
}

@Command(name = "get", description = "get 相关操作")
class GetCommand implements Runnable {

    @Option(names = {"-b"},
            description = "桶名",
            required = true)
    String bucket;

    @Option(names = {"--local-path"},
            description = "本地下载路径",
            required = true)
    String localPath;

    @Option(names = {"--rule"},
            description = "指定规则，下载时对对象做相应处理",
            defaultValue = "")
    String rule;

    @Parameters(arity = "1..",
                description = "对象列表")
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

    @Option(names = {"-b"},
            description = "桶名",
            required = true)
    String bucket;

    @Option(names = {"--local-path"},
            description = "本地路径，get 操作将对象批量下载到此路径；put 操作将此路径的文件批量上传到对应桶中")
    String localPath;

    @Option(names = "--prefix",
            description = "对象前缀",
            defaultValue = "")
    String prefix;

    @Option(names = {"--rule"},
            description = "规则",
            defaultValue = "")
    String rule;

    @CommandLine.ArgGroup
    Operation operation;

    static class Operation {
        @Option(names = {"get"},
                required = true)
        boolean get;

        @Option(names = {"put"},
                required = true)
        boolean put;

        @Option(names = {"delete"},
                required = true)
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