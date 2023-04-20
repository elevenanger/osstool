package cn.anger.cli;

import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.OssFactory;
import cn.anger.ossservice.services.config.OssConfigurationStore;
import com.amazonaws.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static cn.anger.cli.OssCli.oss;

/**
 * @author Anger
 * created on 2023/4/20
 * oss 命令行工具客户端
 */
// todo 未完成
@Command(
        name = "osscli",
        mixinStandardHelpOptions = true,
        version = "osscli 1.0",
        description = "对象存储命令行工具",
        subcommands = {ConfigCommands.class,
                        BucketCommands.class,
                        PutCommands.class})
public class OssCli implements Runnable {

    public static final AtomicReference<Oss> oss = new AtomicReference<>(OssFactory.getInstance());

    @Option(names = "init-from")
    String configId;

    @Override
    public void run() {
        if (!StringUtils.isNullOrEmpty(configId))
            oss.set(OssFactory.getInstance(OssConfigurationStore.getOne(configId)));
    }

    @ArgGroup(exclusive = false)
    ObjectDeleteOperation objectDeleteOperation;

    static class ObjectDeleteOperation {

        @Option(names = {"--bucket"},
                description = "桶名",
                required = true)
        String bucket;

        @Option(names = "--delete",
                description = "删除文件",
                required = true)
        List<String> deleteObjects;

    }

    public static void main(String[] args) {
        new CommandLine(new OssCli())
                .execute("-h");
    }

}

@Command(name = "conf", aliases = {"-c"})
class ConfigCommands implements Runnable {

    @ArgGroup
    ExclusiveConfigOptions options;

    static class ExclusiveConfigOptions {
        @Option(names = {"-a", "--all"},
                description = "列出所有 oss 配置信息")
        boolean listAll;

        @Option(names = {"--current"},
                description = "显示当前配置信息")
        boolean current;
    }

    @Override
    public void run() {
        if (options.listAll)
            System.out.println(OssConfigurationStore.getAllAsString());
        if (options.current)
            System.out.println(oss.get().getCurrentConfiguration());
    }

}

@Command(name = "bucket", aliases = {"-b"})
class BucketCommands implements Runnable {

    @ArgGroup
    ExclusiveBucketOptions options;

    static class ExclusiveBucketOptions {
        @Option(names = {"-a", "--all"},
                description = "获取所有的 bucket 信息")
        boolean all;

        @Option(names = {"-c", "--create"},
                description = "创建 bucket")
        String createBucketName;

        @Option(names = {"-d", "--delete"},
                description = "删除 bucket")
        String deleteBucketName;
    }



    @Override
    public void run() {
        if (options.all)
            System.out.println(oss.get().listBuckets());
        if (!StringUtils.isNullOrEmpty(options.createBucketName))
            oss.get().createBucket(options.createBucketName);
        if (!StringUtils.isNullOrEmpty(options.deleteBucketName))
            oss.get().deleteBucket(options.deleteBucketName);
    }
}

@Command(name = "put")
class PutCommands implements Runnable {

    @ArgGroup
    PutBucketCommand putBucketCommand;

    @ArgGroup
    PutObjectCommand putObjectCommand;

    static class PutBucketCommand {
        @Option(names = {"bucket"},
                description = "创建桶",
                required = true)
        boolean bucket;

        @Parameters(description = "要创建的桶，可以为多个", arity = "1..")
        List<String> buckets;
    }

    static class PutObjectCommand {
        @Option(names = {"object"}, description = "上传对象", required = true)
        boolean object;

        @Option(names = {"-b", "--bucket"})
        String bucket;

        @Parameters(description = "要上传的文件，可以为多个", arity = "1..")
        List<File> objects;
    }

    @Override
    public void run() {
        if (putBucketCommand.bucket)
            putBucketCommand.buckets.forEach(b -> oss.get().createBucket(b));
        if (putObjectCommand.object)
            putObjectCommand.objects.forEach(o -> oss.get().putObject(putObjectCommand.bucket, o));
    }
}