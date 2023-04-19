package cn.anger.ossservice.services;

import cn.anger.ossservice.services.config.OssConfigurationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Anger
 * created on 2023/4/19
 */
class CosTest extends OssTest {
    @BeforeEach
    void setUp() {
        oss = OssFactory.getInstance(OssConfigurationStore.getOne("COS_ANGER"));
        bucket = "anger-1317673019";
    }

    @Test
    void bucketBaseTest() {
        listBuckets();
    }

    @Test
    void baseObjectTests() {
        listObjects();
        putObject();
        downloadObjectWithRule();
        deleteObject();
    }

    @Test
    void batchTests() {
        batchUpload();
        batchDownload();
        batchDelete();
    }
}
