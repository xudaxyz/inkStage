package com.inkstage.config.scheduled;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.enums.CountType;
import com.inkstage.service.CountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 计数对账定时任务
 * <p>
 * 基于 Redis Key 扫描方案：扫描 Redis 中已有的计数 Key，反查 DB 对账。
 * 无需新增任何 Mapper 方法，因为 Redis 中有 Key 就说明该 ID 之前被访问过，
 * 直接用 getCountFromDatabase 方法即可对比。
 * <p>
 * 执行时间：每天凌晨 2 点
 * <p>
 * 对账流程：
 * 1. 遍历所有 CountType（共 22 种）
 * 2. 对每种类型，SCAN Redis 中 inkstage:count:{type}:* 的所有 Key
 * 3. 解析 Key 中的 targetId，分别获取 Redis 缓存计数和 DB 真实计数
 * 4. 不一致则将 Redis 修正为 DB 的值（以 DB 为准）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CountReconciliationScheduled {

    private final CacheManager cacheManager;
    private final CountService countService;

    /**
     * 计数对账主入口
     * <p>
     * 遍历所有计数类型，逐一对账并汇总统计结果
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void reconcileCounts() {
        log.info("开始计数对账任务");
        int totalChecked = 0;
        int totalFixed = 0;
        int totalErrors = 0;

        for (CountType countType : CountType.values()) {
            try {
                ReconcileResult result = reconcileByType(countType);
                totalChecked += result.checked;
                totalFixed += result.fixed;
                totalErrors += result.errors;
            } catch (Exception e) {
                log.error("对账扫描失败, countType: {}", countType, e);
                totalErrors++;
            }
        }

        log.info("计数对账任务完成, 检查: {}, 修正: {}, 异常: {}", totalChecked, totalFixed, totalErrors);
    }

    /**
     * 对单个计数类型执行对账
     * <p>
     * 扫描 Redis 中该类型的所有计数 Key，逐个与 DB 对比，不一致则修正 Redis
     *
     * @param countType 计数类型
     * @return 该类型的对账统计结果
     */
    private ReconcileResult reconcileByType(CountType countType) {
        ReconcileResult result = new ReconcileResult();
        String pattern = CacheKey.COUNT + countType.name().toLowerCase() + ":*";
        Set<String> keys = cacheManager.scanKeys(pattern);

        if (keys.isEmpty()) {
            return result;
        }

        log.info("对账扫描: {}, 缓存键数量: {}", countType.getDescription(), keys.size());

        for (String key : keys) {
            result.checked++;
            try {
                Long targetId = extractTargetId(key);
                if (targetId == null) {
                    log.warn("无法解析targetId, key: {}", key);
                    result.errors++;
                    continue;
                }

                Long redisCount = cacheManager.get(key, Long.class);
                if (redisCount == null) {
                    continue;
                }

                Long dbCount = countService.getCountFromDatabase(countType, targetId);
                long dbValue = dbCount != null ? dbCount : 0L;

                if (redisCount != dbValue) {
                    log.warn("计数不一致: type={}, targetId={}, redis={}, db={}",
                            countType.getDescription(), targetId, redisCount, dbValue);
                    cacheManager.set(key, dbValue);
                    result.fixed++;
                }
            } catch (Exception e) {
                log.error("对账处理失败, key: {}", key, e);
                result.errors++;
            }
        }

        if (result.fixed > 0 || result.errors > 0) {
            log.info("对账结果: type={}, checked={}, fixed={}, errors={}",
                    countType.getDescription(), result.checked, result.fixed, result.errors);
        }

        return result;
    }

    /**
     * 从 Redis Key 中提取 targetId
     * <p>
     * Key 格式为 inkstage:count:{countType小写}:{targetId}，
     * 例如 inkstage:count:article_read:12345 → 提取出 12345
     *
     * @param key Redis 缓存键
     * @return 目标ID，解析失败返回null
     */
    private Long extractTargetId(String key) {
        String suffix = key.substring(CacheKey.COUNT.length());
        int lastColon = suffix.lastIndexOf(':');
        if (lastColon < 0) {
            return null;
        }
        try {
            return Long.parseLong(suffix.substring(lastColon + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 单种计数类型的对账统计结果
     * <p>
     */
    private static class ReconcileResult {
        /**
         * 本次扫描了多少个 Redis 计数 Key
         */
        int checked;
        /**
         * 发现 Redis 与 DB 不一致，已修正的 Key 数量
         */
        int fixed;
        /**
         * 处理过程中出现异常的 Key 数量
         */
        int errors;
    }
}
