package com.inkstage.utils.model;

import lombok.Data;

/**
 * Redis使用情况统计类
 */
@Data
public class RedisUsageStats {
    /**
     * 总键数
     */
    private long totalKeys;
    /**
     * 验证码键数
     */
    private long verifyCodeKeys;
    /**
     * 登录限制键数
     */
    private long sendLimitKeys;
    /**
     * 注册限制键数
     */
    private long registerLimitKeys;
    /**
     * 用户键数
     */
    private long userKeys;
    /**
     * 会话键数
     */
    private long sessionKeys;
    /**
     * 令牌键数
     */
    private long tokenKeys;
    /**
     * 缓存键数
     */
    private long cacheKeys;
}
