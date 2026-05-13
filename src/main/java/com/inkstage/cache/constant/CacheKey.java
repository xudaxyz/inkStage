package com.inkstage.cache.constant;

public final class CacheKey {

    private static final String PREFIX = "inkstage:";

    private CacheKey() {
    }

    // ==================== 认证相关 ====================
    public static final String REFRESH_TOKEN = PREFIX + "refresh_token:";
    public static final String USER_REFRESH_TOKENS = PREFIX + "user:refresh_tokens:";
    public static final String REFRESH_TOKEN_BLACKLIST = PREFIX + "refresh_token_black:";
    public static final String LOGIN_ATTEMPT = PREFIX + "login:attempt:";
    public static final String LOGIN_LOCK = PREFIX + "login:lock:";
    public static final String USER_SESSION = PREFIX + "user_session:";
    public static final String VERIFICATION_CODE = PREFIX + "verify:";
    public static final String RATE_LIMIT = PREFIX + "rate:";

    // ==================== 用户相关 ====================
    public static final String USER = PREFIX + "user:";
    public static final String USER_INFO = PREFIX + "user:info:";
    public static final String ADMIN_INFO = PREFIX + "admin:info:";
    public static final String USER_ARTICLES = PREFIX + "user:articles:";
    public static final String USER_HOT = PREFIX + "user:hot:";

    // ==================== 文章相关 ====================
    public static final String ARTICLE = PREFIX + "article:";
    public static final String ARTICLES = PREFIX + "articles:";
    public static final String ARTICLE_HOT = PREFIX + "article:hot:";
    public static final String ARTICLE_DETAIL = PREFIX + "article:detail:";
    public static final String ARTICLE_COLLECT = PREFIX + "article:collect:";
    public static final String ARTICLE_COLLECT_STATUS = PREFIX + "article:collect:status:";
    public static final String ARTICLE_LIKE = PREFIX + "article:like:";
    public static final String ARTICLE_LIKE_STATUS = PREFIX + "article:like:status:";
    public static final String ARTICLE_COLLECTION_STATUS = PREFIX + "article:collection:status:";
    public static final String ARTICLE_LATEST = PREFIX + "article:latest:";
    public static final String ARTICLE_BANNER = PREFIX + "article:banner:";
    public static final String ARTICLE_SEARCH = PREFIX + "article:search:";

    // ==================== 专栏相关 ====================
    public static final String COLUMN = PREFIX + "column:";
    public static final String COLUMN_HOT = PREFIX + "column:hot:";
    public static final String COLUMN_DETAIL = PREFIX + "column:detail:";
    public static final String COLUMN_ARTICLES = PREFIX + "column:articles:";
    public static final String COLUMN_SUBSCRIPTION = PREFIX + "column:subscribe:";
    public static final String COLUMN_SUBSCRIPTION_LIST = PREFIX + "column:subscribe:list:";
    public static final String COLUMN_SUBSCRIPTION_STATUS = PREFIX + "column:subscribe:status:";

    // ==================== 其他业务相关 ====================
    public static final String CATEGORY = PREFIX + "category:";
    public static final String TAG = PREFIX + "tag:";
    public static final String COMMENT_LIST = PREFIX + "comment:list:";
    public static final String COMMENT_REPLY = PREFIX + "comment:reply:";
    public static final String FOLLOW = PREFIX + "follow:";
    public static final String READING_HISTORY = PREFIX + "reading:history:";
    public static final String PERMISSION = PREFIX + "permission:";
    public static final String ROLE = PREFIX + "role:";
    public static final String LOCK = PREFIX + "lock:";
    public static final String SYSTEM_CONFIG = PREFIX + "sys_config:";
    public static final String HOT_DATA = PREFIX + "hot:";

    // ==================== 通知相关 ====================
    public static final String NOTIFICATION_UNREAD_COUNT = PREFIX + "notify:unread:count:";
    public static final String NOTIFICATION_UNREAD_COUNT_BY_CATEGORY = PREFIX + "notify:unread:category:";
    public static final String NOTIFICATION_RECENT_LIST = PREFIX + "notify:recent:list:";
    public static final String NOTIFICATION_SETTING = PREFIX + "notify:setting:";

    // ==================== 键构建方法 ====================

    public static String keyForArticleDetail(Long articleId) {
        return ARTICLE_DETAIL + articleId;
    }

    public static String keyForArticleList(Integer pageNum, Integer pageSize, Long categoryId, Long tagId) {
        StringBuilder key = new StringBuilder(ARTICLES);
        key.append(pageNum).append(":").append(pageSize);
        if (categoryId != null) {
            key.append(":c").append(categoryId);
        }
        if (tagId != null) {
            key.append(":t").append(tagId);
        }
        return key.toString();
    }

    public static String keyForArticleHot(Integer limit, String timeRange) {
        return ARTICLE_HOT + limit + ":" + (timeRange != null ? timeRange : "week");
    }

    public static String keyForArticleLatest(Integer limit) {
        return ARTICLE_LATEST + limit;
    }

    public static String keyForArticleBanner(Integer limit) {
        return ARTICLE_BANNER + limit;
    }

