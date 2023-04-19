package cn.anger.ossservice.services.model;

/**
 * @author : anger
 */
public class PutObjectResponse extends CliResponse {
    private String eTag;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @Override
    public String toString() {
        return "PutObjectResponse{" +
                "eTag='" + eTag + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
