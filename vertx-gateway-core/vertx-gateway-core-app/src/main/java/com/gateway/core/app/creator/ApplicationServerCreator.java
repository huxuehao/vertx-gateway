package com.gateway.core.app.creator;

import com.gateway.common.enums.AppCertType;
import com.gateway.common.enums.AppProtocolType;
import com.gateway.common.errorresponse.CommErrorResponse;
import com.gateway.common.option.AppConfig;
import com.gateway.common.option.GatewayAppOption;
import com.gateway.common.util.Func;
import com.gateway.common.errorresponse.NotFoundResponse;
import com.gateway.common.util.WebUtil;
import com.gateway.core.app.WhiteListOnlineService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述：网关应用部署
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class ApplicationServerCreator {
    private static final Logger logger = LogManager.getLogger(ApplicationServerCreator.class);
    private final Vertx vertx;
    private final GatewayAppOption appOption;
    private HttpClient httpClient;
    private final Router router;

    public ApplicationServerCreator(Vertx vertx, GatewayAppOption appOption) {
        this.vertx = vertx;
        this.appOption = appOption;
        // 创建httpClient
        createHttpClient();
        // 创建路由
        router = Router.router(vertx);
        // 初始化路由
        initRouter(router);

    }

    /**
     * 创建Http服务
     */
    public void createHttp(Handler<AsyncResult<Void>> handler) {
        AppConfig config = appOption.getConfig();
        // 成立条件：没有配置了http协议
        if (AppProtocolType.HTTP != config.getProtocol()) {
            handler.handle(Future.succeededFuture());
        }

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        // 条件成立：当前系统是linux，开启epoll
        if (vertx.isNativeTransportEnabled()) {
            httpServerOptions
                    .setTcpFastOpen(true)
                    .setTcpCork(true)
                    .setTcpQuickAck(true)
                    .setReusePort(true);
        }

        // 创建http服务，用于注册应用下的API
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(config.getPort(), res -> {
                    if (res.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(Future.failedFuture(res.cause()));
                    }
                });
    }

    /**
     * 创建Https服务
     */
    public void createHttps(Handler<AsyncResult<Void>> handler) {
        AppConfig config = appOption.getConfig();
        // 成立条件：没有配置了http协议
        if (AppProtocolType.HTTPS != config.getProtocol()) {
            handler.handle(Future.succeededFuture());
        }

        // 验证证书是否存在
        vertx.fileSystem().exists(config.getCertificatePath(), res -> {
            if (res.failed()) {
                logger.error("证书[" + config.getCertificatePath() +"]不存在");
                handler.handle(Future.failedFuture("证书[" + config.getCertificatePath() +"]不存在"));
                return;
            }

            // 创建http服务，用于注册应用下的API
            HttpServerOptions httpServerOptions = new HttpServerOptions();
            httpServerOptions.setSsl(true);
            if (AppCertType.PEM == config.getCertificateType()) {
                httpServerOptions.setPemKeyCertOptions(new PemKeyCertOptions().setCertPath(config.getCertificatePath()).setKeyPath(config.getCertificateKey()));
            } else if (AppCertType.PFX == config.getCertificateType()) {
                httpServerOptions.setPfxKeyCertOptions(new PfxOptions().setPath(config.getCertificatePath()).setPassword(config.getCertificateKey()));
            } else {
                logger.error("https服务创建失败，只支持pem/pfx类型证书");
                handler.handle(Future.failedFuture("https服务创建失败，只支持pem/pfx类型证书"));
            }

            // 条件成立：当前系统是linux，开启epoll
            if (vertx.isNativeTransportEnabled()) {
                httpServerOptions
                        .setTcpFastOpen(true)
                        .setTcpCork(true)
                        .setTcpQuickAck(true)
                        .setReusePort(true);
            }

            // 创建http服务，用于注册应用下的API
            vertx.createHttpServer(httpServerOptions)
                    .requestHandler(router)
                    .listen(config.getPort(), createRes -> {
                        if (createRes.succeeded()) {
                            handler.handle(Future.succeededFuture());
                        } else {
                            handler.handle(Future.failedFuture(createRes.cause()));
                        }
                    });
        });
    }

    /**
     * 创建http客户端
     */
    private void createHttpClient() {
        AppConfig config = appOption.getConfig();

        // 获取http客户端，将使用期与代理接口进行网络通信
        HttpClientOptions httpClientOptions = new HttpClientOptions()
                .setDecoderInitialBufferSize(config.getDecoderInitialBufferSize())
                .setMaxPoolSize(config.getMaxPoolSize())
                .setMaxInitialLineLength(config.getMaxInitialLineLength())
                .setMaxHeaderSize(config.getMaxHeaderSize())
                .setKeepAlive(config.isKeepAlive());
        // 如果在linux系统开启epoll，开启epoll机制可以提高I/O多路复用的效率，适用于处理大量并发连接的场景
        if (vertx.isNativeTransportEnabled()) {
            httpClientOptions
                    .setTcpFastOpen(true)
                    .setTcpCork(true)
                    .setTcpQuickAck(true)
                    .setReusePort(true);
        }
        httpClient = vertx.createHttpClient(httpClientOptions);
    }

    /**
     * 初始化路由
     * @param router 路由实例
     */
    private void initRouter(Router router) {
        /*
         * 添加一个空路由（该应用下的所有请求都会经过），作为白名处理器
         */
        router.route().handler(this::whiteListHandler);

        // 配置session存储
        SessionStore sessionStore;
        if (vertx.isClustered()) {
            sessionStore = ClusteredSessionStore.create(vertx);
        } else {
            sessionStore = LocalSessionStore.create(vertx);
        }

        AppConfig config = appOption.getConfig();
        SessionHandler sessionHandler = SessionHandler.create(sessionStore)
                .setSessionCookieName(config.getSessionCookieName())
                .setSessionTimeout(config.getSessionTimeOut());
        /*
         * 添加一个空路由（该应用下的所有请求都会经过），作为session处理器
         */
        router.route().handler(sessionHandler);

        // 成立条件：未开启跨域
        if (!config.getCorsOpen()) {
            return;
        }

        // 配置跨域
        CorsHandler corsHandler = CorsHandler.create();

        if (Func.isNotEmpty(config.getAllowedOrigin())) {
            String allowedOrigin = config.getAllowedOrigin();
            if ("*".equals(allowedOrigin)) {
                allowedOrigin = ".*";
            }
            corsHandler.addRelativeOrigin(allowedOrigin);
        } else {
            corsHandler.addRelativeOrigin(".*");
        }

        if (Func.isNotEmpty(config.getAllowCredentials())) {
            corsHandler.allowCredentials(config.getAllowCredentials());
        }

        if (Func.isNotEmpty(config.getMaxAgeSeconds())) {
            corsHandler.maxAgeSeconds(config.getMaxAgeSeconds());
        } else {
            corsHandler.maxAgeSeconds(60);
        }

        if (Func.isNotEmpty(config.getAllowedMethods())) {
            Set<HttpMethod> allowedMethods = new HashSet<>();
            for (String method : config.getAllowedMethods()) {
                allowedMethods.add(new HttpMethod(method.toUpperCase()));
            }
            corsHandler.allowedMethods(allowedMethods);
        } else {
            corsHandler.allowedMethods(new HashSet<HttpMethod>() {{
                add(HttpMethod.GET);
                add(HttpMethod.POST);
            }});
        }
        /*
         * 添加一个空路由（该应用下的所有请求都会经过），作为跨域处理器
         */
        router.route().handler(corsHandler);

        /*
         * 检查请求体的Content-Length
         */
        router.route().handler((rct) -> {
            if (config.getContentLength() <= 0) {
                rct.next();
                return;
            }
            long contentLength = rct.request().getHeader("Content-Length") == null
                    ? 0L
                    : new Long((rct.request().getHeader("Content-Length")));
            if (contentLength > config.getContentLength()) {
                HttpServerResponse response = rct.response();
                response.putHeader("Server", "Vertx-Gateway");
                response.putHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                response.end(new CommErrorResponse(500, "Content-Length 超过应用配置上线").toJsonString());
                return;
            }
            rct.next();
        });

        /*
         * 添加404路由
         */
        router.route().order(Integer.MAX_VALUE).handler(rct -> {
            if (logger.isDebugEnabled()) {
                logger.debug("请求路径不存在: {}:{}", rct.request().method(), rct.request().path());
            }
            HttpServerResponse response = rct.response();
            response.putHeader("Server", "Vertx-Gateway");
            response.putHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            response.end(new NotFoundResponse().toJsonString());
        });
    }

    /**
     * 白名单处理器
     */
    private void whiteListHandler(RoutingContext rct) {
        Set<String> whiteIpList = WhiteListOnlineService.obtainWhiteList(appOption.getId());
        if (whiteIpList == null || whiteIpList.isEmpty()) {
            rct.next();
            return;
        }
        String ip = WebUtil.getIP(rct.request());
        if (whiteIpList.contains(ip)) {
            rct.next();
            return;
        }

        HttpServerResponse response = rct.response();
        response.putHeader("Server", "Vertx-Gateway");
        response.putHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.end(new CommErrorResponse(401, "当前IP无权访问").toJsonString());
    }
}
