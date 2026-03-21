package com.inkstage.service.impl;

import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.service.TokenStoreService;
import com.inkstage.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Redis令牌存储服务实现类
 * 用于管理刷新令牌的存储和验证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenStoreServiceImpl implements TokenStoreService {

    private final RedisUtil redisUtil;

    @Override
    public String storeRefreshToken(Long userId, String refreshToken, Duration expiry) {
        // 生成唯一的令牌ID
        String tokenId = UUID.randomUUID().toString();
        // 构建Redis键
        String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;

        try {
            // 存储刷新令牌到Redis
            redisUtil.set(key, refreshToken, expiry.getSeconds());
            // 同时存储用户ID和令牌ID的映射，用于后续验证
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            redisUtil.sAdd(userTokenKey, tokenId);

            log.info("刷新令牌存储成功，用户ID: {}, 令牌ID: {}", userId, tokenId);
            return tokenId;
        } catch (Exception e) {
            log.error("存储刷新令牌失败，用户ID: {}", userId, e);
            throw new RuntimeException("存储刷新令牌失败", e);
        }
    }

    @Override
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        try {
            // 获取用户的所有刷新令牌ID
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            Set<Object> tokenIdObjects = redisUtil.sMembers(userTokenKey);

            if (tokenIdObjects == null || tokenIdObjects.isEmpty()) {
                log.warn("用户无刷新令牌，用户ID: {}", userId);
                return false;
            }

            // 转换为String类型
            Set<String> tokenIds = new java.util.HashSet<>();
            for (Object obj : tokenIdObjects) {
                if (obj != null) {
                    tokenIds.add(obj.toString());
                }
            }

            // 验证刷新令牌是否有效
            for (String tokenId : tokenIds) {
                String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;
                String storedToken = redisUtil.get(key, String.class);

                if (refreshToken.equals(storedToken)) {
                    log.info("刷新令牌验证成功，用户ID: {}, 令牌ID: {}", userId, tokenId);
                    return true;
                }
            }

            log.warn("刷新令牌验证失败，用户ID: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("验证刷新令牌失败，用户ID: {}", userId, e);
            return false;
        }
    }

    @Override
    public void revokeRefreshToken(Long userId, String refreshToken) {
        try {
            // 获取用户的所有刷新令牌ID
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            Set<Object> tokenIdObjects = redisUtil.sMembers(userTokenKey);

            if (tokenIdObjects == null || tokenIdObjects.isEmpty()) {
                log.warn("用户无刷新令牌，用户ID: {}", userId);
                return;
            }

            // 转换为String类型
            Set<String> tokenIds = new java.util.HashSet<>();
            for (Object obj : tokenIdObjects) {
                if (obj != null) {
                    tokenIds.add(obj.toString());
                }
            }

            // 查找并删除对应的刷新令牌
            for (String tokenId : tokenIds) {
                String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;
                String storedToken = redisUtil.get(key, String.class);

                if (refreshToken.equals(storedToken)) {
                    // 删除令牌
                    redisUtil.delete(key);
                    redisUtil.sRemove(userTokenKey, tokenId);
                    log.info("刷新令牌撤销成功，用户ID: {}, 令牌ID: {}", userId, tokenId);
                    return;
                }
            }

            log.warn("刷新令牌不存在，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("撤销刷新令牌失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void revokeAllRefreshTokens(Long userId) {
        try {
            // 获取用户的所有刷新令牌ID
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            Set<Object> tokenIdObjects = redisUtil.sMembers(userTokenKey);

            if (tokenIdObjects == null || tokenIdObjects.isEmpty()) {
                log.warn("用户无刷新令牌，用户ID: {}", userId);
                return;
            }

            // 转换为String类型
            Set<String> tokenIds = new java.util.HashSet<>();
            for (Object obj : tokenIdObjects) {
                if (obj != null) {
                    tokenIds.add(obj.toString());
                }
            }

            // 删除所有刷新令牌
            for (String tokenId : tokenIds) {
                String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;
                redisUtil.delete(key);
            }

            // 删除用户令牌ID集合
            redisUtil.delete(userTokenKey);
            log.info("用户所有刷新令牌撤销成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("撤销用户所有刷新令牌失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public long getRefreshTokenCount(Long userId) {
        try {
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            Long size = redisUtil.sSize(userTokenKey);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取刷新令牌数量失败，用户ID: {}", userId, e);
            return 0;
        }
    }
}
