package com.inkstage.cache.constant;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class CacheTTL {

    private CacheTTL() {
    }

    // ==================== 基础时间常量 ====================
    public static final Duration FIVE_MINUTES = Duration.ofMinutes(5);
    public static final Duration FIFTEEN_MINUTES = Duration.ofMinutes(15);
    public static final Duration THIRTY_MINUTES = Duration.ofMinutes(30);
    public static final Duration ONE_HOUR = Duration.ofHours(1);
    public static final Duration TWO_HOURS = Duration.ofHours(2);
    public static final Duration ONE_DAY = Duration.ofDays(1);
    public static final Duration SEVEN_DAYS = Duration.ofDays(7);

    // 默认TTL
    public static final Duration DEFAULT = ONE_HOUR;

    // ==================== 随机偏移配置 ====================
    /**
     * 默认随机偏移比例（0.1 = 10%）
     * 例如：基础TTL为1小时，偏移后范围为 54分钟 ~ 66分钟
     */
    private static final double DEFAULT_RANDOM_OFFSET_RATIO = 0.1;

    /**
     * 最小随机偏移时间（5分钟）
     */
    private static final Duration MIN_RANDOM_OFFSET = Duration.ofMinutes(5);

    /**
     * 最大随机偏移时间（30分钟）
     */
    private static final Duration MAX_RANDOM_OFFSET = Duration.ofMinutes(30);

    /**
     * 为给定的基础TTL添加随机偏移，防止缓存雪崩
     *
     * @param baseTTL 基础TTL
     * @return 添加随机偏移后的TTL
     */
    public static Duration withRandomOffset(Duration baseTTL) {
        return withRandomOffset(baseTTL, DEFAULT_RANDOM_OFFSET_RATIO);
    }

    /**
     * 为给定的基础TTL添加随机偏移，防止缓存雪崩
     *
     * @param baseTTL     基础TTL
     * @param offsetRatio 偏移比例（0.0 ~ 1.0）
     * @return 添加随机偏移后的TTL
     */
    public static Duration withRandomOffset(Duration baseTTL, double offsetRatio) {
        if (baseTTL == null) {
            return DEFAULT;
        }

        // 确保偏移比例在合理范围内
        double ratio = Math.clamp(offsetRatio, 0.0, 1.0);

        // 计算偏移量
        long baseSeconds = baseTTL.getSeconds();
        long offsetSeconds = (long) (baseSeconds * ratio);

        // 确保偏移量在最小和最大范围内
        offsetSeconds = Math.clamp(offsetSeconds, MIN_RANDOM_OFFSET.getSeconds(), MAX_RANDOM_OFFSET.getSeconds());

        // 生成随机偏移（正负范围内）
        Random random = ThreadLocalRandom.current();
        long randomOffset = random.nextLong(-offsetSeconds, offsetSeconds + 1);

        // 计算最终TTL，确保不为负数
        long finalSeconds = Math.max(60, baseSeconds + randomOffset);

        return Duration.ofSeconds(finalSeconds);
    }

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