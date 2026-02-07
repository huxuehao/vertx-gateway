package com.hxh.gateway.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.api.mapper.GatewayApiMapper;
import com.hxh.gateway.application.mapper.GatewayApplicationMapper;
import com.hxh.gateway.classify.mapper.ClassifyMapper;
import com.hxh.gateway.common.config.GatewayEvent;
import com.hxh.gateway.common.config.Identity;
import com.hxh.gateway.common.config.RedisSubscribeBody;
import com.hxh.gateway.common.constant.ChannelConstant;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.dto.ClientAuthTreeDto;
import com.hxh.gateway.common.entity.GatewayApi;
import com.hxh.gateway.common.enums.ApiAccessLimitType;
import com.hxh.gateway.common.option.ApiConfig;
import com.hxh.gateway.common.option.GatewayApiOption;
import com.hxh.gateway.common.option.ProxyApiUrl;
import com.hxh.gateway.common.util.AuthUtil;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.common.util.tree.TreeUtil;
import io.vertx.core.Vertx;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 描述：网关API
 *
 * @author huxuehao
 **/
@Service
public class GatewayApiServiceImpl extends ServiceImpl<GatewayApiMapper, GatewayApi> implements GatewayApiService {
    private final ApplicationEventPublisher publisher;
    private final GatewayApplicationMapper applicationMapper;
    private final ClassifyMapper classifyMapper;
    private final Vertx vertx;

    public GatewayApiServiceImpl(ApplicationEventPublisher publisher, GatewayApplicationMapper applicationMapper, ClassifyMapper classifyMapper, Vertx vertx) {
        this.publisher = publisher;
        this.applicationMapper = applicationMapper;
        this.classifyMapper = classifyMapper;
        this.vertx = vertx;
    }

    @Override
    public List<ClientAuthTreeDto> classifyTree() {
        List<ClientAuthTreeDto> treeList = applicationMapper.getTreeList(DBConst.GATEWAY_APPLICATION);
        treeList.addAll(classifyMapper.getTreeList(DBConst.GATEWAY_API_CLASSIFY));
        return TreeUtil.convertTree(treeList);
    }

    @Override
    public boolean saveV2(GatewayApi api) {
        api.setCreateUser(AuthUtil.getUserId());
        api.setCreateTime(new Date());

        return save(api);
    }

    @Override
    public boolean updateByIdV2(GatewayApi api) {
        UpdateWrapper<GatewayApi> uw = new UpdateWrapper<>();
        uw.set("name", api.getName());
        uw.set("remark", api.getRemark());
        uw.set("parent_id", api.getParentId());
        uw.set("app_id", api.getAppId());
        uw.set("update_user", AuthUtil.getUserId());
        uw.set("update_time", new Date());
        uw.eq("id", api.getId());

        return update(uw);
    }

    @Override
    public boolean saveConfig(GatewayApi api) {
        GatewayApi route = getById(api.getId());
        //if (route.getOnline() == 1) {
        //    throw new RuntimeException(route.getName() + " [" + route.getPath() +"] 已部署，请先卸载");
        //}

        ApiConfig config = JSON.parseObject(api.getConfig(), ApiConfig.class);

        if (config.getAccessLimitType() != ApiAccessLimitType.NONE
                && config.getIpTimes() > 0
                && config.getRouterTimes() > 0) {
            throw new RuntimeException("IP访问次数 和 路由访问次数 不可同时大于0");
        }

        // 检查代理服务器权重之和
        List<ProxyApiUrl> collect = config.getProxyApiUrls().stream()
                .filter(server -> server.getWeight() > 0)
                .map(server -> new ProxyApiUrl(server.getUrl(), server.getWeight()))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalArgumentException("代理服务器列表权重之和需大于0");
        }

        QueryWrapper<GatewayApi> qw = new QueryWrapper<>();
        qw.eq("path", config.getRoutePath());
        qw.eq("app_id", api.getAppId());
        qw.ne("id", api.getId());
        qw.eq("del_flag", 0);

        List<GatewayApi> list = list(qw);
        if (!list.isEmpty()) {
            throw new RuntimeException("请求地址 ["+config.getRoutePath()+"] 已被占用");
        }
        route.setPath(config.getRoutePath());
        route.setConfig(api.getConfig());
        route.setUpdateUser(AuthUtil.getUserId());
        route.setUpdateTime(new Date());

        // 如果是下线状态，直接更新
        if (route.getOnline() == 0) {
            return updateById(route);
        }

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();
        GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
        vertx.eventBus().request(GatewayEventBusAddr.RESET_API, apiOption).onComplete(res -> {
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

        updateById(route);

        // 网关事件发布
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(api.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.RESET_API_CHANNEL, body));
        return true;
    }

    @Override
    @Transactional
    public boolean removeByIdV2(List<Long> ids) {
        List<GatewayApi> apis = listByIds(ids);

        for (GatewayApi api : apis) {
            if (api.getOnline() == 1) {
                throw new RuntimeException(api.getName() + " [" + api.getPath() +"] 已部署，请先卸载");
            }
        }

        return removeBatchByIds(apis.stream().map(GatewayApi::getId).collect(Collectors.toList()));
    }

    @Override
    public boolean online(Long id) {
        GatewayApi api = getById(id);
        if (Func.isEmpty(api.getConfig())) {
            throw new RuntimeException("请完善接口配置");
        } else if (api.getOnline() == 1) {
            return true;
        }

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();

        // 部署指定应用
        GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
        vertx.eventBus().request(GatewayEventBusAddr.ONLINE_API, apiOption).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(api.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_API_CHANNEL, body));
        return true;
    }

    @Override
    public boolean offline(Long id) {
        GatewayApi api = getById(id);
        if (Func.isEmpty(api.getConfig())) {
            throw new RuntimeException("请完善接口配置");
        } else if (api.getOnline() == 0) {
            return true;
        }

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();

        // 卸载指定应用
        GatewayApiOption apiOption = new GatewayApiOption(api.getId(), api.getAppId(), JSON.parseObject(api.getConfig(), ApiConfig.class));
        vertx.eventBus().request(GatewayEventBusAddr.OFFLINE_API, apiOption).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(api.getId()));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.OFFLINE_API_CHANNEL, body));
        return true;
    }
}
