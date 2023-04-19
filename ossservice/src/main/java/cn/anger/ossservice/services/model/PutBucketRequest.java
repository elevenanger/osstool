package cn.anger.ossservice.services.model;

/**
 * @author Anger
 * created on 2023/2/26
 * 创建桶请求
 */
public class PutBucketRequest extends CliRequest {
    private String bucketName;

    public PutBucketRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
