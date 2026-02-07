package com.hxh.gateway.core.cluster.config;

import com.alibaba.fastjson2.JSON;
import com.hxh.gateway.common.config.GatewayEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.hxh.gateway.common.constant.ChannelConstant.*;

/**
 * 描述：网管事件监听器
 *
 * @author huxuehao
 **/
@Component
public class GatewayEventListener {
    private final StringRedisTemplate stringRedisTemplate;

    public GatewayEventListener(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @EventListener
    public void listenerGatewayEvent(GatewayEvent event) {
        switch (event.getChannel()) {
            case ONLINE_APP_CHANNEL:
                stringRedisTemplate.convertAndSend(ONLINE_APP_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case OFFLINE_APP_CHANNEL:
                stringRedisTemplate.convertAndSend(OFFLINE_APP_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case ONLINE_API_CHANNEL:
                stringRedisTemplate.convertAndSend(ONLINE_API_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case OFFLINE_API_CHANNEL:
                stringRedisTemplate.convertAndSend(OFFLINE_API_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case RESET_API_CHANNEL:
                stringRedisTemplate.convertAndSend(RESET_API_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case ONLINE_CLIENT_CHANNEL:
                stringRedisTemplate.convertAndSend(ONLINE_CLIENT_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case OFFLINE_CLIENT_CHANNEL:
                stringRedisTemplate.convertAndSend(OFFLINE_CLIENT_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case ONLINE_WHITE_IP_CHANNEL:
                stringRedisTemplate.convertAndSend(ONLINE_WHITE_IP_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            case OFFLINE_WHITE_IP_CHANNEL:
                stringRedisTemplate.convertAndSend(OFFLINE_WHITE_IP_CHANNEL, JSON.toJSONString(event.getBody()));
                break;
            default:
                break;
        }
    }
}
