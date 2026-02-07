package com.hxh.gateway.core.api;

import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.dto.GatewayClientDto;
import com.hxh.gateway.common.util.Func;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：客户端上线部署
 *
 * @author huxuehao
 **/
public class ClientOnlineService extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(ClientOnlineService.class);

    /**
     * 缓存
     * key   客户端编号
     * value 客户端DTO
     */
    private static final Map<String, GatewayClientDto> CLIENT_CACHE = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        vertx.eventBus().consumer(GatewayEventBusAddr.INSERT_UPDATE_CLIENT_TO_CACHE, this::insertUpdateClient);
        vertx.eventBus().consumer(GatewayEventBusAddr.DELETE_CLIENT_TO_CACHE, this::deleteClient);

        logger.info("==> 完成 ClientOnlineService 启动~");
        startPromise.complete();
    }

    /**
     * 插入更新
     */
    void insertUpdateClient(Message<GatewayClientDto> msg) {
        GatewayClientDto clientDto = msg.body();
        try {
            CLIENT_CACHE.put(clientDto.getClientCode(), clientDto);

            logger.info("客户端 ["+msg.body().getClientCode()+"] 已部署~");
            msg.reply(1);
        } catch (Exception e) {
            msg.fail(500, e.getMessage());
        }
    }

    /**
     * 获取
     */
    public static GatewayClientDto obtainClient(String code) {
        return CLIENT_CACHE.get(code);
    }

    /**
     * 删除
     */
    void deleteClient(Message<String> msg) {
        String codesStr = msg.body();
        if (Func.isEmpty(codesStr)) {
            msg.reply(1);
        }
        String[] codes = codesStr.split(",");
        for (String code : codes) {
            CLIENT_CACHE.remove(code);
        }

        logger.info("客户端 ["+codesStr+"] 已卸载~");
        msg.reply(1);
    }
}
