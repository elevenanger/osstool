package cn.anger.ossservice.services.aws;

import cn.anger.ossservice.services.AbstractOss;
import cn.anger.ossservice.services.model.OssConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author : anger
 * Oss 服务巨杉 AWS 协议实现
 */
public class AwsService extends AbstractOss<AmazonS3> {

    public AwsService(OssConfiguration configuration) {
        super(configuration);
    }

    @Override
    public AmazonS3 createClient(OssConfiguration configuration) {
        return new AmazonS3Initializer(configuration).s3;
    }

    private static final class AmazonS3Initializer {
        private final AmazonS3 s3;
        private final OssConfiguration configuration;

        AmazonS3Initializer(OssConfiguration configuration) {
            this.configuration = configuration;
            s3 = initialize();
        }

        private AmazonS3 initialize() {
            String endPoint = configuration.getEndPoint();
            String accessKey = configuration.getAccessKey();
            String secreteKey = configuration.getSecreteKey();

            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secreteKey);
            AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endPoint, null);

            return AmazonS3ClientBuilder.standard()
                                        .withEndpointConfiguration(endpointConfiguration)
                                        .withPathStyleAccessEnabled(true)
                                        .withCredentials(provider)
                                        .build();
        }
    }
}