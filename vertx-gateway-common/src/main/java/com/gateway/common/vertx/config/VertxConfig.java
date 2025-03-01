package com.gateway.common.vertx.config;

import com.gateway.common.dto.GatewayClientDto;
import com.gateway.common.entity.GatewayWhiteList;
import com.gateway.common.option.GatewayApiOption;
import com.gateway.common.vertx.codec.GatewayApiOptionCodec;
import com.gateway.common.vertx.codec.GatewayAppOptionCodec;
import com.gateway.common.option.GatewayAppOption;
import com.gateway.common.vertx.codec.GatewayClientDtoCodec;
import com.gateway.common.vertx.codec.GatewayWhiteListCodec;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：vertx配置类
 *
 * @author huxuehao
 **/
@Configuration
public class VertxConfig {
    @Bean
    public Vertx vertx() {
        Vertx vertx = Vertx.vertx();

        // 注册自定义解码器
        vertx.eventBus().registerDefaultCodec(GatewayAppOption.class, new GatewayAppOptionCodec());
        vertx.eventBus().registerDefaultCodec(GatewayApiOption.class, new GatewayApiOptionCodec());
        vertx.eventBus().registerDefaultCodec(GatewayClientDto.class, new GatewayClientDtoCodec());
        vertx.eventBus().registerDefaultCodec(GatewayWhiteList.class, new GatewayWhiteListCodec());

        return vertx;
    }
}
