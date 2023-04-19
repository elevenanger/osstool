package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 对象下载返回数据
 */
public class DownloadObjectResponse extends CliResponse {
    private String bucket;
    private String key;
    private String localPath;
    private long size;

    public DownloadObjectResponse() {}

    public DownloadObjectResponse(String bucket, String key, String localPath, long size) {
        this.bucket = bucket;
        this.key = key;
        this.localPath = localPath;
        this.size = size;
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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DownloadObjectResponse{" +
                "bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", localPath='" + localPath + '\'' +
                ", size=" + size +
                '}';
    }
}
