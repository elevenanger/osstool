package cn.anger.ossservice.services;

import cn.anger.ossservice.exception.OssBaseException;
import cn.anger.ossservice.exception.UnsupportedOssOperationException;
import cn.anger.ossservice.services.model.*;
import cn.anger.ossservice.services.model.transform.RequestTransformers;
import cn.anger.ossservice.services.model.transform.ResponseTransformers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * @author : anger
 * OSS 抽象类，提供方法的默认实现
 */
public abstract class AbstractOss<T> implements Oss, Client<T> {

    protected final T client;

    private final OssConfiguration ossConfiguration;

    protected AbstractOss(OssConfiguration configuration) {
        this.ossConfiguration = configuration;
        this.client = createClient(configuration);
    }

    @Override
    public ListBucketsResponse listBuckets(ListBucketsRequest request) {
        return execute(request);
    }

    @Override
    public ListBucketsResponse listBuckets() {
        return listBuckets(new ListBucketsRequest());
    }

    @Override
    public DeleteBucketResponse deleteBucket(DeleteBucketRequest request) {
        execute(request, Void.class);
        return new DeleteBucketResponse(request.getBucket());
    }

    @Override
    public DeleteBucketResponse deleteBucket(String bucket) {
        return deleteBucket(new DeleteBucketRequest(bucket));
    }

    @Override
    public PutBucketResponse createBucket(PutBucketRequest request) {
        return execute(request);
    }

    @Override
    public PutBucketResponse createBucket(String bucketName) {
        return createBucket(new PutBucketRequest(bucketName));
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest request) {
        PutObjectResponse response = execute(request);
        response.setKey(request.getKey());
        return response;
    }

    @Override
    public PutObjectResponse putObject(String bucket, File file) {
        return putObject(new PutObjectRequest(bucket, file.getName(), file));
    }

    @Override
    public <O> GetObjectResponse<O> getObject(GetObjectRequest request) {
        return execute(request);
    }

    @Override
    public <O> GetObjectResponse<O> getObject(String bucket, String key, String rule) {
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        request.putCustomQueryParameter(rule, null);
        return getObject(request);
    }

    @Override
    public <O> GetObjectResponse<O> getObject(String bucket, String key) {
        return getObject(new GetObjectRequest(bucket, key));
    }

    @Override
    public DownloadObjectResponse downloadObject(String bucket, String key, String path) {
        return downloadObject(new DownloadObjectRequest(bucket, key, path));
    }

    @Override
    public DownloadObjectResponse downloadObject(String bucket, String key, String path, String rule) {
        return downloadObject(new DownloadObjectRequest(bucket, key, path, rule));
    }

