package com.inkstage.service;

import java.time.Duration;

/**
 * 令牌存储服务接口
 * 用于管理刷新令牌的存储和验证
 */
public interface TokenStoreService {

    /**
     * 存储刷新令牌
     * @param userId 用户ID
     * @param refreshToken 刷新令牌
     * @param expiry 过期时间
     * @return 令牌ID
     */
    String storeRefreshToken(Long userId, String refreshToken, Duration expiry);

    /**
     * 验证刷新令牌
     * @param userId 用户ID
     * @param refreshToken 刷新令牌
     * @return 是否有效
     */
    boolean validateRefreshToken(Long userId, String refreshToken);

    /**
     * 撤销刷新令牌
     * @param userId 用户ID
     * @param refreshToken 刷新令牌
     */
    void revokeRefreshToken(Long userId, String refreshToken);

    /**
     * 撤销用户所有刷新令牌
     * @param userId 用户ID
     */
    void revokeAllRefreshTokens(Long userId);

    /**
     * 获取用户刷新令牌数量
     * @param userId 用户ID
     * @return 令牌数量
     */
    long getRefreshTokenCount(Long userId);
}
