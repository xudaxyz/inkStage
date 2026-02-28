package com.inkstage.enums;

import lombok.Getter;

/**
 * 计数类型
 */
@Getter
public enum CountType {
    // 文章相关计数
    ARTICLE_READ_COUNT("ARTICLE_READ_COUNT"),
    ARTICLE_LIKE_COUNT("ARTICLE_LIKE_COUNT"),
    ARTICLE_COMMENT_COUNT("ARTICLE_COMMENT_COUNT"),
    ARTICLE_COLLECTION_COUNT("ARTICLE_COLLECTION_COUNT"),
    ARTICLE_SHARE_COUNT("ARTICLE_SHARE_COUNT"),
    // 评论相关计数
    COMMENT_REPLY_COUNT("COMMENT_REPLY_COUNT");

    private final String type;

    CountType(String type) {
        this.type = type;
    }

}
