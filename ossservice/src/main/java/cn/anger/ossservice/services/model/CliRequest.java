package cn.anger.ossservice.services.model;

import cn.anger.ossservice.services.Oss;

import java.util.*;

/**
 * @author : anger
 * 客户端请求父类
 */
public class CliRequest {

    private final Date requestTime = new Date();

    private Oss.Type type;

    private Map<String, String> customRequestHeaders;

    private Map<String, List<String>> customQueryParameters;

    public Map<String, String> getCustomRequestHeaders() {
        if (customRequestHeaders == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(customRequestHeaders);
    }

    public String putCustomRequestHeader(String name, String value) {
        if (customRequestHeaders == null) {
            customRequestHeaders = new HashMap<String, String>();
        }
        if (name == null) {
            throw new IllegalArgumentException("custom header key can't be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("custom header value can't be null");
        }
        return customRequestHeaders.put(name, value);
    }

    public Map<String, List<String>> getCustomQueryParameters() {
        if (customQueryParameters == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(customQueryParameters);
    }

    public void putCustomQueryParameter(String name, String value) {
        if (customQueryParameters == null) {
            customQueryParameters = new HashMap<String, List<String>>();
        }
        List<String> paramList =
                customQueryParameters.computeIfAbsent(name, k -> new LinkedList<String>());
        paramList.add(value);
    }

    public Oss.Type getOssType() {
        return type;
    }

    public void setOssType(Oss.Type type) {
        this.type = type;
    }

    public Date getRequestTime() {
        return requestTime;
    }

}
