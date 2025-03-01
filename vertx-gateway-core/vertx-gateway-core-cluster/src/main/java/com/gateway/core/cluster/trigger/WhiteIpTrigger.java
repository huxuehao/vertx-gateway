package com.gateway.core.cluster.trigger;

import com.gateway.common.constant.GatewayEventBusAddr;
import com.gateway.common.entity.GatewayWhiteList;
import com.gateway.white.service.GatewayWhiteListService;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：白名单触发器
 *
 * @author huxuehao
 **/
@Component
public class WhiteIpTrigger {
    private final Vertx vertx;
    private final GatewayWhiteListService gatewayWhiteListService;

    public WhiteIpTrigger(Vertx vertx, GatewayWhiteListService gatewayWhiteListService) {
        this.vertx = vertx;
        this.gatewayWhiteListService = gatewayWhiteListService;
    }

    /**
     * 上线
     * @param ids ID
     */
    public void online(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayWhiteList> whiteLists = gatewayWhiteListService.listByIds(ids);
        for (GatewayWhiteList whiteList : whiteLists) {
            vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_WHITE_TO_CACHE, whiteList);
        }
    }

    /**
     * 下线
     * @param whiteLists 白名单集合
     */
    public void offline(List<?> whiteLists) {
        if (whiteLists == null || whiteLists.isEmpty()) return;

        for (Object whiteList : whiteLists) {
            vertx.eventBus().request(GatewayEventBusAddr.DELETE_WHITE_TO_CACHE, whiteList);
        }
    }
}
