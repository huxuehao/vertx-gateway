package com.hxh.gateway.core.api;

import com.hxh.gateway.common.constant.GatewayRouteConst;
import com.hxh.gateway.common.entity.GatewayApiLog;
import com.hxh.gateway.common.enums.ApiProxyApiType;
import com.hxh.gateway.common.option.ApiConfig;
import com.hxh.gateway.common.option.GatewayApiOption;
import com.hxh.gateway.common.option.GatewayAppOption;
import com.hxh.gateway.common.option.ProxyApiUrl;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.common.util.WebUtil;
import com.hxh.gateway.core.api.handler.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.*;

/**
 * 描述：API路由创建者
 *
 * @author huxuehao
 **/
public class ApiRouterCreator {
    /**
     * API缓存
     * key：apiID
     * value: API的路由
     */
    private final Map<Long, List<Route>> ROUTE_CACHE = new HashMap<>();
    private final Router router;
    private final GatewayAppOption appOption;
    private final HttpClient httpClient;

    public ApiRouterCreator(GatewayAppOption appOption, Router router, HttpClient httpClient) {
        this.appOption = appOption;
        this.router = router;
        this.httpClient = httpClient;
    }

    public void addRoute(Message<GatewayApiOption> msg) {
        if (router == null) {
            msg.fail(500, "API所属应用下没Router");
            return;
        }

        GatewayApiOption apiOption = msg.body();
        checkAndPerfectProxyUrl(apiOption);

        ArrayList<Route> routes = new ArrayList<>();
        try {
            // 添加空的路由，作为日志记录的起始
            Route route = router.route();
            route.handler((rct -> {
                GatewayApiLog apiLog = new GatewayApiLog();
                apiLog.setApiId(apiOption.getId());
                apiLog.setAppId(appOption.getId());
                apiLog.setReqMethod(apiOption.getConfig().getRouteMethod().toString());
                apiLog.setReqPath(rct.request().path());
                apiLog.setReqIp(WebUtil.getIP(rct.request()));
                apiLog.setStartTime(System.currentTimeMillis());
                rct.put(GatewayRouteConst.API_LOG_BODY, apiLog);

                rct.next();
            }));
            routes.add(route);

            // 添加认证处理器
            addAuthHandler(routes, apiOption, httpClient);
            // 添加访问限制处理器
            addAccessLimitHandler(routes, apiOption, httpClient);
            // 添加参数检查处理器
            addParamsCheckHandler(routes, apiOption, httpClient);
            // 添加代理服务处理器
            addProxyServerHandler(routes, apiOption, httpClient);
            // 添加异常处理器
            addExceptionHandler(routes, apiOption, httpClient);
        } catch (Exception e) {
            routes.forEach(r -> r.disable().remove());
            msg.fail(500, e.getMessage());
            throw new RuntimeException(e);
        }

        ROUTE_CACHE.put(apiOption.getId(), routes);
        msg.reply(true);
    }

    /**
     * 检查并完善代理服务的URL
     */
    private void checkAndPerfectProxyUrl(GatewayApiOption apiOption) {
        ApiConfig config = apiOption.getConfig();
        String urlPrefix = appOption.getConfig().getServerUrlPrefix();

        if (Func.isEmpty(urlPrefix)) {
            return;
        }
        if (!ApiProxyApiType.HTTP.equals(config.getProxyApiType())) {
            return;
        }

        List<ProxyApiUrl> proxyApiUrls = config.getProxyApiUrls();
        for (ProxyApiUrl proxyApiUrl : proxyApiUrls) {
            if (proxyApiUrl.getUrl().toLowerCase().startsWith("http")) {
                continue;
            }

            if (urlPrefix.endsWith("/")) {
                urlPrefix = urlPrefix.substring(0, urlPrefix.length()-1);
            }
            String urlSuffix = proxyApiUrl.getUrl();
            if (!urlSuffix.startsWith("/")) {
                urlSuffix = "/"+urlSuffix;
            }

            proxyApiUrl.setUrl(urlPrefix + urlSuffix);
        }
    }

    /**
     * 添加认证处理器
     * @param routes    路由集合
     * @param apiOption GatewayApiOption
     */
    private void addAuthHandler(ArrayList<Route> routes, GatewayApiOption apiOption, HttpClient httpClient) {
        Route route = router.route();
        RouteHandlerUtil.setHandler(RouteAuthHandler.class, route, apiOption, httpClient);
        routes.add(route);
    }


    /**
     * 添加访问限制处理器
     * @param routes    路由集合
     * @param apiOption GatewayApiOption
     */
    private void addAccessLimitHandler(ArrayList<Route> routes, GatewayApiOption apiOption, HttpClient httpClient) {
        Route route = router.route();
        RouteHandlerUtil.setHandler(RouteAccessLimitHandler.class, route, apiOption, httpClient);
        routes.add(route);
    }

    /**
     * 添加参数检查处理器
     * @param routes    路由集合
     * @param apiOption GatewayApiOption
     */
    private void addParamsCheckHandler(ArrayList<Route> routes, GatewayApiOption apiOption, HttpClient httpClient) {
        Route route = router.route();
        RouteHandlerUtil.setHandler(RouteParamsCheckHandler.class, route, apiOption, httpClient);
        routes.add(route);
    }

    /**
     * 添加代理服务处理器
     * @param routes    路由集合
     * @param apiOption GatewayApiOption
     */
    private void addProxyServerHandler(ArrayList<Route> routes, GatewayApiOption apiOption, HttpClient httpClient) {
        Route route = router.route();
        if (ApiProxyApiType.HTTP == apiOption.getConfig().getProxyApiType()) {
            RouteHandlerUtil.setHandler(RouteClientHandler.class, route, apiOption, httpClient);
        } else if (ApiProxyApiType.REDIRECT == apiOption.getConfig().getProxyApiType()) {
            RouteHandlerUtil.setHandler(RouteRedirectHandler.class, route, apiOption, httpClient);
        }
        routes.add(route);
    }


    /**
     * 添加异常处理器
     * @param routes    路由集合
     * @param apiOption GatewayApiOption
     */
    private void addExceptionHandler(ArrayList<Route> routes, GatewayApiOption apiOption, HttpClient httpClient) {
        Route route = router.route();
        RouteHandlerUtil.setHandler(RouteExceptionHandler.class, route, apiOption, httpClient);
        routes.add(route);
    }


    public void updateRoute(Message<GatewayApiOption> msg) {
        if (router == null) {
            msg.fail(500, "API 所属应用下没 Router");
            return;
        }

        GatewayApiOption apiOption = msg.body();
        if (apiOption == null) {
            msg.fail(500, "GatewayApiOption 不可为空");
            return;
        }

        List<Route> routes = ROUTE_CACHE.get(apiOption.getId());
        if (routes != null && !routes.isEmpty()) {
            routes.forEach(r -> r.disable().remove());
        }

        addRoute(msg);
    }


    public void deleteRoute(Message<GatewayApiOption> msg) {
        if (router == null) {
            msg.fail(500, "API 所属应用下没 Router");
            return;
        }

        GatewayApiOption apiOption = msg.body();
        if (apiOption == null) {
            msg.fail(500, "GatewayApiOption 不可为空");
            return;
        }

        List<Route> routes = ROUTE_CACHE.get(apiOption.getId());
        if (routes != null && !routes.isEmpty()) {
            routes.forEach(r -> r.disable().remove());
        }

        msg.reply(true);
    }

}
