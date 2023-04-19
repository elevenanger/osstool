package cn.anger.ossservice.services.model;

import java.io.File;
import java.io.InputStream;
import java.util.function.UnaryOperator;

/**
 * @author : anger
 */
public class PutObjectRequest extends CliRequest {

    public PutObjectRequest() {}

    public PutObjectRequest(String bucketName, String key, File file) {
        this.bucketName = bucketName;
        this.key = keyTrans.apply(key);
        this.file = file;
    }

    private static final UnaryOperator<String> keyTrans = k -> k.replace("\\", "/");

    private String bucketName;
    private String key;
    private File file;
    private InputStream inputStream;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = keyTrans.apply(key);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
