package com.gateway.white.controller;

import com.gateway.common.annotation.MenuTag;
import com.gateway.common.config.GatewayEvent;
import com.gateway.common.config.Identity;
import com.gateway.common.config.RedisSubscribeBody;
import com.gateway.common.constant.ChannelConstant;
import com.gateway.common.constant.GatewayEventBusAddr;
import com.gateway.common.entity.GatewayWhiteList;
import com.gateway.common.mp.support.MP;
import com.gateway.common.r.R;
import com.gateway.white.service.GatewayWhiteListService;
import io.vertx.core.Vertx;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 描述：网关白名单
 *
 * @author huxuehao
 **/
@RestController
@RequestMapping("/gateway/white/list")
@MenuTag(code = "gateway_whiteList")
public class GatewayWhiteListController {
    private final ApplicationEventPublisher publisher;
    private final GatewayWhiteListService gatewayWhiteListService;
    private final Vertx vertx;

    public GatewayWhiteListController(ApplicationEventPublisher publisher, GatewayWhiteListService gatewayWhiteListService, Vertx vertx) {
        this.publisher = publisher;
        this.gatewayWhiteListService = gatewayWhiteListService;
        this.vertx = vertx;
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:white:list:list')")
    @GetMapping(value = "/list", name = "列表")
    public R<?> list(GatewayWhiteList whiteList) {

        return R.data(gatewayWhiteListService.list(MP.getQueryWrapper(whiteList)));
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:white:list:add')")
    @PostMapping(value = "/add", name = "新增")
    public R<?> add(@RequestBody GatewayWhiteList whiteList) {
        gatewayWhiteListService.save(whiteList);

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_WHITE_TO_CACHE, whiteList).onComplete(res -> {
            if (res.failed()) {
                future.completeExceptionally(res.cause());
            } else {
                future.complete(null);
            }
        });

        // 等待异步完成
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 网关事件发布
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(whiteList.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_WHITE_IP_CHANNEL, body));
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:white:list:update')")
    @PostMapping(value = "/update", name = "更新")
    public R<?> update(@RequestBody GatewayWhiteList whiteList) {
        gatewayWhiteListService.updateById(whiteList);

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_WHITE_TO_CACHE, whiteList).onComplete(res -> {
            if (res.failed()) {
                future.completeExceptionally(res.cause());
            } else {
                future.complete(null);
            }
        });

        // 等待异步完成
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 网关事件发布
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(whiteList.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_WHITE_IP_CHANNEL, body));
        return R.data(true);
    }

    @PreAuthorize("@ps.hasPermission('post::gateway:white:list:delete')")
    @PostMapping(value = "/delete", name = "删除")
    public R<?> delete(@RequestBody List<Long> ids) {
        List<GatewayWhiteList> whiteLists = gatewayWhiteListService.listByIds(ids);

        // 通过List集合包裹多个异步CompletableFuture
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (GatewayWhiteList whiteList : whiteLists) {
            // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
            CompletableFuture<Void> future = new CompletableFuture<>();
            vertx.eventBus().request(GatewayEventBusAddr.DELETE_WHITE_TO_CACHE, whiteList).onComplete(res -> {
                if (res.failed()) {
                    future.completeExceptionally(res.cause());
                } else {
                    future.complete(null);
                }
            });
            futures.add(future);
        }

        // 将所有的 CompletableFuture 组合成一个新的 CompletableFuture
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // 等待异步完成
        try {
            allFutures.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 网关事件发布
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, ids);
        body.setEntity(whiteLists);
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.OFFLINE_WHITE_IP_CHANNEL, body));
        return R.data(gatewayWhiteListService.removeBatchByIds(ids));
    }
}
