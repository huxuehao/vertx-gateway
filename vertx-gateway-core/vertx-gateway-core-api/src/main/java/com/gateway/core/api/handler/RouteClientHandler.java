package com.gateway.core.api.handler;

import com.alibaba.fastjson2.JSON;
import com.gateway.common.constant.GatewayRouteConst;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.enums.ApiBalancingType;
import com.gateway.common.enums.ApiParamPosition;
import com.gateway.common.option.ApiConfig;
import com.gateway.common.option.ProxyApiUrl;
import com.gateway.common.option.ProxyConstParam;
import com.gateway.common.util.MeUtil;
import com.gateway.core.api.RouteHandler;
import com.gateway.core.log.ConcurrentLogProducer;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 描述：客户端处理器
 *
 * @author huxuehao
 **/
public class RouteClientHandler extends RouteHandler {
    private static final Logger logger = LogManager.getLogger(RouteClientHandler.class);

    private LoadBalancer loadBalancer;

    @Override
    public void handle(RoutingContext rct) {
        // 字段有效性检查
        if (checkFieldIsNull()) {
            endRoutingFieldNull(rct);
            return;
        }

        ApiConfig config = apiOption.getConfig();

        RequestOptions proxyRequestOptions = new RequestOptions();
        proxyRequestOptions.setMethod(HttpMethod.valueOf(config.getProxyApiMethod().name()));
        proxyRequestOptions.setTimeout(config.getProxyTimeoutTime());

        // 获取网关接口请求头参数
        MultiMap headerParams = rct.get(GatewayRouteConst.HEADER_PARAMS_KEY);
        proxyRequestOptions.setHeaders(headerParams);

        // 设置后端服务的请求的请求头
        MultiMap rctHeaders = rct.request().headers();
        String contentType;
        if ((contentType = rctHeaders.get("Content-Type")) != null) {
            proxyRequestOptions.putHeader("Content-Type", contentType);
        } else if ((contentType = rctHeaders.get("content-type")) != null) {
            proxyRequestOptions.putHeader("content-type", contentType);
        }

        // 添加常量请求头
        List<ProxyConstParam> proxyConstParams = apiOption.getConfig().getProxyConstParams();
        for (ProxyConstParam constParam : proxyConstParams) {
            if (ApiParamPosition.HEADER == constParam.getPosition()) {
                proxyRequestOptions.putHeader(constParam.getKey(), constParam.getValue());
            }
        }

        // 添加负载均衡器
        if (loadBalancer == null) {
            loadBalancer = new LoadBalancer(config.getProxyApiUrls());
        }

        // 缓存请求体
        rct.request().bodyHandler(body -> {
            // 日志采集
            GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
            log.setReqBody(body.toString());
            log.setProxyConstParams(JSON.toJSONString(config.getProxyConstParams()));
            log.setProxyMethod(config.getProxyApiMethod().toString());

            executeProxyRequestWithRetry(rct, proxyRequestOptions, config, 0, body)
                    .onSuccess(resp -> {
                        // 将代理响应的状态码和头部写回原始响应
                        rct.response().setStatusCode(resp.statusCode());
                        resp.headers().forEach(entry -> {
                            rct.response().putHeader(entry.getKey(), entry.getValue());
                        });

                        // 使用Pump将代理响应的body写回原始响应
                        Pump respPump = Pump.pump(resp, rct.response());
                        respPump.start();

                        // 代理响应异常处理器
                        resp.exceptionHandler(e -> {
                            logger.error("代理响应传输发生了异常:" + e);
                            respPump.stop();
                            rct.response().end(); // 结束原始响应
                        });

                        // 代理响应结束处理器
                        resp.endHandler(end -> {
                            logger.info("代理响应传输完成");
                            rct.response().end(); // 结束原始响应
                        });

                        // 日志采集
                        log.setStatus(1);
                        log.setRespBody(resp.body().toString());
                        ConcurrentLogProducer.pushLog(log);
                    })
                    .onFailure(err -> {
                        logger.error("请求转发失败，重试次数用尽: " + err.getMessage());
                        rct.response().setStatusCode(502).end("请求转发失败");

                        // 日志采集
                        log.setStatus(1);
                        log.setErrorContent(MeUtil.catchThrowableStackInfo(err));
                        ConcurrentLogProducer.pushLog(log);
                    });
        });
    }

