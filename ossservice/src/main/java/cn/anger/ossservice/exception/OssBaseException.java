package cn.anger.ossservice.exception;

/**
 * @author : anger
 * 所有异常的基类
 */
public class OssBaseException extends RuntimeException {
    public OssBaseException(String message) {
        super(message);
    }

    public OssBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public OssBaseException(Throwable cause) {
        super(cause);
    }
}
