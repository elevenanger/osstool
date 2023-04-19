package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 获取对象请求
 */
public class GetObjectRequest extends CliRequest {

    public GetObjectRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }

    private String bucketName;
    private String key;
    private String versionId;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}
