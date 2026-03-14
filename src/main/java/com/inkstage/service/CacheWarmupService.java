package com.inkstage.service;

import com.inkstage.service.ArticleQueryService;
import com.inkstage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 缓存预热服务
 * 在应用启动时加载常用数据到缓存中，以提高系统的响应速度
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmupService {

    private final ArticleQueryService articleQueryService;
    private final UserStatsService userStatsService;

    /**
     * 应用启动后执行缓存预热
     */
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        log.info("开始执行缓存预热");
        try {
            // 预热热门文章缓存
            warmupHotArticles();
            
            // 预热最新文章缓存
            warmupLatestArticles();
            
            // 预热轮播图文章缓存
            warmupBannerArticles();
            
            // 预热热门用户缓存
            warmupHotUsers();
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }

    /**
     * 预热热门文章缓存
     */
    private void warmupHotArticles() {
        try {
            log.info("预热热门文章缓存");
            articleQueryService.getHotArticles(10, "week");
            articleQueryService.getHotArticles(5, "month");
            articleQueryService.getHotArticles(3, "year");
        } catch (Exception e) {
            log.error("预热热门文章缓存失败", e);
        }
    }

    /**
     * 预热最新文章缓存
     */
    private void warmupLatestArticles() {
        try {
            log.info("预热最新文章缓存");
            articleQueryService.getLatestArticles(10);
        } catch (Exception e) {
            log.error("预热最新文章缓存失败", e);
        }
    }

    /**
     * 预热轮播图文章缓存
     */
    private void warmupBannerArticles() {
        try {
            log.info("预热轮播图文章缓存");
            articleQueryService.getBannerArticles(5);
        } catch (Exception e) {
            log.error("预热轮播图文章缓存失败", e);
        }
    }

    /**
     * 预热热门用户缓存
     */
    private void warmupHotUsers() {
        try {
            log.info("预热热门用户缓存");
            userStatsService.getHotUsers(10);
        } catch (Exception e) {
            log.error("预热热门用户缓存失败", e);
        }
    }
}