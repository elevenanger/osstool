package cn.anger.ossservice.services.model;

import java.util.Date;

/**
 * @author : anger
 * 响应父类
 */
public class CliResponse {
    private final Date responseTime = new Date();

    public Date getResponseTime() {
        return responseTime;
    }

}
