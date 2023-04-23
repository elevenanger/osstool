package cn.anger.ossservice.services;

import cn.anger.ossservice.services.model.CliResponse;
import cn.anger.ossservice.services.model.ListObjectsRequest;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Anger
 * created on 2023/4/19
 */
class OssTest {

    static final String BUCKET_NAME = "local";

    static final String TEST_BUCKET = "test";

    static final String FILE_NAME = "test.file";

    static final String OSS_DOWNLOAD_PATH = System.getenv("OSS_DOWNLOAD_PATH");

    static final String OSS_UPLOAD_PATH = System.getenv("OSS_UPLOAD_PATH");

    static Oss oss = OssFactory.getInstance();

    void templateFunction(Supplier<CliResponse> responseSupplier) {
        AtomicReference<CliResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(responseSupplier.get()));
        System.out.println(response);
    }

    @Test
    void listBuckets() {
        templateFunction(oss::listBuckets);
    }

    @Test
    void createAndDeleteBucket() {
        templateFunction(() -> oss.createBucket(TEST_BUCKET));
        templateFunction(() -> oss.deleteBucket(TEST_BUCKET));
    }

    @Test
    void putAndDownloadAndDeleteObject() {
        templateFunction(() -> oss.putObject(
                BUCKET_NAME,
                Paths.get(OSS_UPLOAD_PATH, FILE_NAME).toFile()));

        templateFunction(() ->
                oss.downloadObject(
                        BUCKET_NAME,
                        FILE_NAME,
                        OSS_DOWNLOAD_PATH));

        templateFunction(() -> oss.downloadObject(
                BUCKET_NAME,
                FILE_NAME,
                OSS_DOWNLOAD_PATH,
                "imageMogr2/thumbnail/!50p"));

        templateFunction(() -> oss.deleteObject(BUCKET_NAME, FILE_NAME));
    }

    @Test
    void listObjects() {
        templateFunction(() -> oss.listObjects(new ListObjectsRequest(BUCKET_NAME, "")));
    }

    @Test
    void listAllObjects() {
        templateFunction(() -> oss.listAllObjects(BUCKET_NAME));
    }

    @Test
    void listAllObjectsWithPrefix() {
        templateFunction(() -> oss.listAllObjects(BUCKET_NAME, ""));
    }

    @Test
    void batchUpload() {
        templateFunction(() -> oss.batchUpload(BUCKET_NAME, OSS_UPLOAD_PATH));
    }

    @Test
    void batchDownload() {
        templateFunction(() -> oss.batchDownload(BUCKET_NAME, OSS_DOWNLOAD_PATH, ""));
    }

    @Test
    void batchDelete() {
        templateFunction(() -> oss.batchDelete(BUCKET_NAME, ""));
    }

    @Test
    void getCurrentConfiguration() {
        assertNotNull(oss.getCurrentConfiguration());
        System.out.println(oss.getCurrentConfiguration());
    }
}