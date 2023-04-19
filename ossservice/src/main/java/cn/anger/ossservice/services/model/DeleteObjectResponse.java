package cn.anger.ossservice.services.model;

import java.util.StringJoiner;

/**
 * @author Anger
 * created on 2023/2/26
 */
public class DeleteObjectResponse extends CliResponse {
    private String bucket;
    private String deletedObjectKey;

    public DeleteObjectResponse(String bucket, String deletedObjectKey) {
        this.bucket = bucket;
        this.deletedObjectKey = deletedObjectKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDeletedObjectKey() {
        return deletedObjectKey;
    }

    public void setDeletedObjectKey(String deletedObjectKey) {
        this.deletedObjectKey = deletedObjectKey;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DeleteObjectResponse.class.getSimpleName() + "[", "]")
            .add("bucket='" + bucket + "'")
            .add("deletedObjectKey='" + deletedObjectKey + "'")
            .toString();
    }
}
