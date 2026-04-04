package com.inkstage.cache.constant;

/**
 * Redis键常量类
 */
public class RedisKeyConstants {

    // 刷新令牌前缀
    public static final String REFRESH_TOKEN_PREFIX = "inkstage:refresh_token:";

    // 用户刷新令牌前缀
    public static final String USER_REFRESH_TOKEN_PREFIX = "inkstage:user:refresh_tokens:";

    // 刷新令牌黑名单前缀
    public static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "inkstage:refresh_token_blacklist:";

    // 用户登录尝试次数前缀
    public static final String LOGIN_ATTEMPT_PREFIX = "inkstage:login:attempt:";

    // 用户登录锁定前缀
    public static final String LOGIN_LOCK_PREFIX = "inkstage:login:lock:";

    // 用户会话前缀
    public static final String USER_SESSION_PREFIX = "inkstage:user_session:";

    // 验证码前缀
    public static final String VERIFICATION_CODE_PREFIX = "inkstage:verify:";

    // 用户信息缓存前缀
    public static final String USER_INFO_PREFIX = "inkstage:user:info:";

    // 管理员信息缓存前缀
    public static final String ADMIN_INFO_PREFIX = "inkstage:admin:info:";

    // 权限缓存前缀
    public static final String PERMISSION_PREFIX = "inkstage:permission:";

    // 角色缓存前缀
    public static final String ROLE_PREFIX = "inkstage:role:";

    // 限流前缀
    public static final String RATE_LIMIT_PREFIX = "inkstage:rate:";

    // 分布式锁前缀
    public static final String LOCK_PREFIX = "inkstage:lock:";

    // 系统配置前缀
    public static final String SYSTEM_CONFIG_PREFIX = "inkstage:sys_config:";

    // 热点数据前缀
    public static final String HOT_DATA_PREFIX = "inkstage:hot:";

    // 通知未读计数
    public static final String NOTIFICATION_UNREAD_COUNT = "inkstage:notify:unread:count:";

    // 通知按分类未读计数
    public static final String NOTIFICATION_UNREAD_COUNT_BY_CATEGORY = "inkstage:notify:unread:category:";

    // 通知最近列表
    public static final String NOTIFICATION_RECENT_LIST = "inkstage:notify:recent:list:";

    // 用户前缀
    public static final String USER_PREFIX = "inkstage:user:";

    // 文章前缀
    public static final String ARTICLE_PREFIX = "inkstage:article:";

    // 文章列表前缀
    public static final String ARTICLES_PREFIX = "inkstage:articles:";

    // 文章热点前缀
    public static final String ARTICLE_HOT_PREFIX = "inkstage:article:hot:";

    // 文章计数前缀
    public static final String ARTICLE_COUNT_PREFIX = "inkstage:article:";

    // 分类前缀
    public static final String CATEGORY_PREFIX = "inkstage:category:";

    // 标签前缀
    public static final String TAG_PREFIX = "inkstage:tag:";

    // 评论前缀
    public static final String COMMENT_PREFIX = "inkstage:comment:";

    // 评论回复前缀
    public static final String COMMENT_REPLY_PREFIX = "inkstage:comment:reply:";

    // 关注前缀
    public static final String FOLLOW_PREFIX = "inkstage:follow:";

    // 阅读历史前缀
    public static final String READING_HISTORY_PREFIX = "inkstage:reading:history:";

    // 文章详情前缀
    public static final String ARTICLE_DETAIL_PREFIX = "inkstage:article:detail:";

    // 文章收藏前缀
    public static final String ARTICLE_COLLECT_PREFIX = "inkstage:article:collect:";

    // 文章点赞前缀
    public static final String ARTICLE_LIKE_PREFIX = "inkstage:article:like:";

    // 用户文章列表前缀
    public static final String USER_ARTICLE_LIST_PREFIX = "inkstage:user:articles:";

    // 热门用户前缀
    public static final String USER_HOT_PREFIX = "inkstage:user:hot:";

    // 最新文章前缀
    public static final String ARTICLE_LATEST_PREFIX = "inkstage:article:latest:";

    // 轮播文章前缀
    public static final String ARTICLE_BANNER_PREFIX = "inkstage:article:banner:";

    // 文章搜索前缀
    public static final String ARTICLE_SEARCH_PREFIX = "inkstage:article:search:";

    // 我的文章列表前缀
    public static final String ARTICLE_MY_PREFIX = "inkstage:cache:article:my:";

    // @Cache注解使用的缓存名称
    public static final String CACHE_DASHBOARD = "dashboard";
    public static final String CACHE_FOLLOW_STATUS = "follow:status";
    public static final String CACHE_FOLLOW_LIST = "follow:list";
    public static final String CACHE_ANNOUNCEMENT = "announcement";
    public static final String CACHE_READING_HISTORY = "reading:history";
    public static final String CACHE_COMMENT_LIST = "comment:list";
    public static final String CACHE_COMMENT_REPLIES = "comment:replies";
    public static final String CACHE_COMMENT_ADMIN = "comment:admin";
    public static final String CACHE_USER_HOT = "user:hot";
    public static final String CACHE_COLLECTION_STATUS = "collection:status";
    public static final String CACHE_LIKE_STATUS = "like:status";
    public static final String CACHE_ARTICLE_DETAIL = "article:detail";
    public static final String CACHE_ARTICLE_LIST = "article:list";
    public static final String CACHE_ARTICLE_HOT = "article:hot";
    public static final String CACHE_ARTICLE_LATEST = "article:latest";
    public static final String CACHE_ARTICLE_BANNER = "article:banner";
    public static final String CACHE_ARTICLE_USER = "article:user";
    public static final String CACHE_ARTICLE_USER_RELATED = "article:user:related";
    public static final String CACHE_ARTICLE_SEARCH = "article:search";
    public static final String CACHE_ARTICLE_MY = "article:my";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_USER_ROLES = "user:roles";
    public static final String CACHE_ROLES = "roles";
    public static final String CACHE_TAGS = "tags";

    // RedisUtil使用的缓存名称
    public static final String CACHE_ARTICLE_COUNT = "inkstage:article:count";
    public static final String CACHE_NOTIFICATION_UNREAD = "inkstage:notification:unread";
    public static final String CACHE_NOTIFICATION_RECENT = "inkstage:notification:recent";
    public static final String CACHE_VERIFY_CODE = "inkstage:verify:code";
    public static final String CACHE_LOGIN_ATTEMPT = "inkstage:login:attempt";
    public static final String CACHE_LOGIN_LOCK = "inkstage:login:lock";

    // 版本号前缀
    public static final String VERSION_PREFIX = "v:";

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
        return "inkstage:" + key;
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
        return buildCacheKey(ARTICLE_COUNT_PREFIX, articleId + ":" + countType);
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
     * 构建热门用户缓存键
     *
     * @param limit 数量限制
     * @return 热门用户缓存键
     */
    public static String buildHotUserCacheKey(Integer limit) {
        return buildCacheKey(USER_HOT_PREFIX, limit.toString());
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
