package cn.anger.ossservice.services.model;

import cn.anger.ossservice.services.Oss;

/**
 * @author : anger
 * 对象存储客户端配置信息
 */
public class OssConfiguration {

    private Oss.Type type;

    /**
     * 地址
     */
    private String endPoint;
    private String accessKey;
    private String secreteKey;

    public void setType(Oss.Type type) {
        this.type = type;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecreteKey(String secreteKey) {
        this.secreteKey = secreteKey;
    }

    public Oss.Type getType() {
        return type;
    }

    public OssConfiguration withType(Oss.Type type) {
        this.type = type;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public OssConfiguration withEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public OssConfiguration withAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getSecreteKey() {
        return secreteKey;
    }

    public OssConfiguration withSecreteKey(String secreteKey) {
        this.secreteKey = secreteKey;
        return this;
    }

    @Override
    public String toString() {
        return "OssConfiguration{" +
                "type=" + type +
                ", endPoint='" + endPoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secreteKey='" + secreteKey + '\'' +
                '}';
    }
}
