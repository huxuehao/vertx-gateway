package com.gateway.common.errorresponse;

/**
 * 描述：
 *
 * @author huxuehao
 **/
public class NotFoundResponse extends Response {
    public NotFoundResponse() {
        setCode(404);
        setMsg("未找到对应的路由");
    }
}
