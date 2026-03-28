package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.*;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.service.NotificationService;
import com.inkstage.service.NotificationSettingService;
import com.inkstage.service.NotificationTemplateService;
import com.inkstage.service.WebSocketService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationProducer notificationProducer;
    private final NotificationCacheService notificationCacheService;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationSettingService notificationSettingService;
    private final WebSocketService webSocketService;

    @Override
    public List<Notification> getNotificationList(Long userId, NotificationType type) {
        if (type == null) {
            return notificationMapper.selectByUserId(userId);
        } else {
            return notificationMapper.selectByUserIdAndType(userId, type);
        }
    }

    @Override
    public boolean markAsRead(Long notificationId) {
        int result = notificationMapper.updateReadStatus(notificationId, ReadStatus.READ);
        if (result > 0) {
            Notification notification = notificationMapper.selectById(notificationId);
            if (notification != null) {
                notificationCacheService.decrementUnreadCount(notification.getUserId());
                int unreadCount = notificationCacheService.getUnreadCount(notification.getUserId());
                webSocketService.sendUnreadCountToUser(notification.getUserId(), unreadCount);
            }
        }
        return result > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        int result = notificationMapper.updateAllReadStatus(userId, ReadStatus.READ);
        if (result > 0) {
            notificationCacheService.resetUnreadCount(userId);
            webSocketService.sendUnreadCountToUser(userId, 0);
        }
        return result > 0;
    }

    @Override
    public boolean deleteNotification(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        int result = notificationMapper.deleteById(notificationId);
        if (result > 0 && notification != null && ReadStatus.UNREAD.equals(notification.getReadStatus())) {
            notificationCacheService.decrementUnreadCount(notification.getUserId());
            int unreadCount = notificationCacheService.getUnreadCount(notification.getUserId());
            webSocketService.sendUnreadCountToUser(notification.getUserId(), unreadCount);
        }
        return result > 0;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        // 构建通知消息DTO
        NotificationMessageDTO message = buildMessageDTO(notification);

        // 发送到RabbitMQ
        notificationProducer.sendNotification(message);
        return true;
    }

    /**
     * 获取通知类型对应的设置键
     *
     * @param type 通知类型
     * @return 设置键
     */
    private String getSettingKey(NotificationType type) {
        return switch (type) {
            case ARTICLE_LIKE -> "articleLike";
            case ARTICLE_PUBLISH -> "articlePublish";
            case ARTICLE_COLLECTION -> "articleCollection";
            case COMMENT_REPLY -> "commentReply";
            case ARTICLE_COMMENT -> "articleComment";
            case COMMENT_LIKE -> "commentLike";
            case FOLLOW -> "follow";
            case REPORT -> "report";
            case MESSAGE -> "message";
            case FEEDBACK -> "feedback";
            case SYSTEM, USER_STATUS_CHANGE, ARTICLE_REVIEW_REPROCESS, ARTICLE_TOP, ARTICLE_RECOMMEND, ARTICLE_DELETE,
                 ARTICLE_ONLINE, ARTICLE_OFFLINE, TAG_DELETE, ARTICLE_REVIEW_REJECT, COMMENT_REVIEW_REJECT,
                 COMMENT_TOP -> "system";
        };
    }

    /**
     * 检查用户是否开启了该类型的通知
     *
     * @param userId 用户ID
     * @param type   通知类型
     * @return 是否开启
     */
    private boolean isNotificationDisabled(Long userId, NotificationType type) {
        String settingKey = getSettingKey(type);
        boolean enabled = notificationSettingService.isNotificationEnabled(userId, settingKey);
        if (!enabled) {
            log.info("用户 ID {} 已关闭 {} 类型的通知", userId, type.getDesc());
        }
        return !enabled;
    }

    /**
     * 使用模板生成通知内容
     *
     * @param type      通知类型
     * @param relatedId 关联ID
     * @param params    模板参数
     * @return 通知内容对象
     */
    private NotificationContent generateContentWithTemplate(NotificationType type, Long relatedId, Object... params) {
        String title = notificationTemplateService.generateTitle(type, params);
        String content = notificationTemplateService.generateContent(type, params);
        String actionUrl = notificationTemplateService.generateActionUrl(type, relatedId);
        return new NotificationContent(title, content, actionUrl);
    }

    /**
     * 构建通知对象
     *
     * @param userId    用户ID
     * @param type      通知类型
     * @param relatedId 关联ID
     * @param senderId  发送者ID
     * @param content   通知内容
     * @return 通知对象
     */
    private Notification buildNotification(Long userId, NotificationType type, Long relatedId,
                                          Long senderId, NotificationContent content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(content.title());
        notification.setContent(content.content());
        notification.setRelatedId(relatedId);
        notification.setPriority(Priority.NORMAL);
        notification.setSenderId(senderId);
        notification.setActionUrl(content.actionUrl());
        return notification;
    }

    /**
     * 构建通知消息DTO
     *
     * @param notification 通知对象
     * @return 通知消息DTO
     */
    private NotificationMessageDTO buildMessageDTO(Notification notification) {
        NotificationMessageDTO message = new NotificationMessageDTO();
        message.setUserId(notification.getUserId());
        message.setContent(notification.getContent());
        message.setType(notification.getType());
        message.setRelatedId(notification.getRelatedId());
        message.setSenderId(notification.getSenderId());
        message.setRelatedType(notification.getRelatedType());
        message.setActionUrl(notification.getActionUrl());
        message.setExtraData(notification.getExtraData());
        message.setTitle(notification.getTitle());
        return message;
    }

    /**
     * 通知内容记录类
     */
    private record NotificationContent(String title, String content, String actionUrl) {}

    @Override
    public boolean sendNotificationWithTemplate(Long userId, NotificationType type, Long relatedId, Long senderId, Object... params) {
        // 检查用户是否开启了该类型的通知
        if (isNotificationDisabled(userId, type)) {
            return false;
        }

        // 使用模板生成通知内容
        NotificationContent content = generateContentWithTemplate(type, relatedId, params);

        // 构建通知对象
        Notification notification = buildNotification(userId, type, relatedId, senderId, content);

        // 发送通知
        return sendNotification(notification);
    }

    @Override
    public boolean sendBatchNotifications(List<Notification> notifications) {
        try {
            List<NotificationMessageDTO> messages = new ArrayList<>();
            for (Notification notification : notifications) {
                // 检查用户是否开启了该类型的通知
                if (isNotificationDisabled(notification.getUserId(), notification.getType())) {
                    continue;
                }

                // 构建通知消息DTO
                NotificationMessageDTO message = buildMessageDTO(notification);
                messages.add(message);
            }

            // 批量发送到RabbitMQ
            if (!messages.isEmpty()) {
                notificationProducer.sendBatchNotifications(messages);
            }

            return true;
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
            return false;
        }
    }

    @Override
    public boolean sendBatchNotificationsWithTemplate(List<Map<String, Object>> notificationDataList) {
        try {
            List<Notification> notifications = new ArrayList<>();

            for (Map<String, Object> data : notificationDataList) {
                Long userId = (Long) data.get("userId");
                NotificationType type = (NotificationType) data.get("type");
                Long relatedId = (Long) data.get("relatedId");
                Long senderId = (Long) data.get("senderId");
                Object[] params = (Object[]) data.getOrDefault("params", new Object[0]);

                // 检查用户是否开启了该类型的通知
                if (isNotificationDisabled(userId, type)) {
                    continue;
                }

                // 使用模板生成通知内容
                NotificationContent content = generateContentWithTemplate(type, relatedId, params);

                // 构建通知对象
                Notification notification = buildNotification(userId, type, relatedId, senderId, content);

                notifications.add(notification);
            }

            // 批量发送通知
            return sendBatchNotifications(notifications);
        } catch (Exception e) {
            log.error("批量发送带模板的通知失败", e);
            return false;
        }
    }

    @Override
    public int getUnreadCount(Long userId) {
        // 尝试从缓存获取
        Integer cachedCount = notificationCacheService.getCachedUnreadCount(userId);
        if (cachedCount != null) {
            return cachedCount;
        }
        // 从数据库查询（处理缓存穿透）
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        // 更新缓存
        notificationCacheService.cacheUnreadCount(userId, unreadCount);
        return unreadCount;
    }

    @Override
    public void syncUnreadCount(Long userId) {
        // 从数据库查询实际未读数量
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        // 更新缓存
        notificationCacheService.batchUpdateUnreadCount(userId, unreadCount);
    }

    @Override
    public PageResult<Notification> getNotificationListWithPage(NotificationType type, Integer pageNum, Integer pageSize) {
        List<Notification> notifications;
        int total;
        Long userId = UserContext.getCurrentUserId();

        int offset = (pageNum - 1) * pageSize;

        if (type == null) {
            notifications = notificationMapper.selectByUserIdWithPage(userId, offset, pageSize);
            total = notificationMapper.countByUserId(userId);
        } else {
            notifications = notificationMapper.selectByUserIdAndTypeWithPage(userId, type, offset, pageSize);
            total = notificationMapper.countByUserIdAndType(userId, type);
        }

        return PageResult.build(notifications, (long) total, pageNum, pageSize);
    }

    @Override
    public PageResult<Notification> getNotificationsByCategory(Long userId, NotificationCategory category, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Notification> notifications = notificationMapper.selectByUserIdAndCategoryWithPage(userId, category, offset, pageSize);
        int total = notificationMapper.countByUserIdAndCategory(userId, category);
        return PageResult.build(notifications, (long) total, pageNum, pageSize);
    }

    @Override
    public Map<NotificationCategory, Integer> getUnreadCountByCategory(Long userId) {
        // 尝试从缓存获取
        Map<NotificationCategory, Integer> cachedCountMap = notificationCacheService.getCachedUnreadCountByCategory(userId);
        if (cachedCountMap != null) {
            return cachedCountMap;
        }

        // 从数据库查询
        List<Map<String, Object>> countList = notificationMapper.countUnreadByUserIdGroupByCategory(userId);
        Map<NotificationCategory, Integer> countMap = new java.util.HashMap<>();

        // 初始化所有分类的未读数量为0
        for (NotificationCategory cat : NotificationCategory.values()) {
            countMap.put(cat, 0);
        }

        // 填充查询结果
        for (Map<String, Object> map : countList) {
            String categoryName = (String) map.get("category");
            Integer count = (Integer) map.get("count");
            try {
                NotificationCategory category = NotificationCategory.valueOf(categoryName);
                countMap.put(category, count);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown notification category: {}", categoryName);
            }
        }

        // 缓存结果
        notificationCacheService.cacheUnreadCountByCategory(userId, countMap);
        return countMap;
    }

    @Override
    public boolean markAsReadByCategory(Long userId, NotificationCategory category) {
        int result = notificationMapper.updateByUserIdAndCategoryReadStatus(userId, category, ReadStatus.READ);
        if (result > 0) {
            // 清除缓存
            notificationCacheService.clearUnreadCountCache(userId);
        }
        return result > 0;
    }

    @Override
    public boolean clearNotifications(Long userId) {
        int result = notificationMapper.deleteByUserId(userId);
        if (result > 0) {
            // 清除缓存
            notificationCacheService.clearUnreadCountCache(userId);
            notificationCacheService.clearNotificationsCache(userId);
        }
        return result > 0;
    }

    @Override
    public List<Notification> getAggregatedNotifications(Long userId) {
        // 实现通知聚合逻辑
        // 这里简化实现，实际项目中需要根据聚合键进行分组
        // 聚合逻辑...
        return notificationMapper.selectByUserId(userId);
    }
}
