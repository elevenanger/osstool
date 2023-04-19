package cn.anger.ossservice.services.cos;

import cn.anger.ossservice.services.model.OssConfiguration;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.region.Region;
import cn.anger.ossservice.services.AbstractOss;
import cn.anger.ossservice.services.model.*;

/**
 * @author Anger
 * 腾讯云对象存储服务实现
 * created on 2023/4/17
 */
public class COSService extends AbstractOss<COSClient> {
    public COSService(OssConfiguration configuration) {
        super(configuration);
    }

    @Override
    public COSClient createClient(OssConfiguration configuration) {
        return new COSInitializer(configuration).cosClient;
    }

    @Override
    public <O> GetObjectResponse<O> getObject(GetObjectRequest request) {
        return execute(request, COSObject.class);
    }

    private static final class COSInitializer {
        private final COSClient cosClient;
        private final OssConfiguration configuration;

        public COSInitializer(OssConfiguration configuration) {
            this.configuration = configuration;
            this.cosClient = initialize();
        }

        private COSClient initialize() {
            String accessKey = configuration.getAccessKey();
            String secreteKey = configuration.getSecreteKey();

            COSCredentials credentials = new BasicCOSCredentials(accessKey, secreteKey);
            Region region = new Region("ap-guangzhou");

            ClientConfig clientConfig = new ClientConfig(region);

            clientConfig.setHttpProtocol(HttpProtocol.https);

            return new COSClient(credentials, clientConfig);
        }
    }
}
