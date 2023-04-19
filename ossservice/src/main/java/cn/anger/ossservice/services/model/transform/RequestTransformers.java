package cn.anger.ossservice.services.model.transform;

import cn.anger.ossservice.exception.OssBaseException;
import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.model.*;
import cn.anger.utils.reflection.ReflectionUtil;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.qcloud.cos.internal.CosServiceRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : anger
 * 请求转换器
 */
public abstract class RequestTransformers {

    private RequestTransformers() {}

    private static final Map<String, Field> transformerMap = getTransformerMap();

    private static Map<String, Field> getTransformerMap() {
        return Arrays.stream(RequestTransformers.class.getFields())
                .collect(Collectors.toMap(
                        field -> {
                            List<String> parameterTypes = ReflectionUtil.genericTypes(field);
                            Oss.Type ossType = Arrays.stream(Oss.Type.values())
                                    .filter(type -> parameterTypes.get(1).contains(type.getModelPath()))
                                    .findFirst().orElseThrow(() -> new OssBaseException("不支持的OSS类型"));
                            return ossType.name().concat(parameterTypes.get(0));
                        }, Function.identity()));
    }

    public static final RequestTransformer<ListBucketsRequest, com.amazonaws.services.s3.model.ListBucketsRequest>
        seqAwsListBucketRequestTransformer =
            request -> new com.amazonaws.services.s3.model.ListBucketsRequest();

    public static final RequestTransformer<PutBucketRequest, CreateBucketRequest>
            seqAwsCreateBucketRequestTransformer =
            request -> new CreateBucketRequest(request.getBucketName());

    /**
     * 巨杉 AWS list Object 请求转换器
     */
    public static final RequestTransformer<ListObjectsRequest, ListObjectsV2Request>
        seqAwsListObjectRequestTransformer =
        originRequest -> {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(originRequest.getBucketName());
            listObjectsV2Request.setPrefix(originRequest.getPrefix());
            listObjectsV2Request.setStartAfter(originRequest.getStartAfter());
            listObjectsV2Request.setMaxKeys(originRequest.getMaxKeys());
            return listObjectsV2Request;
        };

    public static final RequestTransformer<PutObjectRequest, com.amazonaws.services.s3.model.PutObjectRequest>
        seqAwsPutObjectRequestTransformer =
        originRequest -> {
            if (originRequest.getInputStream() == null) {
                return new com.amazonaws.services.s3.model.PutObjectRequest(
                            originRequest.getBucketName(),
                            originRequest.getKey(),
                            originRequest.getFile());
            } else {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(originRequest.getFile().length());
                return new com.amazonaws.services.s3.model.PutObjectRequest(
                            originRequest.getBucketName(),
                            originRequest.getKey(),
                            originRequest.getInputStream(),
                            objectMetadata
                );
            }
        };

    public static final RequestTransformer<GetObjectRequest, com.amazonaws.services.s3.model.GetObjectRequest>
        seqAwsGetObjectRequestTransformer =
        originRequest ->
            originRequest.getVersionId() == null ?
                new com.amazonaws.services.s3.model.GetObjectRequest(
                    originRequest.getBucketName(),
                    originRequest.getKey()) :
                new com.amazonaws.services.s3.model.GetObjectRequest(
                    originRequest.getBucketName(),
                    originRequest.getKey(),
                    originRequest.getVersionId());

    public static final RequestTransformer<DeleteObjectRequest, com.amazonaws.services.s3.model.DeleteObjectRequest>
        seqAwsObjectDeleteRequestTransformer =
        originRequest ->
            new com.amazonaws.services.s3.model.DeleteObjectRequest(
                originRequest.getBucket(),
                originRequest.getKey());

    public static final RequestTransformer<DeleteBucketRequest, com.amazonaws.services.s3.model.DeleteBucketRequest>
        awsDeleteBucketRequestTransformer =
        deleteBucketRequest ->
            new com.amazonaws.services.s3.model.DeleteBucketRequest(deleteBucketRequest.getBucket());

    public static final RequestTransformer<ListBucketsRequest, com.qcloud.cos.model.ListBucketsRequest>
        cosListBucketRequestTransformer =
            listBucketsRequest ->
                    new com.qcloud.cos.model.ListBucketsRequest();

    public static final RequestTransformer<PutBucketRequest, com.qcloud.cos.model.CreateBucketRequest>
        cosPutBucketRequestTransformer =
            putBucketRequest ->
                new com.qcloud.cos.model.CreateBucketRequest(putBucketRequest.getBucketName());

    public static final RequestTransformer<ListObjectsRequest, com.qcloud.cos.model.ListObjectsRequest>
        cosListObjectRequestTransformer =
        originRequest -> {
            com.qcloud.cos.model.ListObjectsRequest request = new com.qcloud.cos.model.ListObjectsRequest();
            request.setBucketName(originRequest.getBucketName());
            request.setPrefix(originRequest.getPrefix());
            request.setMarker(originRequest.getStartAfter());
            request.setMaxKeys(originRequest.getMaxKeys());
            return request;
        };
    public static final RequestTransformer<PutObjectRequest, com.qcloud.cos.model.PutObjectRequest>
        cosPutObjectRequestTransformer =
            putObjectRequest -> new com.qcloud.cos.model.PutObjectRequest(
                    putObjectRequest.getBucketName(),
                    putObjectRequest.getKey(),
                    putObjectRequest.getFile());

    public static final RequestTransformer<GetObjectRequest, com.qcloud.cos.model.GetObjectRequest>
        cosGetObjectRequestTransformer =
            getObjectRequest -> {
                com.qcloud.cos.model.GetObjectRequest request = new com.qcloud.cos.model.GetObjectRequest(
                        getObjectRequest.getBucketName(),
                        getObjectRequest.getKey());
                copyCustomerQueryParametersToCOS(getObjectRequest, request);
                return request;};

    public static final RequestTransformer<DeleteObjectRequest, com.qcloud.cos.model.DeleteObjectRequest>
        cosDeleteObjectRequestTransformer =
            deleteObjectRequest -> new com.qcloud.cos.model.DeleteObjectRequest(
                    deleteObjectRequest.getBucket(),
                    deleteObjectRequest.getKey());

    @SuppressWarnings("unchecked")
    public static <T extends CliRequest, R> R doTransform(T t, Type requestType, Oss.Type ossType) {
        Field transformer = transformerMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(ossType.name().concat(requestType.getTypeName())))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(
                    () -> new OssBaseException("没有匹配的 RequestTransformer : ".concat(requestType.getTypeName())));

        RequestTransformer<T, R> requestTransformer;
        try {
            requestTransformer =
                (RequestTransformer<T, R>) transformer.get(RequestTransformers.class);
        } catch (IllegalAccessException e) {
            throw new OssBaseException(e);
        }
        return requestTransformer.transform(t);
    }

    private static void copyCustomerQueryParametersToCOS(CliRequest request, CosServiceRequest cosServiceRequest) {
        request.getCustomQueryParameters()
            .forEach((key, value) ->
                        value.forEach(v ->
                            cosServiceRequest.putCustomQueryParameter(key, v)));
    }

}