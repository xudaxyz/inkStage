package com.inkstage.config.scheduled;

import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 账号清理定时任务
 * <p>
 * 扫描冷却期已过的待删除账号，执行永久删除。
 * 冷却期为30天，用户在此期间可登录恢复账号。
 * <p>
 * 执行时间：每天凌晨 0 点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCleanupScheduled {

    private final UserService userService;

    /**
     * 清理过期的待删除账号
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredAccounts() {
        log.info("开始清理过期待删除账号");
        try {
            int count = userService.cleanupExpiredPendingDeleteAccounts();
            log.info("清理过期待删除账号完成，共清理 {} 个账号", count);
        } catch (Exception e) {
            log.error("清理过期待删除账号异常", e);
        }
    }
}
