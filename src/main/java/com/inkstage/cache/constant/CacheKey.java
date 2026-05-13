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
    public static final String SEND_RATE_LIMIT = PREFIX + "send:rate:";

    // ==================== 用户相关 ====================
    public static final String USER_INFO = PREFIX + "user:info:";
    public static final String USER_ROLES = PREFIX + "user:roles:";
    public static final String USER_ARTICLES = PREFIX + "user:articles:";
    public static final String USER_HOT = PREFIX + "user:hot:";
    public static final String USER_HOT_LIST = PREFIX + "user:hot:list:";

    // ==================== 文章相关 ====================
    public static final String ARTICLE = PREFIX + "article:";
    public static final String ARTICLES = PREFIX + "articles:";
    public static final String ARTICLE_HOT = PREFIX + "article:hot:";
    public static final String ARTICLE_DETAIL = PREFIX + "article:detail:";
    public static final String ARTICLE_COLLECT_STATUS = PREFIX + "article:collect:status:";
    public static final String ARTICLE_LIKE_STATUS = PREFIX + "article:like:status:";
    public static final String ARTICLE_LATEST = PREFIX + "article:latest:";
    public static final String ARTICLE_BANNER = PREFIX + "article:banner:";
    public static final String ARTICLE_SEARCH = PREFIX + "article:search:";

    // ==================== 专栏相关 ====================
    public static final String COLUMN = PREFIX + "column:";
    public static final String COLUMN_HOT = PREFIX + "column:hot:";
    public static final String COLUMN_DETAIL = PREFIX + "column:detail:";
    public static final String COLUMN_ARTICLES = PREFIX + "column:articles:";
    public static final String COLUMN_ARTICLES_SEARCH = PREFIX + "column:articles:search:";
    public static final String COLUMN_SUBSCRIPTION_LIST = PREFIX + "column:subscription:list:";
    public static final String COLUMN_SUBSCRIPTION_USER_COUNT = PREFIX + "column:subscription:user:count:";
    public static final String COLUMN_SUBSCRIPTION_COLUMN_COUNT = PREFIX + "column:subscription:column:count:";
    public static final String COLUMN_SUBSCRIPTION_STATUS = PREFIX + "column:subscription:status:";

    // ==================== 其他业务相关 ====================
    public static final String CATEGORY = PREFIX + "category:";
    public static final String ACTIVE_CATEGORY = PREFIX + "category:active";
    public static final String TAG = PREFIX + "tag:";
    public static final String ACTIVE_TAG = PREFIX + "tag:active";
    public static final String COMMENT_LIST = PREFIX + "comment:list:";
    public static final String COMMENT_REPLY = PREFIX + "comment:reply:";
    public static final String FOLLOW = PREFIX + "follow:";
    public static final String FOLLOWER_LIST = PREFIX + "follower:list:";
    public static final String FOLLOWING_LIST = PREFIX + "following:list:";
    public static final String FOLLOW_STATUS = PREFIX + "follow:status:";
    public static final String USER_FOLLOWING_COUNT = PREFIX + "user:following:count:";
    public static final String USER_FOLLOWER_COUNT = PREFIX + "user:follower:count:";
    public static final String READING_HISTORY = PREFIX + "reading:history:";
    public static final String USER_ARTICLE_READING_HISTORY = PREFIX + "user:article:reading:history:";
    public static final String ROLE = PREFIX + "role:";
    public static final String SYSTEM_CONFIG = PREFIX + "sys_config:";
    public static final String HOT_DATA = PREFIX + "hot:";
    public static final String HOT_ANNOUNCEMENT = PREFIX + "hot:announcement:";
    public static final String HOT_ANNOUNCEMENT_PUBLISHED = PREFIX + "hot:announcement:published:";
    public static final String HOT_ANNOUNCEMENT_DETAIL = PREFIX + "hot:announcement:detail:";
    public static final String DASHBOARD_STATUS = PREFIX + "dashboard:status:";

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
            key.append(":c:").append(categoryId);
        }
        if (tagId != null) {
            key.append(":t:").append(tagId);
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
        return COLUMN + pageNum + ":" + pageSize + (keyword != null && !keyword.isEmpty() ? (":" + keyword) : "");
    }

    public static String keyForColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy) {
        return COLUMN_ARTICLES + columnId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "ASC");
    }

    public static String keyForColumnArticleSearch(Long columnId, String keyword, Integer pageNum, Integer pageSize) {
        return COLUMN_ARTICLES_SEARCH + columnId + ":" + keyword + ":" + pageNum + ":" + pageSize;
    }

    public static String keyForColumnHot(Integer limit) {
        return COLUMN_HOT + limit;
    }

    public static String keyForColumnSubscriptionStatus(Long columnId, Long userId) {
        return COLUMN_SUBSCRIPTION_STATUS + userId + ":" + columnId;
    }

    public static String keyForCommentReply(Long parentId, Integer pageNum, Integer pageSize, String sortBy) {
        return COMMENT_REPLY + parentId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "default");
    }

    public static String keyForCommentList(Long articleId, Integer pageNum, Integer pageSize, String sortBy, Integer maxReplies, Integer commentVersion) {
        return COMMENT_LIST + articleId + ":" + pageNum + ":" + pageSize + ":" + (sortBy != null ? sortBy : "default") + ":" + (maxReplies != null ? maxReplies : 0) + ":" + (commentVersion != null ? commentVersion : 1);
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
        return ACTIVE_CATEGORY;
    }

    // ==================== 标签相关 ====================
    public static String keyForTagsActive() {
        return ACTIVE_TAG;
    }

    // ==================== 角色相关 ====================
    public static String keyForRole(Long roleId) {
        return ROLE + roleId;
    }

    // ==================== 用户角色相关 ====================
    public static String keyForUserRoles(Long userId) {
        return USER_ROLES + userId;
    }

    // ==================== 关注相关 ====================
    public static String keyForFollowStatus(Long followerId, Long followingId) {
        return FOLLOW_STATUS + followerId + ":" + followingId;
    }

    public static String keyForFollowerList(Long userId, Integer pageNum, Integer pageSize) {
        return FOLLOWER_LIST + userId + ":" + pageNum + ":" + pageSize;
    }

    public static String keyForFollowingList(Long userId, Integer pageNum, Integer pageSize) {
        return FOLLOWING_LIST + userId + ":" + pageNum + ":" + pageSize;
    }

    // ==================== 阅读历史相关 ====================
    public static String keyForReadingHistory(Long userId, Integer pageNum, Integer pageSize) {
        return READING_HISTORY + userId + ":" + pageNum + ":" + pageSize;
    }

    // ==================== 仪表盘相关 ====================
    public static String keyForDashboardStatus(String key) {
        return DASHBOARD_STATUS + key;
    }

    public static String keyForAnnouncementDetail(Long id) {
        return HOT_ANNOUNCEMENT_DETAIL + id;
    }

    // ==================== 用户热门相关 ====================
    public static String keyForUserHot(Integer limit) {
        return USER_HOT_LIST + limit;
    }

    // ==================== 限流相关 ====================
    public static String keyForSendRateLimit(String type, String account) {
        return SEND_RATE_LIMIT + type + ":" + account;
    }

    // ==================== 文章计数相关 ====================
    public static String keyForArticleCount(Long articleId, String countType) {
        return ARTICLE + countType + ":" + articleId;
    }

    // ==================== 公告相关 ====================
    public static String keyForHotAnnouncementPublished() {
        return HOT_ANNOUNCEMENT_PUBLISHED;
    }

    // ==================== 关注计数相关 ====================
    public static String keyForUserFollowingCount(Long followerId) {
        return USER_FOLLOWING_COUNT + followerId;
    }

    public static String keyForUserFollowerCount(Long followingId) {
        return USER_FOLLOWER_COUNT + followingId;
    }

    // ==================== 专栏订阅列表相关 ====================
    public static String keyForColumnSubscriptionList(Long userId, Integer pageNum, Integer pageSize, String keyword) {
        return COLUMN_SUBSCRIPTION_LIST + userId + ":" + pageNum + ":" + pageSize + ":" + (keyword != null ? keyword : "");
    }

    public static String keyForColumnSubscriptionUserCount(Long userId) {
        return COLUMN_SUBSCRIPTION_USER_COUNT + userId;
    }

    public static String keyForColumnSubscriptionColumnCount(Long columnId) {
        return COLUMN_SUBSCRIPTION_COLUMN_COUNT + columnId;
    }

    public static String keyForRefreshToken(Long userId, String tokenId) {
        return REFRESH_TOKEN + userId + ":" + tokenId;
    }

    public static String keyForUserArticleReadingHistory(Long userId, Long articleId) {
        return USER_ARTICLE_READING_HISTORY + userId + ":" + articleId;
    }
}
