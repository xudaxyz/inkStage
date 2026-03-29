package com.inkstage.service.impl;

import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.service.TokenStoreService;
import com.inkstage.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
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

    private static final int MAX_REFRESH_TOKENS_PER_USER = 10; // 每个用户最多10个刷新令牌

    @Override
    public String storeRefreshToken(Long userId, String refreshToken, Duration expiry) {
        // 生成唯一的令牌ID
        String tokenId = UUID.randomUUID().toString();
        // 构建Redis键
        String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;

        try {
            // 检查并限制用户的刷新令牌数量
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            long tokenCount = redisUtil.sSize(userTokenKey);
            if (tokenCount >= MAX_REFRESH_TOKENS_PER_USER) {
                // 删除最早的令牌
                Set<Object> tokenIdObjects = redisUtil.sMembers(userTokenKey);
                if (tokenIdObjects != null && !tokenIdObjects.isEmpty()) {
                    String oldestTokenId = tokenIdObjects.iterator().next().toString();
                    String oldestTokenKey = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + oldestTokenId;
                    redisUtil.delete(oldestTokenKey);
                    redisUtil.sRemove(userTokenKey, oldestTokenId);
                    log.info("删除用户最早的刷新令牌，用户ID: {}, 令牌ID: {}", userId, oldestTokenId);
                }
            }

            // 存储刷新令牌到Redis
            redisUtil.set(key, refreshToken, expiry.getSeconds());
            // 同时存储用户ID和令牌ID的映射，用于后续验证
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
            // 检查令牌是否在黑名单中
            String blacklistKey = RedisKeyConstants.REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken.hashCode();
            if (redisUtil.hasKey(blacklistKey)) {
                log.warn("刷新令牌已在黑名单中，用户ID: {}", userId);
                return false;
            }

            // 获取用户的所有刷新令牌ID
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            Set<Object> tokenIdObjects = redisUtil.sMembers(userTokenKey);

            if (tokenIdObjects == null || tokenIdObjects.isEmpty()) {
                log.warn("验证刷新令牌, 用户ID {} 无刷新令牌", userId);
                return false;
            }

            // 转换为String类型
            Set<String> tokenIds = new HashSet<>();
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
                log.warn("撤销刷新令牌, 用户ID {} 无刷新令牌", userId);
                return;
            }

            // 转换为String类型
            Set<String> tokenIds = new HashSet<>();
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
                    // 调用私有方法处理单个令牌的撤销
                    revokeSingleRefreshToken(userId, tokenId, storedToken, key);
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
            Set<String> tokenIds = new HashSet<>();
            for (Object obj : tokenIdObjects) {
                if (obj != null) {
                    tokenIds.add(obj.toString());
                }
            }

            // 遍历所有令牌ID，调用私有方法处理单个令牌的撤销
            for (String tokenId : tokenIds) {
                String key = RedisKeyConstants.REFRESH_TOKEN_PREFIX + userId + ":" + tokenId;
                String storedToken = redisUtil.get(key, String.class);
                if (storedToken != null) {
                    // 调用私有方法处理单个令牌的撤销
                    revokeSingleRefreshToken(userId, tokenId, storedToken, key);
                }
            }

            // 删除整个用户令牌ID集合
            redisUtil.delete(userTokenKey);
            log.info("用户所有刷新令牌撤销成功并加入黑名单，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("撤销用户所有刷新令牌失败，用户ID: {}", userId, e);
        }
    }
    
    /**
     * 撤销单个刷新令牌的具体实现
     * @param userId 用户ID
     * @param tokenId 令牌ID
     * @param token 令牌值
     * @param tokenKey 令牌在Redis中的键
     */
    private void revokeSingleRefreshToken(Long userId, String tokenId, String token, String tokenKey) {
        try {
            // 获取原令牌的剩余时间
            Long remainingTime = redisUtil.getExpire(tokenKey);
            if (remainingTime == null || remainingTime <= 0) {
                remainingTime = 86400L; // 默认1天
            }
            
            // 删除令牌
            redisUtil.delete(tokenKey);
            String userTokenKey = RedisKeyConstants.USER_REFRESH_TOKEN_PREFIX + userId;
            redisUtil.sRemove(userTokenKey, tokenId);
            
            // 将令牌添加到黑名单，有效期为原令牌的剩余时间
            String blacklistKey = RedisKeyConstants.REFRESH_TOKEN_BLACKLIST_PREFIX + token.hashCode();
            redisUtil.set(blacklistKey, "1", remainingTime);
            
            log.info("刷新令牌撤销成功并加入黑名单，用户ID: {}, 令牌ID: {}", userId, tokenId);
        } catch (Exception e) {
            log.error("撤销单个刷新令牌失败，用户ID: {}, 令牌ID: {}", userId, tokenId, e);
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
