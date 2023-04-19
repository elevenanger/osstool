package cn.anger.ossservice.services.model;

/**
 * @author Anger
 * created on 2023/2/26
 * 删除对象请求对象
 */
public class DeleteObjectRequest extends CliRequest {
    private String bucket;
    private String key;

    public DeleteObjectRequest(String bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
