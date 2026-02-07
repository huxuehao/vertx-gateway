package com.hxh.gateway.core.cluster.trigger;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxh.gateway.client.service.GatewayClientAuthService;
import com.hxh.gateway.client.service.GatewayClientService;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.dto.GatewayClientDto;
import com.hxh.gateway.common.entity.GatewayClient;
import com.hxh.gateway.common.entity.GatewayClientAuth;
import io.vertx.core.Vertx;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：客户端触发器
 *
 * @author huxuehao
 **/
@Component
public class ClientTrigger {
    private final Vertx vertx;
    private final GatewayClientService gatewayClientService;
    private final GatewayClientAuthService gatewayClientAuthService;

    public ClientTrigger(Vertx vertx, GatewayClientService gatewayClientService, GatewayClientAuthService gatewayClientAuthService) {
        this.vertx = vertx;
        this.gatewayClientService = gatewayClientService;
        this.gatewayClientAuthService = gatewayClientAuthService;
    }

    /**
     * 上线
     * @param ids ID
     */
    public void online(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<GatewayClient> gatewayClients = gatewayClientService.listByIds(ids);
        for (GatewayClient client : gatewayClients) {
            GatewayClientDto clientDto = new GatewayClientDto();
            BeanUtils.copyProperties(client, clientDto);
            List<GatewayClientAuth> auths = gatewayClientAuthService.list(new QueryWrapper<GatewayClientAuth>().eq("client_id", client.getId()));
            clientDto.setAuths(auths);
            vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_CLIENT_TO_CACHE, clientDto);
        }
    }

    /**
     * 下线
     * @param codesStr 客户端编号字符串集合
     */
    public void offline(String codesStr) {
        if (codesStr == null || codesStr.length() == 0) return;

        vertx.eventBus().request(GatewayEventBusAddr.DELETE_CLIENT_TO_CACHE, codesStr);
    }
}
