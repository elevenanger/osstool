package cn.anger.ossservice.services;

import cn.anger.ossservice.services.model.CliResponse;
import cn.anger.ossservice.services.model.ListObjectsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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

    public static final String BUCKET_NAME = "test";

    public static final String FILE_NAME = "oss_test.jpeg";

    public static final String OSS_DOWNLOAD_PATH = System.getenv("OSS_DOWNLOAD_PATH");

    public static final String OSS_UPLOAD_PATH = System.getenv("OSS_UPLOAD_PATH");

    Oss oss;

    String bucket;

    void templateFunction(Supplier<CliResponse> responseSupplier) {
        AtomicReference<CliResponse> response = new AtomicReference<>();
        assertDoesNotThrow(() -> response.set(responseSupplier.get()));
        System.out.println(response);
    }

    @TestTemplate
    void baseObjectTests() {}

    @Test
    void listBuckets() {
        templateFunction(oss::listBuckets);
    }

    @Test
    void createBucket() {
        templateFunction(() -> oss.createBucket(BUCKET_NAME));
    }

    @Test
    void deleteBucket() {
        templateFunction(() -> oss.deleteBucket(BUCKET_NAME));
    }

    @Test
    void putObject() {
        templateFunction(() -> oss.putObject(
                bucket,
                Paths.get(OSS_UPLOAD_PATH, FILE_NAME).toFile()));
    }

    @Test
    void downloadObject() {
        templateFunction(() -> oss.downloadObject(
                bucket,
                FILE_NAME,
                OSS_DOWNLOAD_PATH));
    }

    @Test
    void downloadObjectWithRule() {
        templateFunction(() -> oss.downloadObject(
                bucket,
                FILE_NAME,
                OSS_DOWNLOAD_PATH,
                "imageMogr2/thumbnail/!50p"));
    }

    @Test
    void deleteObject() {
        templateFunction(() -> oss.deleteObject(bucket, FILE_NAME));
    }

    @Test
    void listObjects() {
        templateFunction(() -> oss.listObjects(new ListObjectsRequest(bucket, "")));
    }

    @Test
    void listAllObjects() {
        templateFunction(() -> oss.listAllObjects(bucket));
    }

    @Test
    void listAllObjectsWithPrefix() {
        templateFunction(() -> oss.listAllObjects(bucket, ""));
    }

    @Test
    void batchUpload() {
        templateFunction(() -> oss.batchUpload(bucket, OSS_UPLOAD_PATH));
    }

    @Test
    void batchDownload() {
        templateFunction(() -> oss.batchDownload(bucket, OSS_DOWNLOAD_PATH, ""));
    }

    @Test
    void batchDelete() {
        templateFunction(() -> oss.batchDelete(bucket, ""));
    }

    @Test
    void getCurrentConfiguration() {
        assertNotNull(oss.getCurrentConfiguration());
        System.out.println(oss.getCurrentConfiguration());
    }
}