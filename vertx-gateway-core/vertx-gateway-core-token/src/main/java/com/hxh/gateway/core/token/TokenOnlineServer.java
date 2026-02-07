package com.hxh.gateway.core.token;

import com.hxh.gateway.common.constant.GatewayRouteConst;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 描述：Token服务
 *
 * @author huxuehao
 **/
public class TokenOnlineServer extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(TokenOnlineServer.class);
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route(GatewayRouteConst.TOKEN_SERVER_PATH).handler( new TokenRouterHandler()::getToken);
        // 启动token服务
        vertx.createHttpServer().requestHandler(router).listen(GatewayRouteConst.TOKEN_SERVER_PORT,res -> {
            if (res.succeeded()) {
                System.out.println("*****************************************************************************************");
                System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                System.out.println("* * * * * * * * * * * * TOKEN 服务部署成功 * * * * *  * * * * * * * * * * * * * * * * * * *");
                System.out.println("* * * * * * * * * * * * method: get * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                System.out.println("* * * * * * * * * * * * port:   5060 * * * * * * * * * * * * * * * * * * * *  * * * * * *");
                System.out.println("* * * * * * * * * * * * path:   /oauth/accessToken?clientCode=xxxxx * * * * * * * * * * *");
                System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                System.out.println("*****************************************************************************************");
                startPromise.complete();
            } else {
                logger.error("==>Token服务部署失败");
                startPromise.fail(res.cause());
            }
        });
    }
}
