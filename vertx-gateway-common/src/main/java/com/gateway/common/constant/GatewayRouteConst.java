package com.gateway.common.constant;

/**
 * 描述：网关路由常量
 *
 * @author huxuehao
 **/
public class GatewayRouteConst {
    public static String QUERY_PARAMS_KEY = "RouterQueryParamsKey";
    public static String PATH_PARAMS_KEY = "RouterPathParamsKey";
    public static String HEADER_PARAMS_KEY = "RouterHeaderParamsKey";
    public static String API_LOG_BODY = "VertxGatewayApiLogBody";

    public static Integer TOKEN_SERVER_PORT = 5060;
    public static String TOKEN_SERVER_PATH = "/oauth/accessToken";
    public static String TOKEN_PARAM_KEY = "clientCode";
    public static String TOKEN_ISSUER = "vertx.gateway.token_issuer";
    public static String TOKEN_CLAIM_KEY = "vertx.gateway.token_claim_key";
}
