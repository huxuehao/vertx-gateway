package com.hxh.gateway.common.constant;

/**
 * 描述：网关应用事件总线地址
 *
 * @author huxuehao
 **/
public class GatewayEventBusAddr {
    // 上线应用
    public static final String ONLINE_APP = "vertx.gateway.online.app";
    // 下线应用
    public static final String OFFLINE_APP = "vertx.gateway.offline.app";
    // 上线应用下所有的API
    public static final String ONLINE_SELF_ALL_API = "vertx.gateway.offline.app.self.apis";
    // 上线API
    public static final String ONLINE_API = "vertx.gateway.online.api";
    // 下线API
    public static final String OFFLINE_API = "vertx.gateway.offline.api";
    // 更新API
    public static final String RESET_API = "vertx.gateway.reset.api";

    // 向应用中添加API
    public static final String ADD_API_SUF = ".vertx.gateway.add.api";
    // 更新应用中的API
    public static final String UPDATE_API_SUF = ".vertx.gateway.update.api";
    // 删除应用中的API
    public static final String DELETE_API_SUF = ".vertx.gateway.delete.api";

    // 插入更新客户端权限到缓存
    public final static String INSERT_UPDATE_CLIENT_TO_CACHE = "vertx.gateway.client.insert.update";
    // 从缓存删除客户端权限
    public final static String DELETE_CLIENT_TO_CACHE = "vertx.gateway.client.insert.delete";

    // 插入更新白名单到缓存
    public final static String INSERT_UPDATE_WHITE_TO_CACHE = "vertx.gateway.white.insert.update";
    // 从缓存删除白名单
    public final static String DELETE_WHITE_TO_CACHE = "vertx.gateway.white.insert.delete";
}
