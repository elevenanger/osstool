package cn.anger.cli;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anger
 * created on 2023/4/21
 */
class OssCliTest {

    private static final CommandLine cmd = new CommandLine(new OssCli());

    private static final String UPLOAD_DIR = System.getenv("OSS_UPLOAD_PATH");

    private static final String TEST_FILE = "test.file";

    private static final String TEST_BUCKET = "test";

    private static final String DOWNLOAD_DIR = System.getenv("OSS_DOWNLOAD_PATH");

    private static final String CONFIG = System.getenv("OSS_CONFIG");

    static final String BUCKET = System.getenv("BUCKET");

    @BeforeAll
    static void setUp() {
        cmd.execute("-h");
        cmd.execute("init", CONFIG);
    }

    @Test
    void testListCommand() {
        assertDoesNotThrow(
            () -> {
                cmd.execute("ls");
                cmd.execute("ls", BUCKET);
                cmd.execute("ls", BUCKET, "--prefix", "src");
            }
        );
    }

    @Test
    void testPutAndGetAndDeleteObjectCommand() {
        assertDoesNotThrow(() -> {
            cmd.execute(
                "put", "object",
                UPLOAD_DIR + TEST_FILE,
                "-b", "local");
            cmd.execute("get", TEST_FILE, "--local-path", DOWNLOAD_DIR, "-b", BUCKET);
            cmd.execute("delete", "object", TEST_FILE, "-b", BUCKET);
        });
    }

    @Test
    void testPutAndDeleteBucket() {
        assertDoesNotThrow(() -> {
            cmd.execute("put", "bucket", TEST_BUCKET.concat("1"), TEST_BUCKET.concat("2"));
            cmd.execute("delete", "bucket", TEST_BUCKET.concat("1"), TEST_BUCKET.concat("2"));
        });
    }

}