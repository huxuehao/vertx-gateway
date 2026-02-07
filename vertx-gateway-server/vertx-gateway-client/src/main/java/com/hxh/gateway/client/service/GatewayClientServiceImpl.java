package com.hxh.gateway.client.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.api.mapper.GatewayApiMapper;
import com.hxh.gateway.application.mapper.GatewayApplicationMapper;
import com.hxh.gateway.classify.mapper.ClassifyMapper;
import com.hxh.gateway.client.mapper.GatewayClientMapper;
import com.hxh.gateway.common.config.GatewayEvent;
import com.hxh.gateway.common.config.Identity;
import com.hxh.gateway.common.config.RedisSubscribeBody;
import com.hxh.gateway.common.constant.ChannelConstant;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.dto.ClientAuthTreeDto;
import com.hxh.gateway.common.dto.GatewayClientDto;
import com.hxh.gateway.common.entity.GatewayClient;
import com.hxh.gateway.common.entity.GatewayClientAuth;
import com.hxh.gateway.common.util.AuthUtil;
import com.hxh.gateway.common.util.tree.TreeUtil;
import io.vertx.core.Vertx;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 描述：授权客户端
 *
 * @author huxuehao
 **/
@Service
public class GatewayClientServiceImpl extends ServiceImpl<GatewayClientMapper, GatewayClient> implements GatewayClientService {
    private final ApplicationEventPublisher publisher;
    private final GatewayClientAuthService gatewayClientAuthService;
    private final GatewayApplicationMapper gatewayApplicationMapper;
    private final GatewayApiMapper gatewayApiMapper;
    private final ClassifyMapper classifyMapper;
    private final Vertx vertx;

    public GatewayClientServiceImpl(ApplicationEventPublisher publisher, GatewayClientAuthService gatewayClientAuthService, GatewayApplicationMapper gatewayApplicationMapper, GatewayApiMapper gatewayApiMapper, ClassifyMapper classifyMapper, Vertx vertx) {
        this.publisher = publisher;
        this.gatewayClientAuthService = gatewayClientAuthService;
        this.gatewayApplicationMapper = gatewayApplicationMapper;
        this.gatewayApiMapper = gatewayApiMapper;
        this.classifyMapper = classifyMapper;
        this.vertx = vertx;
    }

    @Override
    @Transactional
    public boolean addV2(GatewayClientDto client) {
        GatewayClient gatewayClient = new GatewayClient();
        BeanUtils.copyProperties(client, gatewayClient);

        gatewayClient.setCreateUser(AuthUtil.getUserId());
        gatewayClient.setCreateTime(new Date());
        save(gatewayClient);

        List<GatewayClientAuth> auths = client.getAuths();
        if (auths == null || auths.isEmpty()) {
            return true;
        }

        for (GatewayClientAuth auth : auths) {
            auth.setClientId(gatewayClient.getId());
        }

        gatewayClientAuthService.saveBatch(auths);

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_CLIENT_TO_CACHE, client).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(gatewayClient.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_CLIENT_CHANNEL, body));
        return true;
    }

    @Override
    @Transactional
    public boolean updateV2(GatewayClientDto client) {
        GatewayClient gatewayClient = new GatewayClient();
        BeanUtils.copyProperties(client, gatewayClient);

        gatewayClient.setUpdateUser(AuthUtil.getUserId());
        gatewayClient.setUpdateTime(new Date());
        updateById(gatewayClient);

        QueryWrapper<GatewayClientAuth> uw = new QueryWrapper<>();
        uw.eq("client_id", gatewayClient.getId());
        gatewayClientAuthService.remove(uw);

        List<GatewayClientAuth> auths = client.getAuths();
        if (auths == null || auths.isEmpty()) {
            return true;
        }

        for (GatewayClientAuth auth : auths) {
            auth.setClientId(gatewayClient.getId());
        }
        gatewayClientAuthService.saveBatch(auths);

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.eventBus().request(GatewayEventBusAddr.INSERT_UPDATE_CLIENT_TO_CACHE, client).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(gatewayClient.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_CLIENT_CHANNEL, body));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteV2(List<Long> ids) {

        List<GatewayClient> gatewayClients = listByIds(ids);
        String codesStr = gatewayClients.stream().map(GatewayClient::getClientCode).collect(Collectors.joining(","));

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.eventBus().request(GatewayEventBusAddr.DELETE_CLIENT_TO_CACHE, codesStr).onComplete(res -> {
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


        removeBatchByIds(ids);

        QueryWrapper<GatewayClientAuth> uw = new QueryWrapper<>();
        uw.in("client_id", ids);
        gatewayClientAuthService.remove(uw);

        // 网关事件发布
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, ids);
        body.setRemark(codesStr);
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.OFFLINE_CLIENT_CHANNEL, body));
        return true;
    }

    @Override
    public GatewayClientDto getOneV2(Long id) {
        GatewayClient gatewayClient = getById(id);

        GatewayClientDto client = new GatewayClientDto();
        BeanUtils.copyProperties(gatewayClient, client);

        QueryWrapper<GatewayClientAuth> qw = new QueryWrapper<>();
        qw.eq("client_id", id);
        List<GatewayClientAuth> auths = gatewayClientAuthService.list(qw);
        client.setAuths(auths);

        return client;
    }

    @Override
    public List<ClientAuthTreeDto> tree() {
        List<ClientAuthTreeDto> treeList = gatewayApplicationMapper.getTreeList(DBConst.GATEWAY_APPLICATION);
        List<ClientAuthTreeDto> collect = classifyMapper.getTreeList(DBConst.GATEWAY_API_CLASSIFY);
        collect.forEach(item -> item.setDisabled(true));
        treeList.addAll(collect);
        treeList.addAll(gatewayApiMapper.getTreeList(DBConst.GATEWAY_API));

        return TreeUtil.convertTree(treeList);
    }
}
