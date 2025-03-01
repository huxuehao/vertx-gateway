package com.gateway.notify;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述：通知处理器
 *
 * @author huxuehao
 **/
public class NotifyWebSocketHandler extends TextWebSocketHandler {
    // 使用线程安全的Map存储用户会话
    private static final ConcurrentMap<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从请求参数获取用户ID
        String userId = getUserIdFromRequest(session);

        // 存储会话
        userSessions.compute(userId, (key, set) -> {
            if (set == null) {
                Set<WebSocketSession> newSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
                newSet.add(session);
                return newSet;
            }
            set.add(session);
            return set;
        });
    }

    private String getUserIdFromRequest(WebSocketSession session) {
        // 从WebSocket连接参数获取用户ID
        URI uri = session.getUri();
        if (uri == null) return "unknown";

        String query = uri.getQuery();
        if (query == null) return "unknown";

        return Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("userId="))
                .findFirst()
                .map(param -> param.split("=")[1])
                .orElse("unknown");
    }

    // 定向发送方法
    public static void sendToUsers(NotifyMessage notifyMessage) {
        TextMessage msg = new TextMessage(JSON.toJSONString(notifyMessage));
        notifyMessage.getUsers().forEach(userId -> {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.removeIf(s -> !s.isOpen()); // 清理无效会话
                sessions.forEach(session -> {
                    try {
                        session.sendMessage(msg);
                    } catch (IOException e) {
                        // 处理异常
                    }
                });
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 移除关闭的会话
        String userId = getUserIdFromRequest(session);
        userSessions.computeIfPresent(userId, (key, set) -> {
            set.remove(session);
            return set.isEmpty() ? null : set;
        });
    }
}
