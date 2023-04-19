package cn.anger.ossservice.services.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author : anger
 * 批量上传响应
 */
public abstract class BatchOperationResponse extends CliResponse {

    protected abstract String getOperationName();

    private final List<OperationResult> operationResults = new ArrayList<>();

    private final BatchOperationStatistics statistics = new BatchOperationStatistics();

    private int batchSize;

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        if (batchSize > 0)
            this.statistics.start();
    }

    public void addSuccessResult(String key) {
        addResult(key, true, "");
    }

    public void addErrorResult(String key, String errorMsg) {
        addResult(key, false, errorMsg);
    }

    private void addResult(String key, boolean success, String msg) {
        OperationResult result = new OperationResult(key, success, msg);
        operationResults.add(result);

    }

    public List<String> getUploadResults() {
        return Collections.unmodifiableList(new ArrayList<>(operationResults)).stream()
                .map(OperationResult::toString)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return statistics.toString();
    }

    private final class OperationResult {
        private final String key;
        private final boolean success;
        private final String errorMsg;

        public OperationResult(String key, boolean success, String errorMsg) {
            this.key = key;
            this.success = success;
            this.errorMsg = errorMsg;
            statistics.updateStatistics(this);
        }

        @Override
        public String toString() {
            return
                getOperationName() + " " + key +
                    (success ? " success" :
                                " failed, error msg " + errorMsg);
        }
    }

    private final class BatchOperationStatistics {
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicInteger currentCount = new AtomicInteger(0);
        private final AtomicLong time = new AtomicLong(0);

        public void start() {
            time.compareAndSet(0, System.nanoTime());
        }

        public void updateStatistics(final OperationResult result) {
            int cur;
            if ((cur = currentCount.incrementAndGet()) == batchSize)
                time.set(System.nanoTime() - time.get());
            if (result.success)
                successCount.incrementAndGet();

            System.out.println(cur + " / " + batchSize + " " + result);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "batch " + getOperationName() + " result" +  "[", "]")
                        .add("totalCount=" + batchSize)
                        .add("successCount=" + successCount)
                        .add("failedCount=" + (batchSize - successCount.get()))
                        .add("time cost=" + (time.get() / 1_000_000) + " ms")
                        .toString();
        }
    }

}
