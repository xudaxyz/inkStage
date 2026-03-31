package com.inkstage.event;

import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.ReportTargetType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 通知事件
 * <p>
 * 用于解耦通知服务与其他服务的依赖关系
 */
@Getter
public class NotificationEvent extends ApplicationEvent {

    /**
     * 接收通知的用户ID
     */
    private final Long userId;

    /**
     * 通知类型
     */
    private final NotificationType notificationType;

    /**
     * 通知标题
     */
    private final String title;

    /**
     * 通知内容
     */
    private final String content;

    /**
     * 关联ID
     */
    private final Long relatedId;

    /**
     * 关联类型
     */
    private final ReportTargetType relatedType;

    /**
     * 发送者ID
     */
    private final Long senderId;

    /**
     * 操作链接
     */
    private final String actionUrl;

    /**
     * 额外数据
     */
    private final String extraData;

    public NotificationEvent(Object source, Long userId, NotificationType notificationType, String title, String content,
                             Long relatedId, ReportTargetType relatedType, Long senderId, String actionUrl, String extraData) {
        super(source);
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
        this.senderId = senderId;
        this.actionUrl = actionUrl;
        this.extraData = extraData;
    }

    /**
     * 构建器模式创建事件
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Object source;
        private Long userId;
        private NotificationType notificationType;
        private String title;
        private String content;
        private Long relatedId;
        private ReportTargetType relatedType;
        private Long senderId;
        private String actionUrl;
        private String extraData;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder relatedId(Long relatedId) {
            this.relatedId = relatedId;
            return this;
        }

        public Builder relatedType(ReportTargetType relatedType) {
            this.relatedType = relatedType;
            return this;
        }

        public Builder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder actionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
            return this;
        }

        public Builder extraData(String extraData) {
            this.extraData = extraData;
            return this;
        }

        public NotificationEvent build() {
            return new NotificationEvent(source, userId, notificationType, title, content, relatedId, relatedType, senderId, actionUrl, extraData);
        }
    }
}
