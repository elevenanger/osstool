package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量上传操作请求返回数据
 */
public class BatchUploadResponse extends BatchOperationResponse {
    @Override
    protected String getOperationName() {
        return "upload";
    }
}
