package com.inkstage.enums.notification;

import lombok.Getter;

/**
 * 通知模板变量枚举类
 * 定义所有允许在通知模板中使用的变量
 */
@Getter
public enum NotificationTemplateVariable {
    
    // 用户相关变量
    USER_ID("userId", "用户ID"),
    USERNAME("username", "用户名"),
    USER_AVATAR("userAvatar", "用户头像"),
    USER_NICKNAME("userNickname", "用户昵称"),
    
    // 文章相关变量
    ARTICLE_ID("articleId", "文章ID"),
    ARTICLE_TITLE("articleTitle", "文章标题"),
    ARTICLE_USER_ID("articleUserId", "文章作者ID"),
    ARTICLE_USERNAME("articleUsername", "文章作者名称"),
    ARTICLE_URL("articleUrl", "文章链接"),
    COLLECTOR_ID("collectorId", "收藏者ID"),
    COLLECTOR_NAME("collectorName", "收藏者名称"),

    // 评论相关变量
    COMMENT_ID("commentId", "评论ID"),
    COMMENT_CONTENT("commentContent", "评论内容"),
    COMMENT_USERNAME("commentUsername", "评论作者名称"),

    // 标签相关
    TAG_ID("tagId", "标签ID"),
    TAG_NAME("tagName", "标签名称"),

    // 系统相关变量
    SYSTEM_TIME("systemTime", "系统时间"),
    SYSTEM_NAME("systemName", "系统名称"),
    SYSTEM_VERSION("systemVersion", "系统版本"),
    
    // 通用变量
    RELATED_ID("relatedId", "关联ID"),
    ACTION_URL("actionUrl", "操作链接"),
    MESSAGE_CONTENT("messageContent", "消息内容"),
    
    // 通知相关变量
    NOTIFICATION_TYPE("notificationType", "通知类型"),
    NOTIFICATION_TIME("notificationTime", "通知时间"),
    SENDER_ID("senderId", "发送者ID"),
    SENDER_NAME("senderName", "发送者名称");
    
    private final String key;
    private final String description;
    
    NotificationTemplateVariable(String key, String description) {
        this.key = key;
        this.description = description;
    }
    
    /**
     * 根据key获取枚举值
     */
    public static NotificationTemplateVariable getByKey(String key) {
        for (NotificationTemplateVariable variable : values()) {
            if (variable.key.equals(key)) {
                return variable;
            }
        }
        return null;
    }
    
    /**
     * 检查key是否是有效的变量
     */
    public static boolean isInvalidKey(String key) {
        return getByKey(key) == null;
    }
    
    /**
     * 获取所有变量的key列表
     */
    public static String[] getAllKeys() {
        String[] keys = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            keys[i] = values()[i].key;
        }
        return keys;
    }
    
    /**
     * 获取所有变量的描述列表
     */
    public static String[] getAllDescriptions() {
        String[] descriptions = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            descriptions[i] = values()[i].description;
        }
        return descriptions;
    }
}
