package com.hxh.gateway.core.app;

import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.entity.GatewayWhiteList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 描述：访问白名单上线部署
 *
 * @author huxuehao
 **/
public class WhiteListOnlineService extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(WhiteListOnlineService.class);

    /**
     * 缓存
     * key   APP ID
     * value 请求白名单Host集合
     */
    private static final Map<Long, Set<String>> WHITE_LIST_CACHE = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer(GatewayEventBusAddr.INSERT_UPDATE_WHITE_TO_CACHE, this::insertUpdateWhiteList);
        vertx.eventBus().consumer(GatewayEventBusAddr.DELETE_WHITE_TO_CACHE, this::deleteWhiteList);

        logger.info("==> 完成 ClientOnlineService 启动~");
        startPromise.complete();
    }

    /**
     * 插入更新
     */
    void insertUpdateWhiteList(Message<GatewayWhiteList> msg) {
        GatewayWhiteList whiteList = msg.body();
        try {
            WHITE_LIST_CACHE.computeIfAbsent(whiteList.parentId, k -> new HashSet<>());
            Set<String> hosts = WHITE_LIST_CACHE.get(whiteList.parentId);
            hosts.add(whiteList.getIp());
            WHITE_LIST_CACHE.put(whiteList.parentId, hosts);

            logger.info("白名单 ["+msg.body().getIp()+"] 已部署~");
            msg.reply(1);
        } catch (Exception e) {
            msg.fail(500, e.getMessage());
        }
    }

    /**
     * 获取
     */
    public static Set<String> obtainWhiteList(Long appId) {
        return WHITE_LIST_CACHE.get(appId);
    }

    /**
     * 删除
     */
    void deleteWhiteList(Message<GatewayWhiteList> msg) {
        GatewayWhiteList whiteList = msg.body();
        Set<String> hosts = WHITE_LIST_CACHE.get(whiteList.parentId);
        if (hosts != null) {
            hosts.remove(whiteList.getIp());
        }

        logger.info("白名单 ["+msg.body().getIp()+"] 已卸载~");
        msg.reply(1);
    }
}
