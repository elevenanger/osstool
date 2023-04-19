package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 请求一个桶中所有的对象
 */
public class ListAllObjectRequest extends CliRequest {
    private String bucket;
    private String prefix;

    public ListAllObjectRequest(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public ListAllObjectRequest(String bucket) {
        this.bucket = bucket;
        this.prefix = "";
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