    public static String keyForArticleCollectStatus(Long articleId, Long userId) {
        return ARTICLE_COLLECT_STATUS + articleId + ":" + userId;
    }

    public static String keyForArticleLikeStatus(Long articleId, Long userId) {
        return ARTICLE_LIKE_STATUS + articleId + ":" + userId;
    }

    public static String keyForUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        return USER_ARTICLES + userId + ":" + pageNum + ":" + pageSize;
    }

    public static String keyForUserInfo(Long userId) {
        return USER_INFO + userId;
    }

    public static String keyForColumnDetail(Long columnId) {
        return COLUMN_DETAIL + columnId;
    }

    public static String keyForColumnList(Integer pageNum, Integer pageSize, String keyword) {
        return COLUMN + pageNum + ":" + pageSize + ":" + (keyword != null ? keyword : "");
    }

    public static String keyForColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy) {
        return COLUMN_ARTICLES + columnId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "ASC");
    }

    public static String keyForColumnArticleSearch(Long columnId, String keyword, Integer pageNum, Integer pageSize) {
        return COLUMN_ARTICLES + "search:" + columnId + ":" + keyword + ":" + pageNum + ":" + pageSize;
    }

    public static String keyForColumnHot(Integer limit) {
        return COLUMN_HOT + limit;
    }

    public static String keyForColumnSubscriptionStatus(Long columnId, Long userId) {
        return COLUMN_SUBSCRIPTION_STATUS + userId + ":" + columnId;
    }

    public static String keyForCommentList(Long articleId, Integer pageNum, Integer pageSize, String sortBy) {
        return COMMENT_LIST + articleId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "default");
    }

    public static String keyForCommentReply(Long parentId, Integer pageNum, Integer pageSize, String sortBy) {
        return COMMENT_REPLY + parentId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "default");
    }

    public static String keyForCommentList(Long articleId, Integer pageNum, Integer pageSize, String sortBy, Integer maxReplies, Integer commentVersion) {
        return COMMENT_LIST + articleId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "default") + ":" + (maxReplies != null ? maxReplies : 0) + ":" + (commentVersion != null ? commentVersion : 1);
    }

    public static String keyForArticleCollectionStatus(Long articleId, Long userId) {
        return ARTICLE_COLLECTION_STATUS + articleId + ":" + userId;
    }

    public static String keyForVerifyCode(String account, String purpose) {
        return VERIFICATION_CODE + purpose + ":" + account;
    }

    public static String keyForLoginAttempt(String account) {
        return LOGIN_ATTEMPT + account;
    }

    public static String keyForNotificationUnreadCount(Long userId) {
        return NOTIFICATION_UNREAD_COUNT + userId;
    }

    public static String keyForNotificationSetting(Long userId) {
        return NOTIFICATION_SETTING + userId;
    }

    public static String keyForUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        return USER_HOT + userId + ":" + (excludeArticleId != null ? excludeArticleId : 0) + ":" + limit;
    }

    public static String keyForArticleSearch(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        return ARTICLE_SEARCH + keyword + ":" + (sortBy != null ? sortBy : "default") + ":" + pageNum + ":" + pageSize;
    }

    // ==================== 分类相关 ====================
    public static String keyForCategoryActive() {
        return CATEGORY + "active";
    }

    // ==================== 标签相关 ====================
    public static String keyForTagsActive() {
        return TAG + "active";
    }

    // ==================== 角色相关 ====================
    public static String keyForRole(Long roleId) {
        return ROLE + roleId;
    }

    // ==================== 用户角色相关 ====================
    public static String keyForUserRoles(Long userId) {
        return USER + "roles:" + userId;
    }

    // ==================== 关注相关 ====================
    public static String keyForFollowStatus(Long userId, Long followeeId) {
        return FOLLOW + "status:" + userId + ":" + followeeId;
    }

    public static String keyForFollowList(Long userId, Integer pageNum, Integer pageSize, String type) {
        return FOLLOW + "list:" + userId + ":" + (type != null ? type : "following") + ":" + pageNum + ":" + pageSize;
    }

    // ==================== 阅读历史相关 ====================
    public static String keyForReadingHistory(Long userId, Integer pageNum, Integer pageSize) {
        return READING_HISTORY + userId + ":" + pageNum + ":" + pageSize;
    }

    // ==================== 仪表盘相关 ====================
    public static String keyForDashboard(String key) {
        return HOT_DATA + "dashboard:" + key;
    }

    // ==================== 系统公告相关 ====================
    public static String keyForAnnouncementList(Integer pageNum, Integer pageSize) {
        return HOT_DATA + "announcement:" + pageNum + ":" + pageSize;
    }

    public static String keyForAnnouncementDetail(Long id) {
        return HOT_DATA + "announcement:detail:" + id;
    }

    // ==================== 用户热门相关 ====================
    public static String keyForUserHot(Integer limit) {
        return USER_HOT + "list:" + limit;
    }

    // ==================== 限流相关 ====================
    public static String keyForRateLimit(String type, String account) {
        return RATE_LIMIT + "send:" + type + ":" + account;
    }

    // ==================== 文章计数相关 ====================
    public static String keyForArticleCount(Long articleId, String countType) {
        return ARTICLE + articleId + ":" + countType;
    }
}
