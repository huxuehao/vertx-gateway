package com.gateway.common.errorresponse;

/**
 * 描述：
 *
 * @author huxuehao
 **/
public class CommErrorResponse extends Response {
    public CommErrorResponse(String msg) {
        setCode(500);
        setMsg(msg);
    }

    public CommErrorResponse(int code) {
        setCode(code);
        setMsg("未知异常");
    }
    public CommErrorResponse(int code, String msg) {
        setCode(code);
        setMsg(msg);
    }
}
