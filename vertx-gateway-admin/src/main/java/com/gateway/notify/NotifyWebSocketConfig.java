package com.gateway.notify;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 描述：通知ws配置
 *
 * @author huxuehao
 **/
@Configuration
@EnableWebSocket
public class NotifyWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotifyWebSocketHandler(), "/ws/notify")
                .setAllowedOrigins("*");
    }
}
