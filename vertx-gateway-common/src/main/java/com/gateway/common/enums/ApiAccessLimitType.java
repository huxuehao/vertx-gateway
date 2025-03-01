package com.gateway.common.enums;

/**
 * 描述：访问限制类型
 *
 * @author huxuehao
 **/
public enum ApiAccessLimitType {
    DAY(86400),
    HOUR(3600),
    MINUTE(60),
    NONE(0);
    private final long seconds;

    ApiAccessLimitType(long seconds) {
        this.seconds = seconds;
    }

    public long getSeconds() {
        return seconds;
    }
}
