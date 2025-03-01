package com.gateway.application.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.application.mapper.GatewayApplicationMapper;
import com.gateway.common.config.GatewayEvent;
import com.gateway.common.config.Identity;
import com.gateway.common.config.RedisSubscribeBody;
import com.gateway.common.constant.ChannelConstant;
import com.gateway.common.constant.GatewayEventBusAddr;
import com.gateway.common.constant.GatewayRouteConst;
import com.gateway.common.entity.GatewayApplication;
import com.gateway.common.option.AppConfig;
import com.gateway.common.option.GatewayAppOption;
import com.gateway.common.util.AuthUtil;
import com.gateway.common.util.Func;
import io.vertx.core.Vertx;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 描述：网关应用
 *
 * @author huxuehao
 **/
@Service
public class GatewayApplicationServiceImpl extends ServiceImpl<GatewayApplicationMapper, GatewayApplication> implements GatewayApplicationService {
    private final Vertx vertx;
    private final ApplicationEventPublisher publisher;

    public GatewayApplicationServiceImpl(Vertx vertx, ApplicationEventPublisher publisher) {
        this.vertx = vertx;
        this.publisher = publisher;
    }

    @Override
    public boolean saveV2(GatewayApplication application) {
        application.setCreateUser(AuthUtil.getUserId());
        application.setCreateTime(new Date());

        return save(application);
    }

    @Override
    public boolean updateByIdV2(GatewayApplication application) {
        UpdateWrapper<GatewayApplication> uw = new UpdateWrapper<>();
        uw.set("name", application.getName());
        uw.set("remark", application.getRemark());
        uw.set("update_user", AuthUtil.getUserId());
        uw.set("update_time", new Date());
        uw.eq("id", application.getId());

        return update(uw);
    }

    @Override
    public boolean saveConfig(GatewayApplication application) {
        GatewayApplication app = getById(application.getId());
        if (app.getOnline() == 1) {
            throw new RuntimeException(app.getName() + " [" + app.getProtocol() +"_"+app.getPort()+"] 已部署，请先卸载");
        }

        AppConfig config = JSON.parseObject(application.getConfig(), AppConfig.class);

        if (Objects.equals(config.getPort(), GatewayRouteConst.TOKEN_SERVER_PORT)) {
            throw new RuntimeException("端口 ["+config.getPort()+"] 是保留端口，不可占用");
        }

        QueryWrapper<GatewayApplication> qw = new QueryWrapper<>();
        qw.eq("port", config.getPort());
        qw.ne("id", application.getId());
        qw.eq("del_flag", 0);

        List<GatewayApplication> list = list(qw);
        if (!list.isEmpty()) {
            throw new RuntimeException("端口 ["+config.getPort()+"] 已被占用");
        }
        app.setPort(config.getPort());
        app.setProtocol(config.getProtocol());
        app.setConfig(application.getConfig());
        app.setUpdateUser(AuthUtil.getUserId());
        app.setUpdateTime(new Date());
        return updateById(app);
    }

    @Override
    @Transactional
    public boolean removeByIdV2(List<Long> ids) {
        List<GatewayApplication> apps = listByIds(ids);

        for (GatewayApplication app : apps) {
            if (app.getOnline() == 1) {
                throw new RuntimeException(app.getName() + " [" + app.getProtocol() +"_"+app.getPort()+"] 已部署，请先卸载");
            }
        }

        return removeBatchByIds(apps.stream().map(GatewayApplication::getId).collect(Collectors.toList()));
    }

    @Override
    public boolean online(Long id) {
        GatewayApplication application = getById(id);
        if (Func.isEmpty(application.getConfig())) {
            throw new RuntimeException("请完善应用配置");
        } else if (application.getOnline() == 1) {
            return true;
        }

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();

        // 启动指定应用
        GatewayAppOption appOption = new GatewayAppOption(application.getId(), JSON.parseObject(application.getConfig(), AppConfig.class));
        vertx.eventBus().request(GatewayEventBusAddr.ONLINE_APP, appOption).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(id));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.ONLINE_APP_CHANNEL, body));
        return true;
    }

    @Override
    public boolean offline(Long id) {
        GatewayApplication application = getById(id);
        if (Func.isEmpty(application.getConfig())) {
            throw new RuntimeException("请完善应用配置");
        } else if (application.getOnline() == 0) {
            return true;
        }

        // 通过CompletableFuture来桥接Vertx的异步操作和Spring Boot的同步调用
        CompletableFuture<Void> future = new CompletableFuture<>();

        // 卸载指定应用
        GatewayAppOption appOption = new GatewayAppOption(application.getId(), JSON.parseObject(application.getConfig(), AppConfig.class));
        vertx.eventBus().request(GatewayEventBusAddr.OFFLINE_APP, appOption).onComplete(res -> {
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
        RedisSubscribeBody body = new RedisSubscribeBody(Identity.uuid, Collections.singletonList(id));
        publisher.publishEvent(new GatewayEvent(this, ChannelConstant.OFFLINE_APP_CHANNEL, body));
        return true;
    }
}
