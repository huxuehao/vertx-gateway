package com.gateway.common.config;

import org.springframework.context.ApplicationEvent;

/**
 * 描述：网关事件
 *
 * @author huxuehao
 **/
public class GatewayEvent  extends ApplicationEvent {
    private static final long serialVersionUID = -1L;
    private String channel;
    private RedisSubscribeBody body;

    public GatewayEvent(Object source, String channel, RedisSubscribeBody body) {
        super(source);
        this.channel = channel;
        this.body = body;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public RedisSubscribeBody getBody() {
        return body;
    }

    public void setBody(RedisSubscribeBody body) {
        this.body = body;
    }
}
