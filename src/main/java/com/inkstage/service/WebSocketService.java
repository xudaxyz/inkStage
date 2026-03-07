package com.inkstage.service;

/**
 * WebSocket服务
 */
public interface WebSocketService {

    /**
     * 向指定用户发送通知
     */
    void sendNotificationToUser(Long userId, Object notification);

    /**
     * 向所有用户广播通知
     */
    void broadcastNotification(Object notification);

    /**
     * 向用户发送未读通知数量
     */
    void sendUnreadCountToUser(Long userId, int count);
}
