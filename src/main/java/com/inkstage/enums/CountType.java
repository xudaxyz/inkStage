package com.inkstage.enums;

import lombok.Getter;

/**
 * 通用计数类型枚举
 * <p>
 * 定义系统中所有需要递增/递减更新的计数字段，
 * 每个枚举值对应一个实体表中的具体计数字段。
 */
@Getter
public enum CountType {

    // ══════════ 文章 (Article) ══════════
    ARTICLE_READ("文章阅读数"),
    ARTICLE_LIKE("文章点赞数"),
    ARTICLE_COMMENT("文章评论数"),
    ARTICLE_COLLECTION("文章收藏数"),
    ARTICLE_SHARE("文章分享数"),

    // ══════════ 用户 (User) ══════════
    USER_ARTICLE("用户文章数"),
    USER_FOLLOW("用户关注数"),
    USER_FOLLOWER("用户粉丝数"),
    USER_COMMENT("用户评论数"),
    USER_LIKE("用户获赞数"),

    // ══════════ 评论 (Comment) ══════════
    COMMENT_LIKE("评论点赞数"),
    COMMENT_REPLY("评论回复数"),
    COMMENT_REPORT("评论举报数"),

    // ══════════ 专栏 (Column) ══════════
    COLUMN_ARTICLE("专栏文章数"),
    COLUMN_READ("专栏阅读数"),
    COLUMN_SUBSCRIPTION("专栏订阅数"),

    // ══════════ 标签 (Tag) ══════════
    TAG_ARTICLE("标签文章数"),
    TAG_USAGE("标签使用数"),

    // ══════════ 分类 (Category) ══════════
    CATEGORY_ARTICLE("分类文章数"),

    // ══════════ 收藏夹 (CollectionFolder) ══════════
    FOLDER_ARTICLE("收藏夹文章数"),

    // ══════════ 搜索热词 (SearchHotWord) ══════════
    HOT_WORD_SEARCH("热词搜索数"),

    // ══════════ 系统公告 (SystemAnnouncement) ══════════
    ANNOUNCEMENT_READ("公告阅读数");

    private final String description;

    CountType(String description) {
        this.description = description;
    }
}
