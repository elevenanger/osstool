package cn.anger.ossservice.services;

import cn.anger.ossservice.services.aws.AwsService;
import cn.anger.ossservice.services.config.OssConfigurationStore;
import cn.anger.ossservice.services.cos.COSService;
import cn.anger.ossservice.services.model.OssConfiguration;

/**
 * @author : anger
 * Oss 工厂方法
 */
public class OssFactory {
    OssFactory() {}

    public static Oss getInstance(OssConfiguration configuration) {
        switch (configuration.getType()) {
            case AWS : return new AwsService(configuration);
            case COS : return new COSService(configuration);
            default : return null;
        }
    }

    public static Oss getInstance() {
        return getInstance(OssConfigurationStore.defaultConfiguration());
    }

}
