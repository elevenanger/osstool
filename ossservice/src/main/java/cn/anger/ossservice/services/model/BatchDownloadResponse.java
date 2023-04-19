package cn.anger.ossservice.services.model;

/**
 * @author : anger
 * 批量下载结果
 */
public class BatchDownloadResponse extends BatchOperationResponse {
    @Override
    protected String getOperationName() {
        return "download";
    }
}
