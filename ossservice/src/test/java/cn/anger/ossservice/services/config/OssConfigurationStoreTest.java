package cn.anger.ossservice.services.config;

import cn.anger.ossservice.services.model.OssConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anger
 * created on 2023/4/19
 */
class OssConfigurationStoreTest {

    @Test
    void getAll() {
        AtomicReference<Map<String, OssConfiguration>> configurationMap = new AtomicReference<>();
        assertDoesNotThrow(() -> configurationMap.set(OssConfigurationStore.getAll()));
        configurationMap.get().entrySet().forEach(System.out::println);
    }

    @Test
    void getAllAsString() {
        AtomicReference<String> all = new AtomicReference<>();
        assertDoesNotThrow(() -> all.set(OssConfigurationStore.getAllAsString()));
        System.out.println(all.get());
    }

}