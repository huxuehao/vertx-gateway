package com.gateway.common.option;

import com.gateway.common.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 描述：接口配置
 *
 * @author huxuehao
 **/
@Setter
@Getter
public class ApiConfig {
    /*
     * 权限认证
     */
    //安全认证类型（JWT、NONE）
    private ApiAuthType authType;
    // 认证请求头名称, 例如 VG-AUTH
    private String authHeaderName;

    /*
     * 访问限制
     */
    // 访问限制类型 (DAY、HOUR、MINUTE、NONE)
    private ApiAccessLimitType accessLimitType;
    // 路由访问次数
    private Integer routerTimes;
    // IP访问次数
    private Integer ipTimes;

    /*
     * 路由配置
     */
    // 请求地址
    private String routePath;
    // 请求方法
    private ApiHttpMethod routeMethod;
    // 请求的ContentType，支持正则
    private List<String> routeContentType;
    // 请求参数（query、path、header参数，以及Body是向下传递的）
    private List<RouteParam> routeParams;

    /*
     * 代理接口
     */
    // 代理服务类型(HTTP、JUMP—页面跳转)
    private ApiProxyApiType proxyApiType;
    // 代理接口的方法（POST、GET、PUT等）
    private ApiHttpMethod proxyApiMethod;
    // 代理跳转地址
    private String proxyRedirectUrl;
    // 代理接口url
    private List<ProxyApiUrl> proxyApiUrls;
    // 负载均衡策略（POLLING-轮询、HASH-哈希）
    private ApiBalancingType loadBalancingStrategy;
    // 代理接口常量参数
    private List<ProxyConstParam> proxyConstParams;
    // 代理超时时间(ms)
    private Long proxyTimeoutTime;
    // 是否开启重试
    private Boolean proxyRetry;
    // 重试次数
    private Integer proxyRetryTimes;
    // 重试时间间隔(ms)
    private Long proxyRetryInterval;
}
