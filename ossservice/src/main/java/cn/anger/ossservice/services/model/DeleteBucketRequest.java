package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 删除桶 request
 */
public class DeleteBucketRequest extends CliRequest {

    private String bucket;

    public DeleteBucketRequest(String bucket) {
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
