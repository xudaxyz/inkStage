package com.inkstage.utils;

import com.inkstage.utils.model.RedisUsageStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Redis监控和管理类
 * 用于监控Redis的使用情况和管理Redis键的过期时间
 */
@Slf4j
@Component
public class RedisMonitor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ScheduledExecutorService scheduler;

    public RedisMonitor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 初始化监控任务
     */
    @PostConstruct
    public void init() {
        // 每小时执行一次Redis键监控
        scheduler.scheduleAtFixedRate(this::monitorRedisKeys, 0, 1, TimeUnit.HOURS);
        log.info("Redis监控任务已启动");
    }

    /**
     * 监控Redis键的使用情况
     */
    public void monitorRedisKeys() {
        try {
            // 获取所有键的数量
            long keyCount = redisTemplate.keys("*").size();
            log.info("Redis键总数: {}", keyCount);

            // 监控特定前缀的键
            monitorKeyPrefix("verify_code:", "验证码键");
            monitorKeyPrefix("send_limit:", "发送频率限制键");
            monitorKeyPrefix("register_limit:", "注册频率限制键");
            monitorKeyPrefix("user:", "用户信息键");
            monitorKeyPrefix("session:", "会话键");
            monitorKeyPrefix("token:", "令牌键");
            monitorKeyPrefix("cache:", "缓存键");
        } catch (Exception e) {
            log.error("Redis监控失败", e);
        }
    }

    /**
     * 监控特定前缀的键
     *
     * @param prefix      键前缀
     * @param description 描述
     */
    private void monitorKeyPrefix(String prefix, String description) {
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                log.info("{}数量: {}", description, keys.size());
                // 检查是否有过期时间
                int noExpiryCount = 0;
                for (String key : keys) {
                    Long expire = redisTemplate.getExpire(key);
                    if (expire == null || expire == -1) {
                        noExpiryCount++;
                    }
                }
                if (noExpiryCount > 0) {
                    log.warn("{}中有{}个键没有设置过期时间", description, noExpiryCount);
                }
            }
        } catch (Exception e) {
            log.error("监控{}失败", description, e);
        }
    }

    /**
     * 清理过期的Redis键
     *
     * @param pattern 键模式
     * @return 清理的键数量
     */
    public long cleanExpiredKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                long deleteCount = redisTemplate.delete(keys);
                log.info("清理过期键数量: {}, 模式: {}", deleteCount, pattern);
                return deleteCount;
            }
        } catch (Exception e) {
            log.error("清理过期键失败, 模式: {}", pattern, e);
        }
        return 0;
    }

    /**
     * 清理所有没有过期时间的键
     *
     * @param pattern 键模式
     * @return 清理的键数量
     */
    public long cleanKeysWithoutExpiry(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                long deleteCount = 0;
                for (String key : keys) {
                    Long expire = redisTemplate.getExpire(key);
                    if (expire == null || expire == -1) {
                        redisTemplate.delete(key);
                        deleteCount++;
                    }
                }
                log.info("清理无过期时间键数量: {}, 模式: {}", deleteCount, pattern);
                return deleteCount;
            }
        } catch (Exception e) {
            log.error("清理无过期时间键失败, 模式: {}", pattern, e);
        }
        return 0;
    }

    /**
     * 获取Redis键的使用情况统计
     *
     * @return 使用情况统计信息
     */
    public RedisUsageStats getUsageStats() {
        try {
            RedisUsageStats stats = new RedisUsageStats();

            // 获取所有键的数量
            stats.setTotalKeys(redisTemplate.keys("*").size());

            // 获取特定类型键的数量
            stats.setVerifyCodeKeys(getKeyCount("verify_code:*"));
            stats.setSendLimitKeys(getKeyCount("send_limit:*"));
            stats.setRegisterLimitKeys(getKeyCount("register_limit:*"));
            stats.setUserKeys(getKeyCount("user:*"));
            stats.setSessionKeys(getKeyCount("session:*"));
            stats.setTokenKeys(getKeyCount("token:*"));
            stats.setCacheKeys(getKeyCount("cache:*"));

            return stats;
        } catch (Exception e) {
            log.error("获取Redis使用情况统计失败", e);
            return new RedisUsageStats();
        }
    }

    /**
     * 获取指定模式的键数量
     *
     * @param pattern 键模式
     * @return 键数量
     */
    private long getKeyCount(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? keys.size() : 0;
    }


    /**
     * 关闭监控任务
     */
    public void shutdown() {
        scheduler.shutdown();
        log.info("Redis监控任务已关闭");
    }
}