    @Override
    public DownloadObjectResponse downloadObject(DownloadObjectRequest request) {

        GetObjectResponse<?> result = Objects.isNull(request.getRule()) ?
                                        getObject(request.getBucket(), request.getKey()) :
                                        getObject(request.getBucket(), request.getKey(), request.getRule());

        File localFile = Paths.get(request.getDownloadPath(), request.getKey().split("/")).toFile();

        if (!localFile.getParentFile().exists()) {
            try {
                Files.createDirectories(localFile.getParentFile().toPath());
            } catch (IOException e) {
                throw new OssBaseException(e);
            }
        }

        long size = 0;
        try (BufferedInputStream bis = new BufferedInputStream(result.getObjectContent(), BUFFER_SIZE);
                BufferedOutputStream fos = new BufferedOutputStream(
                    Files.newOutputStream(Paths.get(request.getDownloadPath(), request.getKey())), BUFFER_SIZE)) {
            int len;
            byte[] readBuf = new byte[BUFFER_SIZE];
            while ((len = bis.read(readBuf)) != -1) {
                fos.write(readBuf, 0, len);
                size += len;
            }
        } catch (Exception e) {
            throw new OssBaseException(e);
        }

        return new DownloadObjectResponse(request.getBucket(),
                                            request.getKey(),
                                            localFile.getAbsolutePath(),
                                            size);
    }

    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest request) {
        execute(request, Void.class);
        return new DeleteObjectResponse(request.getBucket(), request.getKey());
    }

    @Override
    public DeleteObjectResponse deleteObject(String bucket, String key) {
        return deleteObject(new DeleteObjectRequest(bucket, key));
    }

    @Override
    public ListObjectsResponse listObjects(ListObjectsRequest request) {
        request.setMaxKeys(MAX_KEYS);
        return execute(request);
    }

    @Override
    public ListAllObjectsResponse listAllObjects(ListAllObjectRequest request) {
        List<ObjectSummary> objectSummaryList = new ArrayList<>();

        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest(request.getBucket(), request.getPrefix());

        while (objectSummaryList.addAll(listObjects(listObjectsRequest).getObjectSummaries()))
            listObjectsRequest
                    .setStartAfter(objectSummaryList.get(objectSummaryList.size() - 1).getKey());

        ListAllObjectsResponse response = new ListAllObjectsResponse();
        response.setObjectSummaryList(objectSummaryList);

        return response;
    }

    @Override
    public ListAllObjectsResponse listAllObjects(String bucket, String prefix) {
        return listAllObjects(new ListAllObjectRequest(bucket, prefix));
    }

    @Override
    public ListAllObjectsResponse listAllObjects(String bucket) {
        return listAllObjects(new ListAllObjectRequest(bucket));
    }

    @Override
    public BatchOperationResponse batchUpload(BatchUploadRequest request) {
        final List<PutObjectRequest> requests;
        try (Stream<Path> pathStream = Files.walk(Paths.get(request.getLocalPath()))) {
            requests = pathStream
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(file ->
                            new PutObjectRequest(request.getBucket(),
                                    file.getPath().replace(request.getLocalPath(), ""), file))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new OssBaseException(e);
        }

        BatchOperationResponse response = new BatchUploadResponse();

        return batchProcess(response,
                requests,
                PutObjectRequest::getKey,
                putObjectRequest -> () -> putObject(putObjectRequest));
    }

    @Override
    public BatchOperationResponse batchUpload(String bucket, String path) {
        return batchUpload(new BatchUploadRequest(bucket, path));
    }

    @Override
    public BatchOperationResponse batchDownload(BatchDownloadRequest request) {
        BatchOperationResponse response = new BatchDownloadResponse();

        return batchProcess(response,
                listAllObjects(request.getBucket()).getObjectSummaryList(),
                ObjectSummary::getKey,
                objectSummary -> () ->
                    downloadObject(
                        objectSummary.getBucket(),
                        objectSummary.getKey(),
                        request.getDownloadPath(),
                        request.getRule()));
    }

    @Override
    public BatchOperationResponse batchDownload(String bucket, String path, String prefix) {
        return batchDownload(new BatchDownloadRequest(bucket, prefix, path));
    }

    @Override
    public BatchOperationResponse batchDownload(String bucket, String path, String prefix, String rule) {
        return batchDownload(new BatchDownloadRequest(bucket, prefix, path, rule));
    }

    @Override
    public BatchOperationResponse batchDelete(BatchDeleteRequest request) {
        BatchOperationResponse response = new BatchDeleteResponse();

        return batchProcess(response,
                listAllObjects(request.getBucket(), request.getPrefix()).getObjectSummaryList(),
                ObjectSummary::getKey,
                objectSummary -> () -> deleteObject(objectSummary.getBucket(),
                        objectSummary.getKey()));
    }

    @Override
    public BatchOperationResponse batchDelete(String bucket, String prefix) {
        return batchDelete(new BatchDeleteRequest(bucket, prefix));
    }

    @Override
    public OssConfiguration getCurrentConfiguration() {
        return ossConfiguration;
    }

    @Override
    public T createClient(OssConfiguration configuration) {
        throw new UnsupportedOssOperationException();
    }

    /**
     * 批量任务执行函数
     * 将需要处理的元素集合转换为 元素类型 E 和 {@link CompletableFuture<V>} 键值对的 {@link Map} 对象
     * @param collection 需要处理的元素集合
     * @param eleToKeyFunc 将集合中的元素转换为 map 的 key 的函数
     * @param eleToValSupFunc 将集合中的元素转换为 map 中的 value {@link Supplier} 的函数
     * @param response {@link BatchOperationResponse}
     * @return 组装好返回结果的 {@link BatchOperationResponse}
     * @param <E> 集合元素类型
     * @param <V> 异步执行的结果
     */
    protected <E, V, R extends BatchOperationResponse> R batchProcess(final R response,
                                                                      final Collection<E> collection,
                                                                      final Function<E, String> eleToKeyFunc,
                                                                      final Function<E, Supplier<V>> eleToValSupFunc)
    {
        response.setBatchSize(collection.size());

        Map<String, CompletableFuture<V>> futureMap =
            collection.stream()
                .collect(
                    Collectors.toMap(
                        eleToKeyFunc,
                        e -> supplyAsync(eleToValSupFunc.apply(e))));

        futureMap.forEach(
            (k, future) -> {
                try {
                    future.get();
                    response.addSuccessResult(k);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    response.addErrorResult(k, e.getMessage());
                } catch (InterruptedException e) {
                    response.addErrorResult(k, e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        );

        return response;
    }

    protected <R extends CliRequest, E extends CliResponse> E execute(R req) {
        return execute(req, null);
    }

    /**
     * 调用 oss 接口的通用处理框架
     * @param req cli 项目 oss 服务源请求，继承 {@link CliRequest}
     * @return cli 项目 oss 服务响应，继承 {@link CliResponse}
     * @param <R> 源请求类型
     * @param <I> oss client 请求类型，经过 {@link RequestTransformers} 转换后的结果
     * @param <O> 执行 oss client 响应方法后的响应类型
     * @param <E> 通过 {@link ResponseTransformers} 将响应类型转换为 cli 服务的响应类型
     * @param clientResponseType 对象存储请求的返回对象类型
     */
    @SuppressWarnings("unchecked")
    protected <R extends CliRequest, I, O, E extends CliResponse> E execute(R req, Class<?> clientResponseType) {
        I request = RequestTransformers.doTransform(req, req.getClass(), this.ossConfiguration.getType());
        O response;
        Method m = Arrays.stream(client.getClass().getMethods())
                    .filter(method -> clientResponseType == null ||
                            clientResponseType.equals(Void.class) ||
                            method.getReturnType().equals(clientResponseType))
                    .filter(method -> method.getParameterTypes().length == 1 &&
                                        method.getParameterTypes()[0].equals(request.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new OssBaseException("没有匹配请求类型的方法 : " + request.getClass()));
        try {
            // 针对没有返回值的情况直接返回 null
            if (clientResponseType == Void.class) {
                m.invoke(client, request);
                return null;
            }
            response = (O) m.invoke(client, request);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new OssBaseException(e);
        }
        return ResponseTransformers.doTransform(response, m.getGenericReturnType());
    }

}
