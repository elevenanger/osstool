package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 对象存储单个对象封装器
 */
public class OssObject<O> {
    private O object;

    public O getObject() {
        return object;
    }

    public void setObject(O object) {
        this.object = object;
    }
}
