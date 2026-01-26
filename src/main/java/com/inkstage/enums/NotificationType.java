package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum NotificationType implements EnumCode {
    SYSTEM(100, "系统通知"),
    ARTICLE_PUBLISH(200, "文章发布通知"),
    ARTICLE_LIKE(201, "文章点赞通知"),
    ARTICLE_COLLECTION(202, "文章收藏通知"),
    ARTICLE_COMMENT(203, "文章评论通知"),
    COMMENT_REPLY(300, "评论回复通知"),
    COMMENT_LIKE(301, "评论点赞通知"),
    FOLLOW(400, "关注通知"),
    MESSAGE(500, "私信通知"),
    REPORT(600, "举报处理通知"),
    FEEDBACK(700, "反馈处理通知");

    private final Integer code;
    private final String desc;

    NotificationType(Integer code, String desc) {
        this.code = code;
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