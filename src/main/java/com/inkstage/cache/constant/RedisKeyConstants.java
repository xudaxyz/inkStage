package com.inkstage.cache.constant;

import java.time.Duration;

/**
 * Redis键常量类
 * 统一管理Redis缓存键和过期时间
 */
public class RedisKeyConstants {

    // ==================== 基础配置 ====================

    /**
     * 模块前缀
     */
    public static final String MODULE_PREFIX = "inkstage:";

    /**
     * 版本号前缀
     */
    public static final String VERSION_PREFIX = "v:";

    // ==================== 前缀常量（全大写带下划线） ====================

    // 认证相关
    public static final String REFRESH_TOKEN_PREFIX = "inkstage:refresh_token:";
    public static final String USER_REFRESH_TOKEN_PREFIX = "inkstage:user:refresh_tokens:";
    public static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "inkstage:refresh_token_black:";
    public static final String LOGIN_ATTEMPT_PREFIX = "inkstage:login:attempt:";
    public static final String LOGIN_LOCK_PREFIX = "inkstage:login:lock:";
    public static final String USER_SESSION_PREFIX = "inkstage:user_session:";
    public static final String VERIFICATION_CODE_PREFIX = "inkstage:verify:";

    // 用户相关
    public static final String USER_PREFIX = "inkstage:user:";
    public static final String USER_INFO_PREFIX = "inkstage:user:info:";
    public static final String ADMIN_INFO_PREFIX = "inkstage:admin:info:";
    public static final String USER_ARTICLE_LIST_PREFIX = "inkstage:user:articles:";
    public static final String USER_HOT_PREFIX = "inkstage:user:hot:";

    // 文章相关
    public static final String ARTICLE_PREFIX = "inkstage:article:";
    public static final String ARTICLES_PREFIX = "inkstage:articles:";
    public static final String ARTICLE_HOT_PREFIX = "inkstage:article:hot:";
    public static final String ARTICLE_DETAIL_PREFIX = "inkstage:article:detail:";
    public static final String ARTICLE_COLLECT_PREFIX = "inkstage:article:collect:";
    public static final String ARTICLE_LIKE_PREFIX = "inkstage:article:like:";
    public static final String ARTICLE_LATEST_PREFIX = "inkstage:article:latest:";
    public static final String ARTICLE_BANNER_PREFIX = "inkstage:article:banner:";
    public static final String ARTICLE_SEARCH_PREFIX = "inkstage:article:search:";

    // 其他业务相关
    public static final String CATEGORY_PREFIX = "inkstage:category:";
    public static final String TAG_PREFIX = "inkstage:tag:";
    public static final String COMMENT_LIST_PREFIX = "inkstage:comment:list:";
    public static final String COMMENT_REPLY_PREFIX = "inkstage:comment:reply:";
    public static final String FOLLOW_PREFIX = "inkstage:follow:";
    public static final String READING_HISTORY_PREFIX = "inkstage:reading:history:";
    public static final String PERMISSION_PREFIX = "inkstage:permission:";
    public static final String ROLE_PREFIX = "inkstage:role:";
    public static final String RATE_LIMIT_PREFIX = "inkstage:rate:";
    public static final String LOCK_PREFIX = "inkstage:lock:";
    public static final String SYSTEM_CONFIG_PREFIX = "inkstage:sys_config:";
    public static final String HOT_DATA_PREFIX = "inkstage:hot:";
    public static final String NOTIFICATION_UNREAD_COUNT = "inkstage:notify:unread:count:";
    public static final String NOTIFICATION_UNREAD_COUNT_BY_CATEGORY = "inkstage:notify:unread:category:";
    public static final String NOTIFICATION_RECENT_LIST = "inkstage:notify:recent:list:";

    // ==================== 缓存名称（小写带冒号） ====================

