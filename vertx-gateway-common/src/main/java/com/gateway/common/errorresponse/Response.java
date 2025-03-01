package com.gateway.common.errorresponse;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：
 *
 * @author huxuehao
 **/
@Getter
@Setter
public abstract class Response {
    int code;
    String msg;

    public String toJsonString() {
        return JSON.toJSONString(this);
    };
}
