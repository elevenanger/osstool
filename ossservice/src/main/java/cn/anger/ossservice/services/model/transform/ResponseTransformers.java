package cn.anger.ossservice.services.model.transform;

import cn.anger.ossservice.exception.OssBaseException;
import cn.anger.ossservice.services.Oss;
import cn.anger.ossservice.services.model.*;
import cn.anger.utils.reflection.ReflectionUtil;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectListing;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : anger
 * response 转换器
 */
public class ResponseTransformers {
    private ResponseTransformers() {}

    private static final Map<String, Field> transformerMap = getTransformerMap();

    private static Map<String, Field> getTransformerMap() {
        return Arrays.stream(ResponseTransformers.class.getFields())
                .collect(Collectors.toMap(
                        field -> {
                            List<String> parameterTypes = ReflectionUtil.genericTypes(field);
                            return parameterTypes.get(0);
                        }, Function.identity()));
    }

    public static final ResponseTransformer<List<Bucket>, ListBucketsResponse>
        seqAwsListBucketsResponseTransformer =
            buckets -> {
                List<cn.anger.ossservice.services.model.Bucket> bs =
                    buckets.stream()
                        .map(bucket -> {
                            cn.anger.ossservice.services.model.Bucket b = new cn.anger.ossservice.services.model.Bucket();
                            b.setType(Oss.Type.AWS);
                            b.setName(bucket.getName());
                            b.setCreateDate(bucket.getCreationDate());
                            return b;})
                        .collect(Collectors.toList());
                return new ListBucketsResponse(bs);
            };

    public static final ResponseTransformer<S3Object, GetObjectResponse<S3Object>>
        awsGetObjectResponseTransformer =
        s3Object -> {
            GetObjectResponse<S3Object> response = new GetObjectResponse<>();
            response.setOssObject(s3Object);
            response.setObjectContent(s3Object.getObjectContent());
            return response;
        };

    /**
     * 创建桶请求转换器
     */
    public static final ResponseTransformer<Bucket, PutBucketResponse>
        seqAwsCreateBucketResponseTransformer =
        bucket -> {
            cn.anger.ossservice.services.model.Bucket b = new cn.anger.ossservice.services.model.Bucket();
            b.setName(bucket.getName());
            b.setCreateDate(bucket.getCreationDate());
            return new PutBucketResponse(b);
        };

    public static final ResponseTransformer<ListObjectsV2Result, ListObjectsResponse>
        seqAwsListObjectResponseTransformer =
        listObjectsV2Result -> {
            ListObjectsResponse response = new ListObjectsResponse();

            List<ObjectSummary> objectSummaries =
                listObjectsV2Result.getObjectSummaries().stream()
                    .map(objectSummary -> {
                        ObjectSummary summary = new ObjectSummary();
                        summary.setBucket(objectSummary.getBucketName());
                        summary.setETag(objectSummary.getETag());
                        summary.setKey(objectSummary.getKey());
                        summary.setSize(objectSummary.getSize());
                        summary.setLastModified(objectSummary.getLastModified());
                        return summary;
                    }).collect(Collectors.toList());

            response.setObjectSummaries(objectSummaries);
            response.setStartAfter(listObjectsV2Result.getStartAfter());
            response.setMaxKey(listObjectsV2Result.getMaxKeys());
            response.setCount(objectSummaries.size());

            return response;
        };

    public static final ResponseTransformer<PutObjectResult, PutObjectResponse>
        seqAwsPutObjectResponseTransformer =
        putObjectResult -> {
            PutObjectResponse response = new PutObjectResponse();
            response.setETag(putObjectResult.getETag());
            return response;
        };

    public static final ResponseTransformer<List<com.qcloud.cos.model.Bucket>, ListBucketsResponse>
        cosListBucketResponseTransformer =
        buckets -> {
            List<cn.anger.ossservice.services.model.Bucket> bs =
                    buckets.stream()
                            .map(bucket -> {
                                cn.anger.ossservice.services.model.Bucket b = new cn.anger.ossservice.services.model.Bucket();
                                b.setType(Oss.Type.COS);
                                b.setName(bucket.getName());
                                b.setCreateDate(bucket.getCreationDate());
                                return b;})
                            .collect(Collectors.toList());
            return new ListBucketsResponse(bs);
        };

    public static final ResponseTransformer<com.qcloud.cos.model.Bucket, PutBucketResponse>
        cosPutBucketResponseTransformer =
            bucket -> {
                cn.anger.ossservice.services.model.Bucket b = new cn.anger.ossservice.services.model.Bucket();
                b.setName(bucket.getName());
                b.setType(Oss.Type.COS);
                b.setCreateDate(bucket.getCreationDate());
                return new PutBucketResponse(b);
            };

    public static final ResponseTransformer<ObjectListing, ListObjectsResponse>
        cosListObjectResponseTransformer =
        objectListing -> {
            ListObjectsResponse response = new ListObjectsResponse();

            List<ObjectSummary> objectSummaries =
                objectListing.getObjectSummaries().stream()
                    .map(objectSummary -> {
                        ObjectSummary summary = new ObjectSummary();
                        summary.setBucket(objectSummary.getBucketName());
                        summary.setETag(objectSummary.getETag());
                        summary.setKey(objectSummary.getKey());
                        summary.setSize(objectSummary.getSize());
                        summary.setLastModified(objectSummary.getLastModified());
                        return summary;
                    }).collect(Collectors.toList());

            response.setObjectSummaries(objectSummaries);
            response.setStartAfter(objectListing.getNextMarker());
            response.setMaxKey(objectListing.getMaxKeys());
            response.setCount(objectSummaries.size());

            return response;
        };

    public static final ResponseTransformer<com.qcloud.cos.model.PutObjectResult, PutObjectResponse>
        cosPutObjectResponseTransformer =
        putObjectResult -> {
            PutObjectResponse response = new PutObjectResponse();
            response.setETag(putObjectResult.getETag());
            return response;
        };

    public static final ResponseTransformer<COSObject, GetObjectResponse<COSObject>>
        cosGetObjectResponseTransformer =
        cosObject -> {
            GetObjectResponse<COSObject> response = new GetObjectResponse<>();
            response.setOssObject(cosObject);
            response.setObjectContent(cosObject.getObjectContent());
            return response;
        };

    public static <T, R extends CliResponse> R doTransform(T t, Type type) {
        Optional<R> response = Optional.empty();
        try {
            @SuppressWarnings("unchecked")
            ResponseTransformer<T, R> responseTransformer =
                (ResponseTransformer<T, R>) transformerMap.get(type.getTypeName()).get(ResponseTransformer.class);
            response = Optional.ofNullable(responseTransformer.transform(t));
        } catch (IllegalAccessException e) {
            throw new OssBaseException(e);
        }
        return response.orElseThrow(() -> new OssBaseException("无法转换的响应 : " + t.getClass()));
    }

}
