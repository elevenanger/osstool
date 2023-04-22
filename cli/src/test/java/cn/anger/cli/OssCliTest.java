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
                cmd.execute("ls", "-b", BUCKET);
                cmd.execute("ls", "-b", BUCKET, "--prefix", "src");
            }
        );
    }

    @Test
    void testPutAndDeleteObjectCommand() {
        assertDoesNotThrow(() -> {
            cmd.execute(
                "put", "object",
                UPLOAD_DIR + TEST_FILE,
                "-b", "local");

            cmd.execute("delete", "object", TEST_FILE, "-b", BUCKET);
        });
    }

    @Test
    void testPutAndDeleteBucket() {
        assertDoesNotThrow(() -> {
            cmd.execute("put", "bucket", BUCKET.concat("1"), BUCKET.concat("2"));
            cmd.execute("delete", "bucket", BUCKET.concat("1"), BUCKET.concat("2"));
        });
    }

}