package com.inkstage.enums.notification;

import lombok.Getter;

/**
 * 通知分类枚举
 */
@Getter
public enum NotificationCategory {
    INTERACTION_LIKE_COLLECT("互动通知-赞与收藏"),
    INTERACTION_COMMENT_AT("互动通知-评论和@"),
    INTERACTION_FOLLOW("互动通知-新增关注"),
    SYSTEM_ANNOUNCEMENT("系统通知-官方公告"),
    SYSTEM_AUDIT("系统通知-审核结果"),
    SYSTEM_SECURITY("系统通知-账号安全"),
    CONTENT_UPDATE_FOLLOW("内容更新-关注内容"),
    CONTENT_UPDATE_RECOMMEND("内容更新-推荐内容");

    private final String description;

    NotificationCategory(String description) {
        this.description = description;
    }
}