package com.inkstage.constant;

/**
 * Redis键常量类
 * 定义Redis键的命名规范和前缀
 */
public class RedisKeyConstants {

    // ==================== 验证码相关 ====================
    /**
     * 验证码前缀
     * 格式: verify_code:{account}:{purpose}
     */
    public static final String VERIFY_CODE_PREFIX = "verify_code:";

    /**
     * 验证码发送频率限制前缀
     * 格式: send_limit:{account}:{purpose}
     */
    public static final String SEND_LIMIT_PREFIX = "send_limit:";

    // ==================== 用户相关 ====================
    /**
     * 用户信息前缀
     * 格式: user:{user_id}
     */
    public static final String USER_PREFIX = "user:";

    /**
     * 用户会话前缀
     * 格式: session:{session_id}
     */
    public static final String SESSION_PREFIX = "session:";

    /**
     * 用户登录令牌前缀
     * 格式: token:{user_id}
     */
    public static final String TOKEN_PREFIX = "token:";

    /**
     * 用户权限前缀
     * 格式: permission:{user_id}
     */
    public static final String PERMISSION_PREFIX = "permission:";

    // ==================== 注册相关 ====================
    /**
     * 注册频率限制前缀
     * 格式: register_limit:{client_ip}
     */
    public static final String REGISTER_LIMIT_PREFIX = "register_limit:";

    // ==================== 缓存相关 ====================
    /**
     * 缓存前缀
     * 格式: cache:{cache_name}:{key}
     */
    public static final String CACHE_PREFIX = "cache:";

    /**
     * 热点数据前缀
     * 格式: hot:{data_type}:{key}
     */
    public static final String HOT_DATA_PREFIX = "hot:";

    // ==================== 限流相关 ====================
    /**
     * API限流前缀
     * 格式: rate_limit:{api_path}:{client_ip}
     */
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";

    /**
     * IP限流前缀
     * 格式: ip_limit:{client_ip}
     */
    public static final String IP_LIMIT_PREFIX = "ip_limit:";

    // ==================== 分布式锁相关 ====================
    /**
     * 分布式锁前缀
     * 格式: lock:{lock_name}
     */
    public static final String LOCK_PREFIX = "lock:";

    /**
     * 秒杀锁前缀
     * 格式: seckill:{product_id}
     */
    public static final String SECKILL_LOCK_PREFIX = "seckill:";

    // ==================== 消息队列相关 ====================
    /**
     * 消息队列前缀
     * 格式: queue:{queue_name}
     */
    public static final String QUEUE_PREFIX = "queue:";

    /**
     * 延迟队列前缀
     * 格式: delay_queue:{queue_name}
     */
    public static final String DELAY_QUEUE_PREFIX = "delay_queue:";

    // ==================== 排行榜相关 ====================
    /**
     * 排行榜前缀
     * 格式: rank:{rank_name}
     */
    public static final String RANK_PREFIX = "rank:";

    // ==================== 地理位置相关 ====================
    /**
     * 地理位置前缀
     * 格式: geo:{geo_name}
     */
    public static final String GEO_PREFIX = "geo:";

    // ==================== 布隆过滤器相关 ====================
    /**
     * 布隆过滤器前缀
     * 格式: bloom:{filter_name}
     */
    public static final String BLOOM_PREFIX = "bloom:";

    // ==================== 通知相关 ====================
    /**
     * 未读通知数量前缀
     * 格式: notification:unread:{user_id}
     */
    public static final String NOTIFICATION_UNREAD_COUNT = "notification:unread:";

    /**
     * 构建验证码键
     * @param account 账号
     * @param purpose 用途
     * @return 验证码键
     */
    public static String buildVerifyCodeKey(String account, String purpose) {
        return VERIFY_CODE_PREFIX + account + ":" + purpose;
    }

    /**
     * 构建发送频率限制键
     * @param account 账号
     * @param purpose 用途
     * @return 发送频率限制键
     */
    public static String buildSendLimitKey(String account, String purpose) {
        return SEND_LIMIT_PREFIX + account + ":" + purpose;
    }

    /**
     * 构建用户信息键
     * @param userId 用户ID
     * @return 用户信息键
     */
    public static String buildUserKey(Long userId) {
        return USER_PREFIX + userId;
    }

    /**
     * 构建注册频率限制键
     * @param clientIp 客户端IP
     * @return 注册频率限制键
     */
    public static String buildRegisterLimitKey(String clientIp) {
        return REGISTER_LIMIT_PREFIX + clientIp;
    }

    /**
     * 构建登录失败限制键
     * @param clientIp 客户端IP
     * @return 登录失败限制键
     */
    public static String buildLoginFailKey(String clientIp) {
        return "login_fail:" + clientIp;
    }

    /**
     * 构建API限流键
     * @param apiPath API路径
     * @param clientIp 客户端IP
     * @return API限流键
     */
    public static String buildRateLimitKey(String apiPath, String clientIp) {
        return RATE_LIMIT_PREFIX + apiPath + ":" + clientIp;
    }

    /**
     * 构建分布式锁键
     * @param lockName 锁名称
     * @return 分布式锁键
     */
    public static String buildLockKey(String lockName) {
        return LOCK_PREFIX + lockName;
    }

    /**
     * 构建缓存键
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @return 缓存键
     */
    public static String buildCacheKey(String cacheName, String key) {
        return CACHE_PREFIX + cacheName + ":" + key;
    }

    /**
     * 构建热点数据键
     * @param dataType 数据类型
     * @param key 数据键
     * @return 热点数据键
     */
    public static String buildHotDataKey(String dataType, String key) {
        return HOT_DATA_PREFIX + dataType + ":" + key;
    }

    /**
     * 构建排行榜键
     * @param rankName 排行榜名称
     * @return 排行榜键
     */
    public static String buildRankKey(String rankName) {
        return RANK_PREFIX + rankName;
    }
}
