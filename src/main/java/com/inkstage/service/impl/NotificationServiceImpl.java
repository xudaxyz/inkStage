package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.NotificationCategory;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.ReadStatus;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.service.NotificationService;
import com.inkstage.service.NotificationSettingService;
import com.inkstage.service.NotificationTemplateService;
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
            // 减少未读通知数量
            Notification notification = notificationMapper.selectById(notificationId);
            if (notification != null) {
                notificationCacheService.decrementUnreadCount(notification.getUserId());
            }
        }
        return result > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        int result = notificationMapper.updateAllReadStatus(userId, ReadStatus.READ);
        if (result > 0) {
            // 重置未读通知数量
            notificationCacheService.resetUnreadCount(userId);
        }
        return result > 0;
    }

    @Override
    public boolean deleteNotification(Long notificationId) {
        // 先获取通知信息，用于后续更新缓存
        Notification notification = notificationMapper.selectById(notificationId);
        int result = notificationMapper.deleteById(notificationId);
        if (result > 0 && notification != null && ReadStatus.UNREAD.equals(notification.getReadStatus())) {
            // 如果删除的是未读通知，减少未读通知数量
            notificationCacheService.decrementUnreadCount(notification.getUserId());
        }
        return result > 0;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        // 构建通知消息DTO
        NotificationMessageDTO message = new NotificationMessageDTO();
        message.setUserId(notification.getUserId());
        message.setContent(notification.getContent());
        message.setType(notification.getType());
        message.setRelatedId(notification.getRelatedId());
        message.setRelatedType(notification.getRelatedType());
        message.setSenderId(notification.getSenderId());
        message.setActionUrl(notification.getActionUrl());
        message.setExtraData(notification.getExtraData());
        message.setTitle(notification.getTitle());

        // 发送到RabbitMQ
        notificationProducer.sendNotification(message);
        return true;
    }

    @Override
    public boolean sendNotificationWithTemplate(Long userId, NotificationType type, Long relatedId, Long senderId, Object... params) {
        // 检查用户是否开启了该类型的通知
        String settingKey = switch (type) {
            case ARTICLE_PUBLISH -> "articlePublish";
            case ARTICLE_LIKE -> "articleLike";
            case ARTICLE_COLLECTION -> "articleCollection";
            case ARTICLE_COMMENT -> "articleComment";
            case COMMENT_REPLY -> "commentReply";
            case COMMENT_LIKE -> "commentLike";
            case FOLLOW -> "follow";
            case MESSAGE -> "message";
            case REPORT -> "report";
            case FEEDBACK -> "feedback";
            case SYSTEM, USER_STATUS_CHANGE, ARTICLE_REVIEW_REPROCESS, ARTICLE_TOP, ARTICLE_RECOMMEND, ARTICLE_DELETE,
                 ARTICLE_ONLINE, ARTICLE_OFFLINE, TAG_DELETE, ARTICLE_REVIEW_REJECT, COMMENT_REVIEW_REJECT,
                 COMMENT_TOP -> "system";
        };

        if (!notificationSettingService.isNotificationEnabled(userId, settingKey)) {
            log.info("用户 ID {} 已关闭 {} 类型的通知", userId, type.getDesc());
            return false;
        }

        // 生成通知标题
        String title = notificationTemplateService.generateTitle(type, params);
        // 生成通知内容
        String content = notificationTemplateService.generateContent(type, params);
        // 生成操作链接
        String actionUrl = notificationTemplateService.generateActionUrl(type, relatedId);

        // 构建通知对象
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setSenderId(senderId);
        notification.setActionUrl(actionUrl);

        // 发送通知
        return sendNotification(notification);
    }

    @Override
    public boolean sendBatchNotifications(List<Notification> notifications) {
        try {
            List<NotificationMessageDTO> messages = new ArrayList<>();
            for (Notification notification : notifications) {
                // 检查用户是否开启了该类型的通知
                String settingKey = switch (notification.getType()) {
                    case ARTICLE_PUBLISH -> "articlePublish";
                    case ARTICLE_LIKE -> "articleLike";
                    case ARTICLE_COLLECTION -> "articleCollection";
                    case ARTICLE_COMMENT -> "articleComment";
                    case COMMENT_REPLY -> "commentReply";
                    case COMMENT_LIKE -> "commentLike";
                    case FOLLOW -> "follow";
                    case MESSAGE -> "message";
                    case REPORT -> "report";
                    case FEEDBACK -> "feedback";
                    case SYSTEM, USER_STATUS_CHANGE, ARTICLE_REVIEW_REJECT, ARTICLE_REVIEW_REPROCESS, ARTICLE_OFFLINE,
                         ARTICLE_ONLINE, ARTICLE_TOP, ARTICLE_RECOMMEND,
                         ARTICLE_DELETE, TAG_DELETE, COMMENT_REVIEW_REJECT, COMMENT_TOP -> "system";

                };

                if (!notificationSettingService.isNotificationEnabled(notification.getUserId(), settingKey)) {
                    log.info("用户 {} 已关闭 {} 类型的通知", notification.getUserId(), notification.getType().getDesc());
                    continue;
                }

                // 构建通知消息DTO
                NotificationMessageDTO message = new NotificationMessageDTO();
                message.setUserId(notification.getUserId());
                message.setType(notification.getType());
                message.setContent(notification.getContent());
                message.setRelatedId(notification.getRelatedId());
                message.setSenderId(notification.getSenderId());
                message.setRelatedType(notification.getRelatedType());
                message.setActionUrl(notification.getActionUrl());
                message.setExtraData(notification.getExtraData());
                message.setTitle(notification.getTitle());

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
                String settingKey = switch (type) {
                    case ARTICLE_PUBLISH -> "articlePublish";
                    case ARTICLE_LIKE -> "articleLike";
                    case ARTICLE_COLLECTION -> "articleCollection";
                    case ARTICLE_COMMENT -> "articleComment";
                    case COMMENT_REPLY -> "commentReply";
                    case COMMENT_LIKE -> "commentLike";
                    case FOLLOW -> "follow";
                    case MESSAGE -> "message";
                    case REPORT -> "report";
                    case FEEDBACK -> "feedback";
                    case SYSTEM, USER_STATUS_CHANGE, ARTICLE_REVIEW_REJECT, ARTICLE_REVIEW_REPROCESS, ARTICLE_OFFLINE,
                         ARTICLE_ONLINE, ARTICLE_TOP, ARTICLE_RECOMMEND,
                         ARTICLE_DELETE, TAG_DELETE, COMMENT_REVIEW_REJECT, COMMENT_TOP -> "system";

                };

                if (!notificationSettingService.isNotificationEnabled(userId, settingKey)) {
                    log.info("用户 {} 已关闭 {} 类型的通知", userId, type.getDesc());
                    continue;
                }

                // 生成通知标题
                String title = notificationTemplateService.generateTitle(type, params);
                // 生成通知内容
                String content = notificationTemplateService.generateContent(type, params);
                // 生成操作链接
                String actionUrl = notificationTemplateService.generateActionUrl(type, relatedId);

                // 构建通知对象
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setType(type);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setRelatedId(relatedId);
                notification.setSenderId(senderId);
                notification.setActionUrl(actionUrl);

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
        return notificationCacheService.getUnreadCount(userId);
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
