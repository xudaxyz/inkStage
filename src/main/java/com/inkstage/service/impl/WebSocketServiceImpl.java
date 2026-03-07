package com.inkstage.service.impl;

import com.inkstage.controller.WebSocketController;
import com.inkstage.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebSocket服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private final WebSocketController webSocketController;

    @Override
    public void sendNotificationToUser(Long userId, Object notification) {
        log.info("向用户发送通知: {}, 通知: {}", userId, notification);
        webSocketController.sendToUser(userId, "/notification/new", notification);
    }

    @Override
    public void broadcastNotification(Object notification) {
        log.info("广播通知: {}", notification);
        webSocketController.broadcast("/topic/notification", notification);
    }

    @Override
    public void sendUnreadCountToUser(Long userId, int count) {
        log.info("向用户发送未读通知数量: {}, 数量: {}", userId, count);
        webSocketController.sendToUser(userId, "/notification/unread-count", count);
    }
}
