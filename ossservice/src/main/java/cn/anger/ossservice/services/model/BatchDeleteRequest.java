package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量删除请求
 */
public class BatchDeleteRequest extends CliRequest {
    private String bucket;
    private String prefix;

    public BatchDeleteRequest(String bucket) {
        this.bucket = bucket;
    }

    public BatchDeleteRequest(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
