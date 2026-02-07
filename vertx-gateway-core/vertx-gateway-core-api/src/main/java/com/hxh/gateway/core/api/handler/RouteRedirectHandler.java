package com.hxh.gateway.core.api.handler;

import com.hxh.gateway.common.constant.GatewayRouteConst;
import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.common.option.ApiConfig;
import com.hxh.gateway.core.api.RouteHandler;
import com.hxh.gateway.core.log.ConcurrentLogProducer;
import io.vertx.ext.web.RoutingContext;

/**
 * 描述：客户端处理器
 *
 * @author huxuehao
 **/
public class RouteRedirectHandler extends RouteHandler {
    @Override
    public void handle(RoutingContext rct) {
        // 字段有效性检查
        if (checkFieldIsNull()) {
            endRoutingFieldNull(rct);
            return;
        }

        ApiConfig config = apiOption.getConfig();
        rct.response().putHeader("Location", config.getProxyRedirectUrl()).setStatusCode(302);
        if (!rct.response().ended()) {
            rct.response().end();
        }

        // 日志采集
        GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
        log.setProxyPath(config.getProxyRedirectUrl());
        log.setStatus(1);
        // 日志推送
        ConcurrentLogProducer.pushLog(log);

    }
}
