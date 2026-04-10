package com.inkstage.config;

import com.inkstage.cache.constant.RedisKeyConstants;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 * 配置RedisTemplate和StringRedisTemplate, 提供更好的序列化支持
 * 适配Spring Data Redis 4.0+版本
 * 启用Spring Cache注解支持, 提供声明式缓存功能
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * 用于操作对象类型的数据
     * Spring Data Redis 4.0+推荐使用RedisSerializer.json()获取JSON序列化器
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用Spring Data Redis 4.0+推荐的JSON序列化器
        RedisSerializer<@NotNull Object> jsonSerializer = RedisSerializer.json();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置所有序列化器, 确保操作一致性
        template.setDefaultSerializer(jsonSerializer);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置StringRedisTemplate
     * 用于操作字符串类型的数据
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

    /**
     * 配置Redis缓存管理器
     * 为不同的缓存名称设置不同的过期时间, 以优化缓存性能和内存使用
     *
     * @param factory Redis连接工厂
     * @return Redis缓存管理器
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // 使用Spring Data Redis 4.0+推荐的JSON序列化器, 用于序列化缓存值
        RedisSerializer<@NotNull Object> jsonSerializer = RedisSerializer.json();
        // 使用字符串序列化器, 用于序列化缓存键
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 基础缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 默认缓存过期时间：30分钟
                .entryTtl(Duration.ofMinutes(30))
                // 缓存键前缀：inkstage:, 避免与其他项目的缓存键冲突
                .computePrefixWith(cacheName -> RedisKeyConstants.MODULE_PREFIX + cacheName + ":")
                // 配置缓存键序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                // 配置缓存值序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // 为不同的缓存名称设置不同的过期时间
        // 策略：更新频率越高的缓存, 过期时间越短
        // 添加随机偏移量防止缓存雪崩
        return RedisCacheManager.builder(factory)
                // 文章列表缓存：20分钟(实时性要求较高)
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLES, defaultConfig.entryTtl(
                        Duration.ofMinutes(20).plusSeconds((long) (Math.random() * 600))))
                // 文章详情缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_DETAIL, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_DETAIL_TTL.plusSeconds((long) (Math.random() * 600))))
                // 热门文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_HOT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_HOT_TTL.plusSeconds((long) (Math.random() * 600))))
                // 最新文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_LATEST, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_LATEST_TTL.plusSeconds((long) (Math.random() * 600))))
                // 轮播图文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_BANNER, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_BANNER_TTL.plusSeconds((long) (Math.random() * 600))))
                // 用户文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_USER, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_USER_TTL.plusSeconds((long) (Math.random() * 600))))
                // 作者相关文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_USER_RELATED, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_USER_RELATED_TTL.plusSeconds((long) (Math.random() * 600))))
                // 当前用户文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_MY, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_MY_TTL.plusSeconds((long) (Math.random() * 600))))
                // 文章评论缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_COMMENT_LIST, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_COMMENT_LIST_TTL.plusSeconds((long) (Math.random() * 600))))
                // 文章点赞缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_LIKE_STATUS, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_LIKE_STATUS_TTL.plusSeconds((long) (Math.random() * 600))))
                // 文章收藏缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_COLLECTION_STATUS, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_COLLECTION_STATUS_TTL.plusSeconds((long) (Math.random() * 600))))
                // 搜索文章缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_SEARCH, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_SEARCH_TTL.plusSeconds((long) (Math.random() * 600))))
                // 用户热门缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_USER_HOT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_USER_HOT_TTL.plusSeconds((long) (Math.random() * 600))))
                // 关注相关缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_FOLLOW_STATUS, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_FOLLOW_STATUS_TTL.plusSeconds((long) (Math.random() * 300))))
                .withCacheConfiguration(RedisKeyConstants.CACHE_FOLLOW_LIST, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_FOLLOW_LIST_TTL.plusSeconds((long) (Math.random() * 300))))
                // 仪表盘统计缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_DASHBOARD, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_DASHBOARD_TTL.plusSeconds((long) (Math.random() * 60))))
                // 系统公告缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ANNOUNCEMENT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ANNOUNCEMENT_TTL.plusSeconds((long) (Math.random() * 600))))
                // 阅读历史缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_READING_HISTORY, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_READING_HISTORY_TTL.plusSeconds((long) (Math.random() * 300))))
                // 分类缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_CATEGORIES, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_CATEGORIES_TTL.plusSeconds((long) (Math.random() * 600))))
                // 标签缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_TAGS, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_TAGS_TTL.plusSeconds((long) (Math.random() * 600))))
                // 角色缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ROLES, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ROLES_TTL.plusSeconds((long) (Math.random() * 600))))
                // 用户角色缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_USER_ROLES, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_USER_ROLES_TTL.plusSeconds((long) (Math.random() * 600))))
                // RedisUtil使用的缓存配置
                // 文章计数缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_ARTICLE_COUNT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_ARTICLE_COUNT_TTL.plusSeconds((long) (Math.random() * 600))))
                // 通知未读数缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_NOTIFICATION_UNREAD, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_NOTIFICATION_UNREAD_TTL.plusSeconds((long) (Math.random() * 300))))
                // 通知最近列表缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_NOTIFICATION_RECENT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_NOTIFICATION_RECENT_TTL.plusSeconds((long) (Math.random() * 60))))
                // 验证码缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_VERIFY_CODE, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_VERIFY_CODE_TTL.plusSeconds((long) (Math.random() * 60))))
                // 登录尝试缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_LOGIN_ATTEMPT, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_LOGIN_ATTEMPT_TTL.plusSeconds((long) (Math.random() * 60))))
                // 登录锁定缓存
                .withCacheConfiguration(RedisKeyConstants.CACHE_LOGIN_LOCK, defaultConfig.entryTtl(
                        RedisKeyConstants.CACHE_LOGIN_LOCK_TTL.plusSeconds((long) (Math.random() * 60))))
                .build();
    }

    /**
     * 配置Lettuce客户端资源
     * 用于管理连接池和其他资源
     */
    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }
}