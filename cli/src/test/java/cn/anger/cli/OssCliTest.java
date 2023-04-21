package cn.anger.cli;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * @author Anger
 * created on 2023/4/21
 */
class OssCliTest {

    private static final CommandLine cmd = new CommandLine(new OssCli());

    private static final String UPLOAD_DIR = System.getenv("OSS_UPLOAD_PATH");

    private static final String DOWNLOAD_DIR = System.getenv("OSS_DOWNLOAD_PATH");

    @BeforeAll
    static void setUp() {
        cmd.execute("-h");
        cmd.execute("init", "mac_15");
    }

    @Test
    void testListCommand() {
        cmd.execute("ls");
        cmd.execute("ls", "-b", "local");
    }

    @Test
    void testPutCommand() {
        cmd.execute(
                "put", "object",
                UPLOAD_DIR + "values.yaml",
                UPLOAD_DIR + "oss_test.jpeg",
                "-b", "local");
    }

    @Test
    void testPutAndDeleteBucket() {
        cmd.execute("put", "bucket", "test1", "test2");
        cmd.execute("delete", "bucket", "test1", "test2");
    }

}