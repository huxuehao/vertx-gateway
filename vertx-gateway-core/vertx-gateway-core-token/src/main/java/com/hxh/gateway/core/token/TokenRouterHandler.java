package com.hxh.gateway.core.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hxh.gateway.common.constant.GatewayRouteConst;
import com.hxh.gateway.common.dto.GatewayClientDto;
import com.hxh.gateway.common.errorresponse.CommErrorResponse;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.core.api.ClientOnlineService;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

/**
 * 描述：Token服务处理器
 *
 * @author huxuehao
 **/
public class TokenRouterHandler{
    public void getToken(RoutingContext rct) {
        HttpServerResponse response = rct.response().putHeader("Content-Type", "application/json;charset=UTF-8");
        String clientCode = rct.request().getParam(GatewayRouteConst.TOKEN_PARAM_KEY);
        if (Func.isEmpty(clientCode)) {
            response.end(new CommErrorResponse(400, "缺少必要请求参数clientCode").toJsonString());
            return;
        }


        GatewayClientDto clientDto = ClientOnlineService.obtainClient(clientCode);
        if (clientDto == null) {
            response.end(new CommErrorResponse(401, "请求参数clientCode为无效参数").toJsonString());
            return;
        }

        if (clientNotValidity(clientDto)) {
            response.end(new CommErrorResponse(401, "无效客户端").toJsonString());
            return;
        }

        String token = genToken(clientDto.getClientCode(), clientDto.getTokenTtl(), clientDto.getTokenSecret());
        JsonObject result = new JsonObject();
        result.put("code", 200);
        result.put("token", token);
        result.put("ttl", clientDto.getTokenTtl());
        response.end(result.toString());
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

    /**
     * 生成token
     * @param clientCode  客户端编号
     * @param tokenTTL    token有效期
     * @param tokenSecret token秘钥
     */
    public String genToken(String clientCode, long tokenTTL, String tokenSecret) {
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        return JWT.create()
                .withIssuer(GatewayRouteConst.TOKEN_ISSUER) // 发布者
                .withIssuedAt(new Date()) // 生成签名的时间
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenTTL)) // 生产签名的有效期
                .withClaim(GatewayRouteConst.TOKEN_CLAIM_KEY, clientCode) // 插入数据
                .sign(algorithm);
    }
}
