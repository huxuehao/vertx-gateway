package com.gateway.core.api.handler;

import com.gateway.common.constant.GatewayRouteConst;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.errorresponse.CommErrorResponse;
import com.gateway.common.util.MeUtil;
import com.gateway.core.api.RouteHandler;
import com.gateway.core.log.ConcurrentLogProducer;
import io.vertx.ext.web.RoutingContext;
import org.springframework.http.MediaType;

/**
 * 描述：路由异常处理器
 *
 * @author huxuehao
 **/
public class RouteExceptionHandler extends RouteHandler {
    @Override
    public void handle(RoutingContext rct) {
        rct.response()
                .putHeader("Server", "Vertx-Gateway")
                .putHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setStatusCode(500)
                .end(new CommErrorResponse(500, "未知错误").toJsonString());
        // 生成日志
        GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
        log.setErrorContent(MeUtil.catchThrowableStackInfo(rct.failure()));
        log.setStatus(0);
        ConcurrentLogProducer.pushLog(log);
    }
}
