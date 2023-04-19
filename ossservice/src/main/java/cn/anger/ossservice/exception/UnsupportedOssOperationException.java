package cn.anger.ossservice.exception;

/**
 * @author : anger
 * 未实现的对象存储方法应当抛出此异常
 */
public class UnsupportedOssOperationException extends OssBaseException {

    public UnsupportedOssOperationException() {
        super("不支持的对象存储操作，通过继承 AbstractOss 实现方法");
    }

}
