package com.gateway.core.api.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gateway.common.constant.GatewayRouteConst;
import com.gateway.common.dto.GatewayClientDto;
import com.gateway.common.entity.GatewayApiLog;
import com.gateway.common.entity.GatewayClientAuth;
import com.gateway.common.enums.ApiAuthType;
import com.gateway.common.option.ApiConfig;
import com.gateway.common.util.Func;
import com.gateway.core.api.RouteHandler;
import com.gateway.core.api.ClientOnlineService;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：路由认证处理器
 *
 * @author huxuehao
 **/
public class RouteAuthHandler extends RouteHandler {
    @Override
    public void handle(RoutingContext rct) {
        // 字段有效性检查
        if (checkFieldIsNull()) {
            endRoutingFieldNull(rct);
            return;
        }

        ApiConfig config = apiOption.getConfig();
        if (ApiAuthType.NONE == config.getAuthType()) {
            rct.next();
            return;
        }

        String token = rct.request().getHeader(config.getAuthHeaderName());
        if (Func.isEmpty(token)) {
            endRouting(rct, 401, "缺少必要的访问凭证");
            return;
        }

        // token解码
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.decode(token);
        } catch (JWTDecodeException e) {
            endRouting(rct, 401, "访问凭证格式有误");
            return;
        }

        // 客户端编号
        String clientCode = decodedJWT.getClaim(GatewayRouteConst.TOKEN_CLAIM_KEY).asString();
        // 验证客户端信息
        GatewayClientDto client = ClientOnlineService.obtainClient(clientCode);
        if (client == null) {
            endRouting(rct, 401, "授权客户端不存在");
            return;
        }

        // 验证客户端的有效性
        if (clientNotValidity(client)) {
            endRouting(rct, 401, "授权客户端无效");
            return;
        }

        // 验证token过期时间
        long expiresTime = decodedJWT.getExpiresAt().getTime();
        if (expiresTime < System.currentTimeMillis()) {
            endRouting(rct, 401, "访问凭证无效");
            return;
        }

        // 验证接口权限
        List<GatewayClientAuth> apiAuth = client.getAuths();
        List<Long> authIds = apiAuth.stream().map(GatewayClientAuth::getAuthId).collect(Collectors.toList());
        if (!authIds.contains(apiOption.getId())) {
            endRouting(rct, 401, "访问凭证无权访问此路由");
            return;
        }

        // 验证token秘钥
        String secretKey = client.getTokenSecret();
        JWTVerifier verifier=JWT.require(Algorithm.HMAC256(secretKey)).build();
        try {
            verifier.verify(token); // token验证
            rct.next();
        } catch(Exception e) {
            endRouting(rct, 401, "访问凭证验证失败");
        }
    }

    /**
     * 验证客户端是否无效
     */
    public static boolean clientNotValidity(GatewayClientDto client) {
        if (client.getOnline() != 1) {
            return true;
        }
        Date clientTtl = client.getClientTtl();
        if (clientTtl == null) {
            return true;
        }
        return System.currentTimeMillis() > clientTtl.getTime();
    }
}
