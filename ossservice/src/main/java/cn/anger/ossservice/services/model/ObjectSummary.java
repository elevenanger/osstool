package cn.anger.ossservice.services.model;

import cn.anger.utils.file.FileSize;

import java.util.Date;

/**
 * @author : anger
 * 对象信息
 */
public class ObjectSummary {
    private String bucket;
    private String key;
    private Date lastModified;
    private long size;
    private String eTag;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ObjectSummary{" +
            "bucket='" + bucket + '\'' +
            ", key='" + key + '\'' +
            ", lastModified=" + lastModified +
            ", size=" + FileSize.toFixed(size) +
            '}';
    }
}
