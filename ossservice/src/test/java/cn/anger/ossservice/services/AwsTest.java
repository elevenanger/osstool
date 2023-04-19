package cn.anger.ossservice.services;

import cn.anger.ossservice.services.config.OssConfigurationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Anger
 * created on 2023/4/19
 */
class AwsTest extends OssTest {
    @BeforeEach
    void setUp() {
        oss = OssFactory.getInstance(OssConfigurationStore.getOne("FL_DEV"));
        bucket = "angersbucket";
    }

    @Test
    void baseObjectTests() {
        listObjects();
        putObject();
        downloadObject();
        deleteObject();
    }

    @Test
    void batchTests() {
        batchDownload();
    }

}