    /**
     * 执行代理请求，并重试
     * @param rct                 RoutingContext
     * @param proxyRequestOptions 请求代理接口的RequestOptions
     * @param apiConfig           接口的配置文件
     * @param retryCount          已经重试的次数
     * @param requestBody         router请求体
     */
    private Future<HttpClientResponse> executeProxyRequestWithRetry(RoutingContext rct, RequestOptions proxyRequestOptions, ApiConfig apiConfig, int retryCount, Buffer requestBody) {
        return executeProxyRequest(rct, proxyRequestOptions, apiConfig, requestBody)
                .onComplete(res -> {
                    if (res.failed()) {
                        throw new RuntimeException(res.cause());
                    }
                })
                .recover(err -> {
                    if (retryCount < apiConfig.getProxyRetryTimes()) {
                        logger.warn("代理请求失败，准备重试。重试次数: " + (retryCount + 1) + "/" + apiConfig.getProxyRetryTimes());
                        return Future.future(promise -> {
                            // 延迟一段时间后重试
                            rct.vertx().setTimer(apiConfig.getProxyRetryInterval(), timerId -> {
                                executeProxyRequestWithRetry(rct, proxyRequestOptions, apiConfig, retryCount + 1, requestBody)
                                        .onComplete(promise);
                            });
                        });
                    } else {
                        // 重试次数用尽，返回失败
                        return Future.failedFuture(err);
                    }
                });
    }

    /**
     * 执行单个代理请求
     */
    private Future<HttpClientResponse> executeProxyRequest(RoutingContext rct, RequestOptions proxyRequestOptions, ApiConfig config, Buffer requestBody) {
        String url;

        // 基于负载均衡器选择URL
        if (ApiBalancingType.HASH == config.getLoadBalancingStrategy()) {
            String ip = rct.request().remoteAddress().host();
            url = loadBalancer.getUrlByIpHash(ip);
        } else {
            url = loadBalancer.getUrlByRoundRobin();
        }

        // 日志采集
        GatewayApiLog log = rct.get(GatewayRouteConst.API_LOG_BODY);
        log.setProxyPath(url);

        // 获取网关接口路径参数
        MultiMap pathParams = rct.get(GatewayRouteConst.PATH_PARAMS_KEY);
        if (pathParams.entries() != null) {
            for (Map.Entry<String, String> entry : pathParams.entries()) {
                url = url.replace(":" + entry.getKey(), entry.getValue());
            }
        }

        // 获取网关接口请求参数
        QueryStringEncoder queryParams = rct.get(GatewayRouteConst.QUERY_PARAMS_KEY);
        // 添加常量请求参数
        List<ProxyConstParam> proxyConstParams = apiOption.getConfig().getProxyConstParams();
        for (ProxyConstParam constParam : proxyConstParams) {
            if (ApiParamPosition.QUERY == constParam.getPosition()) {
                queryParams.addParam(constParam.getKey(), constParam.getValue());
            }
        }
        proxyRequestOptions.setAbsoluteURI(url + queryParams.toString());

        return httpClient.request(proxyRequestOptions)
                .compose(req -> {
                    // 设置HTTP chunked 模式（如果需要流式传输）
                    req.setChunked(true);

                    // 写入缓存的请求体
                    req.write(requestBody);

                    // 代理请求异常处理器
                    req.exceptionHandler(e -> {
                        logger.error("代理请求发生了异常:" + e);
                    });

                    // 发送请求
                    req.end();

                    // 返回Future<HttpClientResponse>
                    return req.response();
                });
    }

    /**
     * 负载均衡器
     */
    static class LoadBalancer {
        private final List<ProxyApiUrl> servers;
        private final AtomicInteger currentIndex = new AtomicInteger(0); // 用于轮询

        public LoadBalancer(List<ProxyApiUrl> servers) {
            if (servers.isEmpty()) {
                throw new IllegalArgumentException("代理服务器列表不能为空");
            }

            this.servers = servers.stream()
                    .filter(server -> server.getWeight() > 0)
                    .map(server -> new ProxyApiUrl(server.getUrl(), server.getWeight()))
                    .collect(Collectors.toList());

            if (this.servers.isEmpty()) {
                throw new IllegalArgumentException("代理服务器列表权重之和需大于0");
            }
        }

        /**
         * 加权轮询选择 URL
         */
        public String getUrlByRoundRobin() {
            if (servers.size() == 1) {
                return servers.get(0).getUrl(); // 只有一组 URL 时直接返回
            }

            int totalWeight = servers.stream().mapToInt(ProxyApiUrl::getWeight).sum();
            int index = currentIndex.getAndUpdate(i -> (i + 1) % totalWeight);
            return getUrlByWeightedIndex(index);
        }

        /**
         * IP 哈希选择 URL
         *
         * @param clientIp 客户端 IP
         */
        public String getUrlByIpHash(String clientIp) {
            if (servers.size() == 1) {
                return servers.get(0).getUrl(); // 只有一组 URL 时直接返回
            }

            int hashCode = clientIp.hashCode();
            int totalWeight = servers.stream().mapToInt(ProxyApiUrl::getWeight).sum();
            int index = Math.abs(hashCode) % totalWeight;
            return getUrlByWeightedIndex(index);
        }

        /**
         * 根据加权索引选择 URL
         */
        private String getUrlByWeightedIndex(int index) {
            int currentWeight = 0;
            for (ProxyApiUrl server : servers) {
                currentWeight += server.getWeight();
                if (index < currentWeight) {
                    return server.getUrl();
                }
            }

            // 正常情况下不会执行到这里
            return servers.get(0).getUrl();
        }
    }
}
