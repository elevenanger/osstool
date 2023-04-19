package cn.anger.ossservice.services;

import cn.anger.ossservice.services.model.OssConfiguration;

/**
 * @author : anger
 * OSS 客户端相关方法
 */
public interface Client<T> {
    T createClient(OssConfiguration configuration);
}
