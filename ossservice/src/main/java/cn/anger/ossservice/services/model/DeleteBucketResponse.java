package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 删除桶 response
 */
public class DeleteBucketResponse extends CliResponse {
    private String bucket;

    public DeleteBucketResponse(String bucket) {
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
