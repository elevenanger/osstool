package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量删除结果
 */
public class BatchDeleteResponse extends BatchOperationResponse {
    @Override
    protected String getOperationName() {
        return "delete";
    }
}
