package com.inkstage.enums.notification;

import com.inkstage.enums.EnumCode;
import com.inkstage.notification.NotificationParam;
import com.inkstage.notification.param.*;
import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NotificationType implements EnumCode {

    // ==================== 可关闭的通知（用户可自主设置开关） ====================
    // [文章互动]文章点赞、收藏、评论相关
    ARTICLE_PUBLISH(200, "ARTICLE_PUBLISH", "文章发布通知", ArticlePublishParam.class, "articlePublishNotification"),
    ARTICLE_LIKE(201, "ARTICLE_LIKE", "文章点赞通知", ArticleLikeParam.class, "articleLikeNotification"),
    ARTICLE_COLLECTION(202, "ARTICLE_COLLECTION", "文章收藏通知", ArticleCollectionParam.class, "articleCollectionNotification"),
    ARTICLE_COMMENT(203, "ARTICLE_COMMENT", "文章评论通知", ArticleCommentParam.class, "articleCommentNotification"),

    // [评论互动]评论回复、点赞相关
    COMMENT_REPLY(300, "COMMENT_REPLY", "评论回复通知", CommentReplyParam.class, "commentReplyNotification"),
    COMMENT_LIKE(301, "COMMENT_LIKE", "评论点赞通知", CommentLikeParam.class, "commentLikeNotification"),

    // [社交互动]关注、私信相关
    FOLLOW(400, "FOLLOW", "关注通知", FollowParam.class, "followNotification"),
    MESSAGE(500, "MESSAGE", "私信通知", NotificationParam.class, "messageNotification"),

    // [系统通知]举报、反馈、系统公告（用户可关闭）
    REPORT(600, "REPORT", "举报通知", ReportParam.class, "reportNotification"),
    REPORT_RESULT(601, "REPORT_RESULT", "举报处理通知", ReportResultParam.class, "reportResultNotification"),
    FEEDBACK(700, "FEEDBACK", "反馈处理通知", FeedbackParam.class, "feedbackNotification"),

    SYSTEM(100, "SYSTEM", "系统通知", SystemNotificationParam.class, "systemNotification"),

    // ==================== 不可关闭的通知（系统强制发送，用户无法设置） ====================
    // [用户状态]账号安全相关
    USER_STATUS_CHANGE(2001, "USER_STATUS_CHANGE", "用户状态变更通知", UserStatusChangeParam.class, null),

    // [文章审核]管理员操作结果，不可关闭
    ARTICLE_REVIEW_APPROVE(3001, "ARTICLE_REVIEW_APPROVE", "文章审核通过通知", ArticleReviewApproveParam.class, null),
    ARTICLE_REVIEW_REJECT(3002, "ARTICLE_REVIEW_REJECT", "文章审核拒绝通知", ArticleReviewRejectParam.class, null),
    ARTICLE_REVIEW_REPROCESS(3003, "ARTICLE_REVIEW_REPROCESS", "文章重新审核通知", ArticleReviewReprocessParam.class, null),
    ARTICLE_OFFLINE(3004, "ARTICLE_OFFLINE", "文章下架通知", ArticleOfflineParam.class, null),
    ARTICLE_ONLINE(3005, "ARTICLE_ONLINE", "文章重新上架通知", ArticleOnlineParam.class, null),
    ARTICLE_TOP(3006, "ARTICLE_TOP", "文章置顶通知", ArticleTopParam.class, null),
    ARTICLE_RECOMMEND(3007, "ARTICLE_RECOMMEND", "文章推荐通知", ArticleRecommendParam.class, null),
    ARTICLE_DELETE(3008, "ARTICLE_DELETE", "文章删除通知", ArticleDeleteParam.class, null),

    // [评论审核]管理员操作结果，不可关闭
    COMMENT_REVIEW_REJECT(4001, "COMMENT_REVIEW_REJECT", "评论审核拒绝通知", CommentReviewRejectParam.class, null),
    COMMENT_TOP(4002, "COMMENT_TOP", "评论置顶通知", CommentTopParam.class, null),

    // [专栏管理]专栏状态变更
    COLUMN_SUBSCRIPTION(5001, "COLUMN_SUBSCRIPTION", "专栏订阅通知", ColumnSubscriptionParam.class, null),
    COLUMN_DISABLED(5002, "COLUMN_DISABLED", "专栏下线通知", ColumnDisabledParam.class, null),
    COLUMN_RESTORED(5003, "COLUMN_RESTORED", "专栏恢复通知", ColumnRestoredParam.class, null),

    // [系统管理]系统操作通知
    TAG_DELETE(1001, "TAG_DELETE", "标签删除通知", TagDeleteParam.class, null);

    private final Integer code;
    private final String name;
    private final String desc;
    private final Class<? extends NotificationParam> paramClass;
    private final String settingField;

    NotificationType(Integer code, String name, String desc, Class<? extends NotificationParam> paramClass, String settingField) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.paramClass = paramClass;
        this.settingField = settingField;
    }

    public boolean hasSettingField() {
        return this.settingField != null;
    }

    public static NotificationType fromCode(Integer code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
