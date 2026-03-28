package com.inkstage.constant;

/**
 * Redis键常量类
 */
public class RedisKeyConstants {

    // 刷新令牌前缀
    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    // 用户刷新令牌前缀
    public static final String USER_REFRESH_TOKEN_PREFIX = "user_refresh_tokens:";
    
    // 用户登录尝试次数前缀
    public static final String LOGIN_ATTEMPT_PREFIX = "login_attempt:";
    
    // 用户登录锁定前缀
    public static final String LOGIN_LOCK_PREFIX = "login_lock:";
    
    // 用户会话前缀
    public static final String USER_SESSION_PREFIX = "user_session:";
    
    // 验证码前缀
    public static final String VERIFICATION_CODE_PREFIX = "verification_code:";
    
    // 用户信息缓存前缀
    public static final String USER_INFO_PREFIX = "user_info:";
    
    // 管理员信息缓存前缀
    public static final String ADMIN_INFO_PREFIX = "admin_info:";
    
    // 权限缓存前缀
    public static final String PERMISSION_PREFIX = "permission:";
    
    // 角色缓存前缀
    public static final String ROLE_PREFIX = "role:";
    
    // 限流前缀
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    // 分布式锁前缀
    public static final String LOCK_PREFIX = "lock:";
    
    // 系统配置前缀
    public static final String SYSTEM_CONFIG_PREFIX = "system_config:";
    
    // 热点数据前缀
    public static final String HOT_DATA_PREFIX = "hot_data:";
    
    // 通知未读计数
    public static final String NOTIFICATION_UNREAD_COUNT = "notification:unread:count:";
    
    // 通知按分类未读计数
    public static final String NOTIFICATION_UNREAD_COUNT_BY_CATEGORY = "notification:unread:count:category:";
    
    // 通知最近列表
    public static final String NOTIFICATION_RECENT_LIST = "notification:recent:list:";
    
    // 用户前缀
    public static final String USER_PREFIX = "user:";
    
    /**
     * 构建缓存键
     * @param prefix 前缀
     * @param key 键
     * @return 缓存键
     */
    public static String buildCacheKey(String prefix, String key) {
        return prefix + key;
    }
    
    /**
     * 构建用户键
     * @param userId 用户ID
     * @return 用户键
     */
    public static String buildUserKey(Long userId) {
        return USER_PREFIX + userId;
    }
    
    /**
     * 构建热点数据键
     * @param type 类型
     * @param key 键
     * @return 热点数据键
     */
    public static String buildHotDataKey(String type, String key) {
        return HOT_DATA_PREFIX + type + ":" + key;
    }
    
    /**
     * 构建验证码键
     * @param account 账号
     * @param purpose 用途
     * @return 验证码键
     */
    public static String buildVerifyCodeKey(String account, String purpose) {
        return VERIFICATION_CODE_PREFIX + purpose + ":" + account;
    }
    
    /**
     * 构建发送限制键
     * @param account 账号
     * @param type 类型
     * @return 发送限制键
     */
    public static String buildSendLimitKey(String account, String type) {
        return RATE_LIMIT_PREFIX + "send:" + type + ":" + account;
    }
}
