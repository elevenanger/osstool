package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量上传接口
 */
public class BatchUploadRequest extends CliRequest {
    String bucket;
    String localPath;

    public BatchUploadRequest(String bucket, String localPath) {
        this.bucket = bucket;
        this.localPath = localPath;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
