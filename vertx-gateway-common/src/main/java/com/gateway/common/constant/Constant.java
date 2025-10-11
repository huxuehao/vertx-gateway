package com.gateway.common.constant;

import java.util.UUID;

/**
 * 描述：常量
 *
 * @author huxuehao
 **/
public class Constant {
    /* token秘钥 */
    public static final String JWT_SECRET = "120606C95258FF3A3ED51603A683C8B1";
    public static final String LOGIN_USER_KEY = "LOGIN-USER-KEY";
    public static final String API_AUTH_CACHE_PRE = "vertx:gateway:user:api:auth:code:";
    /* token存活时间（ms）*/
    public static final Long TOKEN_LIVE_TIME = 10800000L;
    public static final Integer LOG_SAVE_DAYS = 30;
    public static final String SALT = "33453459278276B306C3E5E05A371FF9";
    public static final String VERTX_GATEWAY_ID = "VERTX_GATEWAY_ID";
    public static final String VERTX_GATEWAY_DEFAULT_ID = UUID.randomUUID().toString().replace("-","");
}
