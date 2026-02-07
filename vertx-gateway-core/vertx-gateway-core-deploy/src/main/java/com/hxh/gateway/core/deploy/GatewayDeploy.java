package com.hxh.gateway.core.deploy;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxh.gateway.api.service.GatewayApiService;
import com.hxh.gateway.application.service.GatewayApplicationService;
import com.hxh.gateway.client.service.GatewayClientService;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.dto.GatewayClientDto;
import com.hxh.gateway.common.entity.GatewayApi;
import com.hxh.gateway.common.entity.GatewayApplication;
import com.hxh.gateway.common.entity.GatewayClient;
import com.hxh.gateway.common.entity.GatewayWhiteList;
import com.hxh.gateway.common.option.ApiConfig;
import com.hxh.gateway.common.option.AppConfig;
import com.hxh.gateway.common.option.GatewayApiOption;
import com.hxh.gateway.common.option.GatewayAppOption;
import com.hxh.gateway.core.api.ClientOnlineService;
import com.hxh.gateway.core.app.ApplicationOnlineService;
import com.hxh.gateway.core.app.WhiteListOnlineService;
import com.hxh.gateway.core.token.TokenOnlineServer;
import com.hxh.gateway.white.service.GatewayWhiteListService;
import io.vertx.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：Vertx-Gateway部署
 *
 * @author huxuehao
 **/
@Component
public class GatewayDeploy  implements SmartInitializingSingleton {
    private static final Logger logger = LogManager.getLogger(GatewayDeploy.class);
    private final Vertx vertx;
    private final GatewayClientService clientService;
    private final GatewayWhiteListService whiteListService;
    private final GatewayApplicationService applicationService;
    private final GatewayApiService apiService;

    public GatewayDeploy(Vertx vertx, GatewayClientService clientService, GatewayWhiteListService whiteListService, GatewayApplicationService applicationService, GatewayApiService apiService) {
        this.vertx = vertx;
        this.clientService = clientService;
        this.whiteListService = whiteListService;
        this.applicationService = applicationService;
        this.apiService = apiService;
    }

    public void deploy() {
        ArrayList<Future<?>> futures = new ArrayList<>();

        // Token服务
        futures.add(doDeploy(Vertx.vertx(), new TokenOnlineServer(), new DeploymentOptions()));
        // 部署网关上线服务
        futures.add(doDeploy(vertx, new ApplicationOnlineService(), new DeploymentOptions()));
        // 部署客户端服务
        futures.add(doDeploy(vertx, new ClientOnlineService(), new DeploymentOptions()));
        // 访问白名单
        futures.add(doDeploy(vertx, new WhiteListOnlineService(), new DeploymentOptions()));


        // 阻塞等待
        Future.all(futures).onComplete(res -> {
            if (res.failed()) {
                throw new RuntimeException(res.cause());
            } else {
                startClient();
                startWhiteList();
                startApplication();
            }
        });
    }

    private Future<?> doDeploy(Vertx vertx, AbstractVerticle verticle, DeploymentOptions options) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(verticle, options).onComplete(res -> {
            if (res.succeeded()) {
                promise.complete();
            } else {
                promise.fail(res.cause().toString());
            }
        });
        return promise.future();
    }

    /**
     * 启动客户端
     */
    private void startClient() {
        List<GatewayClient> list = clientService.list();
        for (GatewayClient gatewayClient : list) {
            if (gatewayClient.getOnline() == 0 || gatewayClient.getClientTtl().getTime() < System.currentTimeMillis()) {
                continue;
            }
            GatewayClientDto clientDto = clientService.getOneV2(gatewayClient.getId());
            vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_CLIENT_TO_CACHE, clientDto).onComplete(res -> {
                if (res.failed()) {
                    logger.error("客户端["+gatewayClient.getClientCode()+"]部署失败");
                }
            });
        }
    }

    /**
     * 启动IP白名单
     */
    private void startWhiteList() {
        List<GatewayWhiteList> list = whiteListService.list();
        for (GatewayWhiteList whiteList : list) {
            vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_WHITE_TO_CACHE, whiteList).onComplete(res -> {
                if (res.failed()) {
                    logger.error("白名单["+whiteList.getIp()+"]部署失败");
                }

            });
        }

    }

    /**
     * 启动应用
     */
    private void startApplication() {
        QueryWrapper<GatewayApplication> qw = new QueryWrapper<>();
        qw.eq("online", 1);
        qw.eq("del_flag", 0);
        List<GatewayApplication> list = applicationService.list(qw);
        for (GatewayApplication application : list) {
            GatewayAppOption appOption = new GatewayAppOption(application.getId(), JSON.parseObject(application.getConfig(), AppConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.ONLINE_APP, appOption).onComplete(res -> {
                if (res.failed()) {
                    logger.error("网关应用["+application.getName()+"]部署失败");
                }
                startApi(application.getId());
            });
        }
    }

    /**
     * 启动API
     */
    private void startApi(Long appId) {
        QueryWrapper<GatewayApi> qw = new QueryWrapper<>();
        qw.eq("app_id", appId);
        qw.eq("online", 1);
        qw.eq("del_flag", 0);
        List<GatewayApi> list = apiService.list(qw);
        for (GatewayApi gatewayApi : list) {
            GatewayApiOption apiOption = new GatewayApiOption(gatewayApi.getId(), gatewayApi.getAppId(), JSON.parseObject(gatewayApi.getConfig(), ApiConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.ONLINE_API, apiOption).onComplete(res -> {
                if (res.failed()) {
                    logger.error("网关API["+gatewayApi.getName()+"]上线失败");
                }
            });
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        deploy();
    }
}
