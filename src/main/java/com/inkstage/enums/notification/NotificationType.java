package com.inkstage.enums.notification;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum NotificationType implements EnumCode {
    SYSTEM(100,  "SYSTEM","系统通知"),
    ARTICLE_PUBLISH(200, "ARTICLE_PUBLISH","文章发布通知"),
    COLUMN_ARTICLE_PUBLISH(210, "COLUMN_ARTICLE_PUBLISH", "专栏文章发布通知"),
    COLUMN_DISABLED(211, "COLUMN_DISABLED", "专栏下线通知"),
    COLUMN_RESTORED(212, "COLUMN_RESTORED", "专栏恢复通知"),
    ARTICLE_LIKE(201, "ARTICLE_LIKE","文章点赞通知"),
    ARTICLE_COLLECTION(202, "ARTICLE_COLLECTION","文章收藏通知"),
    ARTICLE_COMMENT(203, "ARTICLE_COMMENT","文章评论通知"),
    COMMENT_REPLY(300, "COMMENT_REPLY","评论回复通知"),
    COMMENT_LIKE(301, "COMMENT_LIKE","评论点赞通知"),
    FOLLOW(400, "FOLLOW","关注通知"),
    MESSAGE(500, "MESSAGE","私信通知"),
    REPORT(600, "REPORT","举报通知"),
    REPORT_RESULT(601, "REPORT_RESULT","举报处理通知"),
    FEEDBACK(700, "FEEDBACK","反馈处理通知"),
    USER_STATUS_CHANGE(800, "USER_STATUS_CHANGE","用户状态变更通知"),
    ARTICLE_REVIEW_APPROVE(801, "ARTICLE_REVIEW_APPROVE","文章审核通过通知"),
    ARTICLE_REVIEW_REJECT(802, "ARTICLE_REVIEW_REJECT","文章审核拒绝通知"),
    ARTICLE_REVIEW_REPROCESS(803, "ARTICLE_REVIEW_REPROCESS","文章重新审核通知"),
    ARTICLE_OFFLINE(804, "ARTICLE_OFFLINE","文章下架通知"),
    ARTICLE_ONLINE(805, "ARTICLE_ONLINE","文章重新上架通知"),
    ARTICLE_TOP(806, "ARTICLE_TOP","文章置顶通知"),
    ARTICLE_RECOMMEND(807, "ARTICLE_RECOMMEND","文章推荐通知"),
    ARTICLE_DELETE(808, "ARTICLE_DELETE","文章删除通知"),
    TAG_DELETE(809, "TAG_DELETE","标签删除通知"),
    COMMENT_REVIEW_REJECT(810, "COMMENT_REVIEW_REJECT","评论审核拒绝通知"),
    COMMENT_TOP(811, "COMMENT_TOP","评论置顶通知");

    private final Integer code;
    private final String name;
    private final String desc;

    NotificationType(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
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