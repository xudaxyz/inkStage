package com.inkstage.cache.constant;

import java.time.Duration;

public final class CacheTTL {

    private CacheTTL() {
    }

    // ==================== 基础时间常量 ====================
    public static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    public static final Duration TEN_MINUTES = Duration.ofMinutes(10);
    public static final Duration FIFTEEN_MINUTES = Duration.ofMinutes(15);
    public static final Duration THIRTY_MINUTES = Duration.ofMinutes(30);
    public static final Duration ONE_HOUR = Duration.ofHours(1);
    public static final Duration TWO_HOURS = Duration.ofHours(2);
    public static final Duration FIVE_HOURS = Duration.ofHours(5);
    public static final Duration TEN_HOURS = Duration.ofHours(10);
    public static final Duration ONE_DAY = Duration.ofDays(1);
    public static final Duration SEVEN_DAYS = Duration.ofDays(7);
    public static final Duration HALF_MONTH = Duration.ofDays(15);
    public static final Duration ONE_MONTH = Duration.ofDays(30);

    // 默认TTL
    public static final Duration DEFAULT = ONE_HOUR;

    // ==================== 业务语义常量 ====================

    // 文章相关
    public static final Duration ARTICLE_DETAIL = TWO_HOURS;
    public static final Duration ARTICLE_LIST = ONE_HOUR;
    public static final Duration ARTICLE_HOT = THIRTY_MINUTES;
    public static final Duration ARTICLE_LATEST = FIVE_MINUTES;
    public static final Duration ARTICLE_BANNER = ONE_HOUR;
    public static final Duration ARTICLE_SEARCH = ONE_HOUR;
    public static final Duration ARTICLE_COLLECT_STATUS = ONE_HOUR;
    public static final Duration ARTICLE_LIKE_STATUS = ONE_HOUR;

    // 用户相关
    public static final Duration USER_INFO = THIRTY_MINUTES;
    public static final Duration USER_ARTICLES = TWO_HOURS;
    public static final Duration USER_HOT = ONE_HOUR;

    // 专栏相关
    public static final Duration COLUMN_DETAIL = THIRTY_MINUTES;
    public static final Duration COLUMN_LIST = FIFTEEN_MINUTES;
    public static final Duration COLUMN_ARTICLES = FIFTEEN_MINUTES;
    public static final Duration COLUMN_HOT = ONE_HOUR;
    public static final Duration COLUMN_SUBSCRIPTION_STATUS = ONE_HOUR;
    public static final Duration COLUMN_SUBSCRIPTION_LIST = THIRTY_MINUTES;

    // 评论相关
    public static final Duration COMMENT_LIST = THIRTY_MINUTES;
    public static final Duration COMMENT_REPLIES = THIRTY_MINUTES;

    // 分类和标签
    public static final Duration CATEGORIES = ONE_HOUR;
    public static final Duration TAGS = FIFTEEN_MINUTES;
    public static final Duration ROLES = SEVEN_DAYS;
    public static final Duration USER_ROLES = ONE_DAY;

    // 通知相关
    public static final Duration NOTIFICATION_UNREAD = THIRTY_MINUTES;
    public static final Duration NOTIFICATION_RECENT = FIVE_MINUTES;
    public static final Duration NOTIFICATION_SETTING = THIRTY_MINUTES;

    // 系统配置
    public static final Duration SYSTEM_CONFIG = ONE_HOUR;
    public static final Duration DASHBOARD = FIVE_MINUTES;
    public static final Duration ANNOUNCEMENT = ONE_HOUR;
    public static final Duration READING_HISTORY = THIRTY_MINUTES;

    // 认证相关
    public static final Duration VERIFY_CODE = FIVE_MINUTES;
    public static final Duration LOGIN_ATTEMPT = FIFTEEN_MINUTES;
    public static final Duration LOGIN_LOCK = FIFTEEN_MINUTES;
    public static final Duration REFRESH_TOKEN = SEVEN_DAYS;
}