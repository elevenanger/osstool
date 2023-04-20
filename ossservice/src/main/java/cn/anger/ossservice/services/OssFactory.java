package cn.anger.ossservice.services;

import cn.anger.ossservice.services.aws.AwsService;
import cn.anger.ossservice.services.config.OssConfigurationStore;
import cn.anger.ossservice.services.cos.COSService;
import cn.anger.ossservice.services.model.OssConfiguration;

import static cn.anger.ossservice.services.Oss.Type.*;

/**
 * @author : anger
 * Oss 工厂方法
 */
public class OssFactory {
    OssFactory() {}

    public static Oss getInstance(OssConfiguration configuration) {
        return switch (configuration.getType()) {
            case AWS -> new AwsService(configuration);
            case COS -> new COSService(configuration);
            default -> null;
        };
    }

    public static Oss getInstance() {
        return getInstance(OssConfigurationStore.defaultConfiguration());
    }

}
