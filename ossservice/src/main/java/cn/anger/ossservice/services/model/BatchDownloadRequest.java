package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量下载请求
 */
public class BatchDownloadRequest extends CliRequest {
    private String bucket;
    private String prefix;
    private String downloadPath;

    public BatchDownloadRequest(String bucket, String downloadPath) {
        this.bucket = bucket;
        this.downloadPath = downloadPath;
    }

    public BatchDownloadRequest(String bucket, String prefix, String downloadPath) {
        this.bucket = bucket;
        this.prefix = prefix;
        this.downloadPath = downloadPath;
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

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
