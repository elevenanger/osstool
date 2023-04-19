package cn.anger.ossservice.services.model;

/**
 * @author Anger
 * created on 2023/2/26
 */
public class PutBucketResponse extends CliResponse {

    private Bucket bucket;

    public PutBucketResponse(Bucket bucket) {
        this.bucket = bucket;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }
}
