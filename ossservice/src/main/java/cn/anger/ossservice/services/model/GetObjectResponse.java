package cn.anger.ossservice.services.model;

import java.io.InputStream;

/**
 * @author Anger
 * created on 2023/4/17
 */
public class GetObjectResponse<O> extends CliResponse {

    OssObject<O> ossObject;

    InputStream objectContent;

    public OssObject<O> getOssObject() {
        return ossObject;
    }

    public void setOssObject(O obj) {
        OssObject<O> object = new OssObject<>();
        object.setObject(obj);
        this.ossObject = object;
    }

    public InputStream getObjectContent() {
        return objectContent;
    }

    public void setObjectContent(InputStream objectContent) {
        this.objectContent = objectContent;
    }

}
