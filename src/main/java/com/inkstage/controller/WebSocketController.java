package com.inkstage.controller;

import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket消息控制器
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 处理客户端连接
     */
    @MessageMapping("/connect")
    public void handleConnect(@Payload String message) {
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            log.info("用户WebSocket连接: {}", userId);
            // 可以在这里处理连接逻辑，例如记录连接状态
        }
    }

    /**
     * 向指定用户发送消息
     */
    public void sendToUser(Long userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                payload
        );
    }

    /**
     * 向所有用户广播消息
     */
    public void broadcast(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}
