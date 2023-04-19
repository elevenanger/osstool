package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 对象下载请求
 */
public class DownloadObjectRequest extends CliRequest {
    private String bucket;
    private String key;
    private String downloadPath;

    private String rule;

    public DownloadObjectRequest() {}

    public DownloadObjectRequest(String bucket, String key, String downloadPath) {
        this.bucket = bucket;
        this.key = key;
        this.downloadPath = downloadPath;
    }

    public DownloadObjectRequest(String bucket, String key, String downloadPath, String rule) {
        this.bucket = bucket;
        this.key = key;
        this.downloadPath = downloadPath;
        this.rule = rule;
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

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
