package com.hxh.gateway.core.app.creator;

import com.alibaba.fastjson2.JSON;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.option.GatewayAppOption;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.core.api.ApiRouterCreator;
import io.vertx.core.*;

/**
 * 描述：网关应用创建者
 *
 * @author huxuehao
 **/
public class ApplicationCreate extends AbstractVerticle {
    private ApplicationServerCreator applicationServerCreator;
    private ApiRouterCreator apiRouterCreator;
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        String appOptionStr = config().getString("appOption");
        if (Func.isEmpty(appOptionStr)) {
            startPromise.fail("网关应用配置为空");
        }
        // 获取应用配置
        GatewayAppOption appOption = JSON.parseObject(appOptionStr, GatewayAppOption.class);

        // 实例化服务创建者
        applicationServerCreator = new ApplicationServerCreator(vertx, appOption);
        // 尝试创建http服务
        Future<Object> httpFuture = Future.future(future -> {
            applicationServerCreator.createHttp(res -> {
                if (res.succeeded()) {
                    future.complete();
                } else {
                    future.fail(res.cause());
                }
            });
        });

        // 尝试创建https服务
        Future<Object> httpsFuture = Future.future(future -> {
            applicationServerCreator.createHttps(res -> {
                if (res.succeeded()) {
                    future.complete();
                } else {
                    future.fail(res.cause());
                }
            });
        });

        // 阻塞Future
        Future.all(httpFuture, httpsFuture).onComplete(res -> {
            if (res.failed()) {
                startPromise.fail(res.cause());
                return;
            }

            // 实例API路由创建者
            apiRouterCreator = new ApiRouterCreator(
                    applicationServerCreator.getAppOption(),
                    applicationServerCreator.getRouter(),
                    applicationServerCreator.getHttpClient());
            Long appId = applicationServerCreator.getAppOption().getId();
            vertx.eventBus().consumer(appId + GatewayEventBusAddr.ADD_API_SUF, apiRouterCreator::addRoute);
            vertx.eventBus().consumer(appId + GatewayEventBusAddr.UPDATE_API_SUF, apiRouterCreator::updateRoute);
            vertx.eventBus().consumer(appId + GatewayEventBusAddr.DELETE_API_SUF, apiRouterCreator::deleteRoute);

            startPromise.complete();
        });
    }
}