    // 文章相关
    public static final String CACHE_ARTICLES = "articles";
    public static final String CACHE_ARTICLE_DETAIL = "article:detail";
    public static final String CACHE_ARTICLE_HOT = "article:hot";
    public static final String CACHE_ARTICLE_LATEST = "article:latest";
    public static final String CACHE_ARTICLE_BANNER = "article:banner";
    public static final String CACHE_ARTICLE_USER = "article:user";
    public static final String CACHE_ARTICLE_USER_RELATED = "article:user:related";
    public static final String CACHE_ARTICLE_SEARCH = "article:search";
    public static final String CACHE_ARTICLE_MY = "article:my";

    // 用户相关
    public static final String CACHE_USER_HOT = "user:hot";
    public static final String CACHE_USER_ROLES = "user:roles";

    // 其他业务
    public static final String CACHE_DASHBOARD = "dashboard";
    public static final String CACHE_FOLLOW_STATUS = "follow:status";
    public static final String CACHE_FOLLOW_LIST = "follow:list";
    public static final String CACHE_ANNOUNCEMENT = "announcement";
    public static final String CACHE_READING_HISTORY = "reading:history";
    public static final String CACHE_COMMENT_LIST = "comment:list";
    public static final String CACHE_COMMENT_REPLIES = "comment:replies";
    public static final String CACHE_COLLECTION_STATUS = "collection:status";
    public static final String CACHE_LIKE_STATUS = "like:status";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_TAGS = "tags";
    public static final String CACHE_ROLES = "roles";

    // RedisUtil使用的缓存
    public static final String CACHE_ARTICLE_COUNT = "article:count";
    public static final String CACHE_NOTIFICATION_UNREAD = "notification:unread";
    public static final String CACHE_NOTIFICATION_RECENT = "notification:recent";
    public static final String CACHE_VERIFY_CODE = "verify:code";
    public static final String CACHE_LOGIN_ATTEMPT = "login:attempt";
    public static final String CACHE_LOGIN_LOCK = "login:lock";

    // ==================== 过期时间常量 ====================

    // 基础过期时间
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    public static final Duration SHORT_TTL = Duration.ofMinutes(5);
    public static final Duration MEDIUM_TTL = Duration.ofMinutes(15);
    public static final Duration LONG_TTL = Duration.ofHours(1);
    public static final Duration VERY_LONG_TTL = Duration.ofHours(2);
    public static final Duration EXTREME_TTL = Duration.ofDays(7);

    // 具体缓存过期时间
    public static final Duration CACHE_ARTICLE_DETAIL_TTL = VERY_LONG_TTL;
    public static final Duration CACHE_ARTICLE_HOT_TTL = MEDIUM_TTL;
    public static final Duration CACHE_ARTICLE_LATEST_TTL = SHORT_TTL;
    public static final Duration CACHE_ARTICLE_BANNER_TTL = LONG_TTL;
    public static final Duration CACHE_ARTICLE_USER_TTL = VERY_LONG_TTL;
    public static final Duration CACHE_ARTICLE_USER_RELATED_TTL = LONG_TTL;
    public static final Duration CACHE_ARTICLE_SEARCH_TTL = LONG_TTL;
    public static final Duration CACHE_ARTICLE_MY_TTL = LONG_TTL;
    public static final Duration CACHE_COMMENT_LIST_TTL = DEFAULT_TTL;
    public static final Duration CACHE_LIKE_STATUS_TTL = LONG_TTL;
    public static final Duration CACHE_COLLECTION_STATUS_TTL = LONG_TTL;
    public static final Duration CACHE_USER_HOT_TTL = LONG_TTL;
    public static final Duration CACHE_FOLLOW_STATUS_TTL = DEFAULT_TTL;
    public static final Duration CACHE_FOLLOW_LIST_TTL = DEFAULT_TTL;
    public static final Duration CACHE_DASHBOARD_TTL = SHORT_TTL;
    public static final Duration CACHE_ANNOUNCEMENT_TTL = LONG_TTL;
    public static final Duration CACHE_READING_HISTORY_TTL = DEFAULT_TTL;
    public static final Duration CACHE_CATEGORIES_TTL = LONG_TTL;
    public static final Duration CACHE_TAGS_TTL = LONG_TTL;
    public static final Duration CACHE_ROLES_TTL = EXTREME_TTL;
    public static final Duration CACHE_USER_ROLES_TTL = Duration.ofDays(1);
    public static final Duration CACHE_ARTICLE_COUNT_TTL = LONG_TTL;
    public static final Duration CACHE_NOTIFICATION_UNREAD_TTL = DEFAULT_TTL;
    public static final Duration CACHE_NOTIFICATION_RECENT_TTL = SHORT_TTL;
    public static final Duration CACHE_VERIFY_CODE_TTL = SHORT_TTL;
    public static final Duration CACHE_LOGIN_ATTEMPT_TTL = MEDIUM_TTL;
    public static final Duration CACHE_LOGIN_LOCK_TTL = MEDIUM_TTL;

