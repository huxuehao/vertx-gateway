package com.gateway.core.cluster.config;

import com.alibaba.fastjson2.JSON;
import com.gateway.common.config.Identity;
import com.gateway.common.config.RedisSubscribeBody;
import com.gateway.core.cluster.trigger.ApiTrigger;
import com.gateway.core.cluster.trigger.ApplicationTrigger;
import com.gateway.core.cluster.trigger.ClientTrigger;
import com.gateway.core.cluster.trigger.WhiteIpTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.ArrayList;
import static com.gateway.common.constant.ChannelConstant.*;

/**
 * 描述：redis 的订阅配置
 *
 * @author huxuehao
 **/
@Configuration
public class RedisSubscribeConfig {
    private final Logger logger = LoggerFactory.getLogger(RedisSubscribeConfig.class);

    private final ApiTrigger apiTrigger;
    private final ApplicationTrigger applicationTrigger;
    private final ClientTrigger clientTrigger;
    private final WhiteIpTrigger whiteIpTrigger;

    public RedisSubscribeConfig(ApiTrigger apiTrigger, ApplicationTrigger applicationTrigger, ClientTrigger clientTrigger, WhiteIpTrigger whiteIpTrigger) {
        this.apiTrigger = apiTrigger;
        this.applicationTrigger = applicationTrigger;
        this.clientTrigger = clientTrigger;
        this.whiteIpTrigger = whiteIpTrigger;
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        /* 定义订阅通道的集合 */
        ArrayList<Topic> topicList = new ArrayList<>();
        /* 全量匹配 */
        topicList.add(new ChannelTopic(ONLINE_APP_CHANNEL));
        topicList.add(new ChannelTopic(OFFLINE_APP_CHANNEL));
        topicList.add(new ChannelTopic(ONLINE_API_CHANNEL));
        topicList.add(new ChannelTopic(OFFLINE_API_CHANNEL));
        topicList.add(new ChannelTopic(RESET_API_CHANNEL));
        topicList.add(new ChannelTopic(ONLINE_CLIENT_CHANNEL));
        topicList.add(new ChannelTopic(OFFLINE_CLIENT_CHANNEL));
        topicList.add(new ChannelTopic(ONLINE_WHITE_IP_CHANNEL));
        topicList.add(new ChannelTopic(OFFLINE_WHITE_IP_CHANNEL));
        /* 添加监听者和主体集合 */
        container.addMessageListener(this.messageListener(), topicList);

        return container;
    }

    /* redis频道（消息）的监听者（接收者）*/
    @Bean
    public MessageListener messageListener() {
        /* 接收消息, 并根据channel的名称进行任务调度触发器 */
        return (message, pattern) -> {
            // 解析消息对象
            RedisSubscribeBody body;
            try {
                body = JSON.parseObject(new String(message.getBody()), RedisSubscribeBody.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 认证是否是自己发给自己的
            if (Identity.uuid.equals(body.getUuid())) {
                return;
            }

            // 获取通道
            String channel = new String(message.getChannel());
            logger.info("接收到了节点的广播消息，channel为：{}", channel);

            switch (channel) {
                case ONLINE_APP_CHANNEL:
                    applicationTrigger.online(body.getIds());
                    break;
                case OFFLINE_APP_CHANNEL:
                    applicationTrigger.offline(body.getIds());
                    break;
                case ONLINE_API_CHANNEL:
                    apiTrigger.online(body.getIds());
                    break;
                case OFFLINE_API_CHANNEL:
                    apiTrigger.offline(body.getIds());
                    break;
                case RESET_API_CHANNEL:
                    apiTrigger.reset(body.getIds());
                    break;
                case ONLINE_CLIENT_CHANNEL:
                    clientTrigger.online(body.getIds());
                    break;
                case OFFLINE_CLIENT_CHANNEL:
                    clientTrigger.offline(body.getRemark());
                    break;
                case ONLINE_WHITE_IP_CHANNEL:
                    whiteIpTrigger.online(body.getIds());
                    break;
                case OFFLINE_WHITE_IP_CHANNEL:
                    whiteIpTrigger.offline(body.getEntity());
                    break;
                default:
                    logger.error("通道channel [{}] 匹配失败", channel);
                    break;
            }
        };
    }
}
