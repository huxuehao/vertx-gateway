package com.gateway.core.cluster.trigger;

import com.alibaba.fastjson2.JSON;
import com.gateway.application.service.GatewayApplicationService;
import com.gateway.common.constant.GatewayEventBusAddr;
import com.gateway.common.entity.GatewayApplication;
import com.gateway.common.option.AppConfig;
import com.gateway.common.option.GatewayAppOption;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：网管应用触发器
 *
 * @author huxuehao
 **/
@Component
public class ApplicationTrigger {
    private final Vertx vertx;
    private final GatewayApplicationService gatewayApplicationService;

    public ApplicationTrigger(Vertx vertx, GatewayApplicationService gatewayApplicationService) {
        this.vertx = vertx;
        this.gatewayApplicationService = gatewayApplicationService;
    }

    /**
     * 部署
     * @param ids ID
     */
    public void online(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayApplication> gatewayApplications = gatewayApplicationService.listByIds(ids);
        for (GatewayApplication application : gatewayApplications) {
            GatewayAppOption appOption = new GatewayAppOption(application.getId(), JSON.parseObject(application.getConfig(), AppConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.ONLINE_APP, appOption);
        }
    }

    /**
     * 卸载
     * @param ids ID
     */
    public void offline(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayApplication> gatewayApplications = gatewayApplicationService.listByIds(ids);
        for (GatewayApplication application : gatewayApplications) {
            GatewayAppOption appOption = new GatewayAppOption(application.getId(), JSON.parseObject(application.getConfig(), AppConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.OFFLINE_APP, appOption);
        }
    }
}