    // ==================== 构建方法（动词+名词） ====================

    /**
     * 构建缓存键
     *
     * @param prefix 前缀
     * @param key    键
     * @return 缓存键
     */
    public static String buildCacheKey(String prefix, String key) {
        return prefix + key;
    }

    /**
     * 构建完整的缓存键（包含前缀）
     *
     * @param key 键
     * @return 完整的缓存键
     */
    public static String buildFullCacheKey(String key) {
        return MODULE_PREFIX + key;
    }

    /**
     * 构建带版本号的缓存键
     *
     * @param prefix  前缀
     * @param key     键
     * @param version 版本号
     * @return 带版本号的缓存键
     */
    public static String buildVersionedCacheKey(String prefix, String key, long version) {
        return prefix + key + ":v" + version;
    }

    /**
     * 构建带版本号的缓存键（支持字符串版本号）
     *
     * @param prefix  前缀
     * @param key     键
     * @param version 版本号
     * @return 带版本号的缓存键
     */
    public static String buildVersionedCacheKey(String prefix, String key, String version) {
        return prefix + key + ":v" + version;
    }

    /**
     * 构建带文章和用户版本号的缓存键
     *
     * @param prefix         前缀
     * @param key            键
     * @param articleVersion 文章版本号
     * @param userVersion    用户版本号
     * @return 带版本号的缓存键
     */
    public static String buildArticleUserVersionedCacheKey(String prefix, String key, long articleVersion, long userVersion) {
        return prefix + key + ":v_a" + articleVersion + "_u" + userVersion;
    }

    // ==================== 用户相关 ====================

    /**
     * 构建用户键
     *
     * @param userId 用户ID
     * @return 用户键
     */
    public static String buildUserKey(Long userId) {
        return USER_PREFIX + userId;
    }

    /**
     * 构建用户版本号键
     *
     * @param userId 用户ID
     * @return 用户版本号键
     */
    public static String buildUserVersionKey(Long userId) {
        return VERSION_PREFIX + "user:" + userId;
    }

    // ==================== 文章相关 ====================

    /**
     * 构建文章版本号键
     *
     * @param articleId 文章ID
     * @return 文章版本号键
     */
    public static String buildArticleVersionKey(Long articleId) {
        return VERSION_PREFIX + "article:" + articleId;
    }

    /**
     * 构建文章列表缓存键
     *
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @return 文章列表缓存键
     */
    public static String buildArticleListCacheKey(Integer pageNum, Integer pageSize, Long categoryId, Long tagId) {
        StringBuilder key = new StringBuilder();
        key.append(pageNum).append(":").append(pageSize);
        if (categoryId != null) {
            key.append(":c").append(categoryId);
        }
        if (tagId != null) {
            key.append(":t").append(tagId);
        }
        return buildCacheKey(ARTICLES_PREFIX, key.toString());
    }

    /**
     * 构建文章热点缓存键
     *
     * @param limit     数量限制
     * @param timeRange 时间范围
     * @return 文章热点缓存键
     */
    public static String buildArticleHotCacheKey(Integer limit, String timeRange) {
        return buildCacheKey(ARTICLE_HOT_PREFIX, limit + ":" + timeRange);
    }

