package com.hxh.gateway.core.api;

import com.hxh.gateway.common.annotation.RouteHandlerDefaultField;
import com.hxh.gateway.common.constant.GatewayRouteConst;
import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.common.errorresponse.CommErrorResponse;
import com.hxh.gateway.common.option.GatewayApiOption;
import com.hxh.gateway.core.log.ConcurrentLogProducer;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.RoutingContext;
import org.springframework.http.MediaType;

/**
 * 描述：路由处理器接口
 *
 * @author huxuehao
 **/
public abstract class RouteHandler implements Handler<RoutingContext> {
    @RouteHandlerDefaultField
    public GatewayApiOption apiOption = null;
    @RouteHandlerDefaultField
    public HttpClient httpClient = null;

    protected void endRoutingOk(RoutingContext rct, String msg) {
        endRouting(rct, 200, msg);
    }

    protected void endRoutingError(RoutingContext rct, String msg) {
        endRouting(rct, 500, msg);
    }
    protected void endRoutingFieldNull(RoutingContext rct) {
        endRouting(rct, 500, "RouteHandler 中的 GatewayApiOption 或 HttpClient 为完成初始化");
    }

    protected void endRouting(RoutingContext rct, int code, String msg) {
        rct.response()
                .setStatusCode(code)
                .putHeader("Server", "Vertx-Gateway")
                .putHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .end(new CommErrorResponse(code, msg).toJsonString());
        // 生成日志
        GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
        log.setStatus(0);
        log.setErrorContent(msg);
        ConcurrentLogProducer.pushLog(log);
    }

    protected boolean checkFieldIsNull() {
        return apiOption == null || httpClient == null;
    }
}
