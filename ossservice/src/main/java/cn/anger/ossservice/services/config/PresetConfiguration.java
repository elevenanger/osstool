package cn.anger.ossservice.services.config;


import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.model.OssConfiguration;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anger
 * created on 2023/2/26
 * sequoia aws 预置配置
 */
public enum PresetConfiguration {
    MAC_15_LOCAL(defaultSeqAwsCredentials().withEndPoint("http://192.168.48.129:8002/")),
    FL_DEV(defaultSeqAwsCredentials().withEndPoint("https://fldev.dgcb.com.cn:8080/"));

    PresetConfiguration(OssConfiguration ossConfiguration) {
        this.configuration = ossConfiguration;
    }
    private final OssConfiguration configuration;

    public OssConfiguration getConfiguration() {
        return configuration;
    }


    private static OssConfiguration defaultSeqAwsCredentials() {
        return new OssConfiguration()
                    .withType(Oss.Type.AWS)
                    .withAccessKey("ABCDEFGHIJKLMNOPQRST")
                    .withSecreteKey("abcdefghijklmnopqrstuvwxyz0123456789ABCD");
    }

    public static Map<String, OssConfiguration> presetConfigurations() {
        return EnumSet.allOf(PresetConfiguration.class).stream()
                .collect(Collectors.toMap(
                        PresetConfiguration::name,
                        PresetConfiguration::getConfiguration));
    }

}