    /**
     * 构建文章计数缓存键
     *
     * @param articleId 文章ID
     * @param countType 计数类型
     * @return 文章计数缓存键
     */
    public static String buildArticleCountCacheKey(Long articleId, String countType) {
        return buildCacheKey(ARTICLE_PREFIX, articleId + ":" + countType);
    }

    /**
     * 构建文章详情缓存键
     *
     * @param articleId 文章ID
     * @return 文章详情缓存键
     */
    public static String buildArticleDetailCacheKey(Long articleId) {
        return buildCacheKey(ARTICLE_DETAIL_PREFIX, articleId.toString());
    }

    /**
     * 构建文章收藏缓存键
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 文章收藏缓存键
     */
    public static String buildArticleCollectCacheKey(Long articleId, Long userId) {
        return buildCacheKey(ARTICLE_COLLECT_PREFIX, articleId + ":" + userId);
    }

    /**
     * 构建文章点赞缓存键
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 文章点赞缓存键
     */
    public static String buildArticleLikeCacheKey(Long articleId, Long userId) {
        return buildCacheKey(ARTICLE_LIKE_PREFIX, articleId + ":" + userId);
    }

    /**
     * 构建用户文章列表缓存键
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户文章列表缓存键
     */
    public static String buildUserArticleListCacheKey(Long userId, Integer pageNum, Integer pageSize) {
        return buildCacheKey(USER_ARTICLE_LIST_PREFIX, userId + ":" + pageNum + ":" + pageSize);
    }

    /**
     * 构建最新文章缓存键
     *
     * @param limit 数量限制
     * @return 最新文章缓存键
     */
    public static String buildLatestArticleCacheKey(Integer limit) {
        return buildCacheKey(ARTICLE_LATEST_PREFIX, limit.toString());
    }

    /**
     * 构建轮播文章缓存键
     *
     * @param limit 数量限制
     * @return 轮播文章缓存键
     */
    public static String buildBannerArticleCacheKey(Integer limit) {
        return buildCacheKey(ARTICLE_BANNER_PREFIX, limit.toString());
    }

    // ==================== 其他业务相关 ====================

    /**
     * 构建分类版本号键
     *
     * @param categoryId 分类ID
     * @return 分类版本号键
     */
    public static String buildCategoryVersionKey(Long categoryId) {
        return VERSION_PREFIX + "category:" + categoryId;
    }

    /**
     * 构建标签版本号键
     *
     * @param tagId 标签ID
     * @return 标签版本号键
     */
    public static String buildTagVersionKey(Long tagId) {
        return VERSION_PREFIX + "tag:" + tagId;
    }

    /**
     * 构建评论版本号键
     *
     * @param commentId 评论ID
     * @return 评论版本号键
     */
    public static String buildCommentVersionKey(Long commentId) {
        return VERSION_PREFIX + "comment:" + commentId;
    }

    /**
     * 构建评论回复缓存键
     *
     * @param parentId 父评论ID
     * @return 评论回复缓存键模式
     */
    public static String buildCommentReplyPattern(Long parentId) {
        return buildCacheKey(COMMENT_REPLY_PREFIX, parentId + ":*");
    }

    /**
     * 构建热点数据键
     *
     * @param type 类型
     * @param key  键
     * @return 热点数据键
     */
    public static String buildHotDataKey(String type, String key) {
        return HOT_DATA_PREFIX + type + ":" + key;
    }

    /**
     * 构建验证码键
     *
     * @param account 账号
     * @param purpose 用途
     * @return 验证码键
     */
    public static String buildVerifyCodeKey(String account, String purpose) {
        return VERIFICATION_CODE_PREFIX + purpose + ":" + account;
    }

    /**
     * 构建发送限制键
     *
     * @param account 账号
     * @param type    类型
     * @return 发送限制键
     */
    public static String buildSendLimitKey(String account, String type) {
        return RATE_LIMIT_PREFIX + "send:" + type + ":" + account;
    }
}
