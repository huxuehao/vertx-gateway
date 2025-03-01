package com.gateway.core.cluster.trigger;

import com.alibaba.fastjson2.JSON;
import com.gateway.api.service.GatewayApiService;
import com.gateway.common.constant.GatewayEventBusAddr;
import com.gateway.common.entity.GatewayApi;
import com.gateway.common.option.ApiConfig;
import com.gateway.common.option.GatewayApiOption;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：网关接口触发器
 *
 * @author huxuehao
 **/
@Component
public class ApiTrigger {
    private final Vertx vertx;
    private final GatewayApiService gatewayApiService;

    public ApiTrigger(Vertx vertx, GatewayApiService gatewayApiService) {
        this.vertx = vertx;
        this.gatewayApiService = gatewayApiService;
    }

    /**
     * 上线
     * @param ids ID
     */
    public void online(List<Long> ids) {
        List<GatewayApi> gatewayApis = gatewayApiService.listByIds(ids);
        for (GatewayApi api : gatewayApis) {
            GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.ONLINE_API, apiOption);
        }
    }

    /**
     * 下线
     * @param ids ID
     */
    public void offline(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayApi> gatewayApis = gatewayApiService.listByIds(ids);
        for (GatewayApi api : gatewayApis) {
            GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.OFFLINE_API, apiOption);
        }
    }

    /**
     * 更新
     * @param ids ID
     */
    public void reset(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayApi> gatewayApis = gatewayApiService.listByIds(ids);
        for (GatewayApi api : gatewayApis) {
            GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
            vertx.eventBus().request(GatewayEventBusAddr.RESET_API, apiOption);
        }
    }
}
