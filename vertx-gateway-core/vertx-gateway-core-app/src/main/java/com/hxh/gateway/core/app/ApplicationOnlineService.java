package com.hxh.gateway.core.app;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hxh.gateway.api.service.GatewayApiService;
import com.hxh.gateway.application.service.GatewayApplicationService;
import com.hxh.gateway.common.constant.GatewayEventBusAddr;
import com.hxh.gateway.common.entity.GatewayApi;
import com.hxh.gateway.common.entity.GatewayApplication;
import com.hxh.gateway.common.option.GatewayApiOption;
import com.hxh.gateway.common.option.GatewayAppOption;
import com.hxh.gateway.common.util.AuthUtil;
import com.hxh.gateway.common.util.BeanUtil;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.core.app.creator.ApplicationCreate;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 描述：网关应用上线服务
 *
 * @author huxuehao
 **/
public class ApplicationOnlineService extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(ApplicationOnlineService.class);
    private final String CACHE_KEY_DELIMITING = "::";
    /**
     * 应用上线缓存
     * key:   已上线应用ID+"::"+部署ID
     * value: 已上线接口ID
     */
    private final Map<String, Set<Long>> APPLICATION_ONLINE_CACHE = new HashMap<>();
    //private String serveId;

    private final GatewayApiService gatewayApiService;
    private final GatewayApplicationService gatewayApplicationService;
    public ApplicationOnlineService() {
        gatewayApiService = BeanUtil.getBean(GatewayApiService.class);
        gatewayApplicationService = BeanUtil.getBean(GatewayApplicationService.class);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        //serveId = Func.getEvn(Constant.VERTX_GATEWAY_ID, Constant.VERTX_GATEWAY_DEFAULT_ID);

        vertx.eventBus().consumer(GatewayEventBusAddr.ONLINE_APP, this::onlineApp);
        vertx.eventBus().consumer(GatewayEventBusAddr.OFFLINE_APP, this::offlineApp);
        vertx.eventBus().consumer(GatewayEventBusAddr.ONLINE_API, this::onlineApi);
        vertx.eventBus().consumer(GatewayEventBusAddr.OFFLINE_API, this::offlineApi);
        vertx.eventBus().consumer(GatewayEventBusAddr.RESET_API, this::resetApi);

        logger.info("==> 完成 ApplicationOnlineService 启动~");
        startPromise.complete();
    }

    /**
     * 上线应用
     */
    void onlineApp(Message<GatewayAppOption> msg) {
        Long appId = msg.body().getId();
        // 判断应用是否已经上线
        Set<Long> apiIds = APPLICATION_ONLINE_CACHE.get(fetchCacheKey(appId));
        if (apiIds != null) {
            msg.reply(true);
            return;
        }

        // 上线应用
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("appOption", JSON.toJSONString(msg.body())));
        vertx.deployVerticle(ApplicationCreate.class, options)
                .onComplete(res -> {
                    UpdateWrapper<GatewayApplication> uw = new UpdateWrapper<>();
                    uw.set("update_user", AuthUtil.getUserId());
                    uw.set("update_time", Func.currentDataTime());

                    if (res.failed()) {
                        uw.eq("id", appId).set("online", 0);
                        gatewayApplicationService.update(uw);

                        msg.fail(500, res.cause().getMessage());
                        throw new RuntimeException(res.cause());
                    } else {
                        String deploymentId = res.result();
                        APPLICATION_ONLINE_CACHE.put(generateCacheKey(appId, deploymentId), new HashSet<>());

                        // 更新数据库状态
                        uw.eq("id", appId).set("online", 1);
                        gatewayApplicationService.update(uw);

                        logger.info("网关应用 ["+msg.body().getConfig().getPort()+"] 已部署~");
                        msg.reply(true);
                    }
                });
    }

    /**
     * 下线应用
     */
    void offlineApp(Message<GatewayAppOption> msg) {
        Long appId = msg.body().getId();

        // 应用是否已下线
        String cacheKey = fetchCacheKey(appId);
        if (cacheKey == null) {
            return;
        }

        List<Future<Message<Object>>> futures = new ArrayList<>();
        //下线API...
        Set<Long> apiIds = APPLICATION_ONLINE_CACHE.get(cacheKey);
        for (Long apiId : apiIds) {
            Future<Message<Object>> messageFuture = vertx.eventBus().request(appId + GatewayEventBusAddr.DELETE_API_SUF, new GatewayApiOption(apiId, appId, null)).onComplete(res -> {
                if (res.succeeded()) {
                    Set<Long> apiIds_ = APPLICATION_ONLINE_CACHE.get(cacheKey);
                    apiIds_.remove(apiId);
                    APPLICATION_ONLINE_CACHE.put(cacheKey, apiIds);

                    // 更新数据库状态
                    UpdateWrapper<GatewayApi> uw = new UpdateWrapper<>();
                    uw.eq("id",apiId).set("online", 0);
                    uw.set("update_user", AuthUtil.getUserId());
                    uw.set("update_time", Func.currentDataTime());
                    gatewayApiService.update(uw);
                }
            });
            futures.add(messageFuture);
        }

        // 等待所有请求完成
        Future.all(futures)
            .onSuccess(compositeFuture -> {
                // 卸载应用
                vertx.undeploy(getDeploymentIdByCacheKey(cacheKey))
                    .onComplete(undeployRes -> {
                        if (undeployRes.failed()) {
                            msg.fail(500, undeployRes.cause().getMessage());
                            throw new RuntimeException(undeployRes.cause());
                        }
                        APPLICATION_ONLINE_CACHE.remove(cacheKey);

                        // 更新数据库状态
                        UpdateWrapper<GatewayApplication> uw = new UpdateWrapper<>();
                        uw.eq("id", appId).set("online", 0);
                        uw.set("update_user", AuthUtil.getUserId());
                        uw.set("update_time", Func.currentDataTime());
                        gatewayApplicationService.update(uw);

                        logger.info("网关应用 ["+msg.body().getConfig().getPort()+"] 已卸载~");
                        msg.reply(true);
                    });
            })
            .onFailure(err -> {
                msg.fail(500, err.getMessage());
            });
    }

    /**
     * 上线API
     */
    void onlineApi(Message<GatewayApiOption> msg) {
        GatewayApiOption apiOption = msg.body();

        String cacheKey = fetchCacheKey(apiOption.getAppId());
        if (cacheKey == null) {
            msg.fail(500, "API所属应用未部署");
            return;
        }

        vertx.eventBus().request(apiOption.getAppId() + GatewayEventBusAddr.ADD_API_SUF, apiOption).onComplete(res -> {
            UpdateWrapper<GatewayApi> uw = new UpdateWrapper<>();
            uw.set("update_user", AuthUtil.getUserId());
            uw.set("update_time", Func.currentDataTime());

            if (res.failed()) {
                // 更新数据库状态
                uw.eq("id", apiOption.getId()).set("online", 0);
                gatewayApiService.update(uw);

                msg.fail(500, res.cause().toString());
                throw new RuntimeException(res.cause());
            } else {
                Set<Long> apiIds = APPLICATION_ONLINE_CACHE.get(cacheKey);
                apiIds.add(apiOption.getId());
                APPLICATION_ONLINE_CACHE.put(cacheKey, apiIds);

                // 更新数据库状态
                uw.eq("id", apiOption.getId()).set("online", 1);
                gatewayApiService.update(uw);

                logger.info("网关API ["+msg.body().getConfig().getRoutePath()+"] 已上线~");
                msg.reply(true);
            }
        });
    }

    /**
     * 下线API
     */
    void offlineApi(Message<GatewayApiOption> msg) {
        GatewayApiOption apiOption = msg.body();

        String cacheKey = fetchCacheKey(apiOption.getAppId());
        if (cacheKey == null) {
            msg.reply(true);
            return;
        }

        vertx.eventBus().request(apiOption.getAppId() + GatewayEventBusAddr.DELETE_API_SUF, apiOption).onComplete(res -> {
            if (res.failed()) {
                msg.fail(500, res.cause().toString());
            } else {
                Set<Long> apiIds = APPLICATION_ONLINE_CACHE.get(cacheKey);
                apiIds.remove(apiOption.getId());
                APPLICATION_ONLINE_CACHE.put(cacheKey, apiIds);

                // 更新数据库状态
                UpdateWrapper<GatewayApi> uw = new UpdateWrapper<>();
                uw.eq("id", apiOption.getId()).set("online", 0);
                uw.set("update_user", AuthUtil.getUserId());
                uw.set("update_time", Func.currentDataTime());
                gatewayApiService.update(uw);

                logger.info("网关API ["+msg.body().getConfig().getRoutePath()+"] 已下线~");
                msg.reply(true);
            }
        });
    }

    /**
     * 更新API
     */
    void resetApi(Message<GatewayApiOption> msg) {
        GatewayApiOption apiOption = msg.body();

        String cacheKey = fetchCacheKey(apiOption.getAppId());
        if (cacheKey == null) {
            msg.reply(true);
            return;
        }

        vertx.eventBus().request(apiOption.getAppId() + GatewayEventBusAddr.UPDATE_API_SUF, apiOption).onComplete(res -> {
            if (res.failed()) {
                msg.fail(500, res.cause().toString());
            } else {
                msg.reply(true);
            }
        });
    }

    /**
     * 生成缓存Key
     * @param appId        网关应用ID
     * @param deploymentId vertx部署ID
     */
    private String generateCacheKey(Long appId, String deploymentId) {
        return appId + CACHE_KEY_DELIMITING + deploymentId;
    }

    /**
     * 获取已经存在的缓存KEY
     * @param appId 网关应用ID
     */
    private String fetchCacheKey(Long appId) {
        Set<String> keys = APPLICATION_ONLINE_CACHE.keySet();
        for (String key : keys) {
            if (key.startsWith(String.valueOf(appId))) {
                return key;
            }
        }

        return null;
    }

    /**
     * 根据缓存Key获取部署ID
     * @param cacheKey 应用缓存Key
     */
    private String getDeploymentIdByCacheKey(String cacheKey) {
        String[] split = cacheKey.split(CACHE_KEY_DELIMITING);
        if (split.length != 2) {
            return null;
        }
        return split[1];
    }
}
