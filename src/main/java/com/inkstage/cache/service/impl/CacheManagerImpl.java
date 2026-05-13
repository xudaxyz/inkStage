package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheManagerImpl implements CacheManager {

    private final RedisUtil redisUtil;

    @Override
    public <T> void set(String key, T value) {
        set(key, value, CacheTTL.DEFAULT);
    }

    @Override
    public <T> void set(String key, T value, Duration ttl) {
        try {
            redisUtil.set(key, value, ttl.getSeconds());
            log.debug("缓存设置成功, key: {}, ttl: {}", key, ttl);
        } catch (Exception e) {
            log.error("缓存设置失败, key: {}", key, e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            T value = redisUtil.get(key, clazz);
            if (value != null) {
                log.debug("从缓存获取clazz: {} 成功, key: {}", clazz, key);
            }
            return value;
        } catch (Exception e) {
            log.error("从缓存获取失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> T getWithType(String key, TypeReference<T> typeRef) {
        try {
            T value = redisUtil.getWithType(key, typeRef);
            if (value != null) {
                log.debug("从缓存获取成功(泛型), key: {}", key);
            }
            return value;
        } catch (Exception e) {
            log.error("从缓存获取失败(泛型), key: {}", key, e);
            return null;
        }
    }

    @Override
    public Object get(String key) {
        try {
            Object value = redisUtil.get(key);
            if (value != null) {
                log.debug("从缓存获取成功, key: {}", key);
            }
            return value;
        } catch (Exception e) {
            log.error("从缓存获取失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisUtil.delete(key);
            log.debug("缓存删除成功, key: {}", key);
        } catch (Exception e) {
            log.error("缓存删除失败, key: {}", key, e);
        }
    }

    @Override
    public long batchDelete(List<String> keys) {
        try {
            long count = redisUtil.delete(keys);
            log.debug("批量缓存删除成功, 数量: {}", count);
            return count;
        } catch (Exception e) {
            log.error("批量缓存删除失败", e);
            return 0;
        }
    }

    @Override
    public void deletePattern(String pattern) {
        try {
            if (pattern.isEmpty()) {
                return;
            }
            if (!pattern.contains("*")) {
                pattern = pattern + "*";
            }
            redisUtil.deletePattern(pattern);
            log.debug("模式匹配删除成功, pattern: {}", pattern);
        } catch (Exception e) {
            log.error("模式匹配删除失败, pattern: {}", pattern, e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return redisUtil.hasKey(key);
        } catch (Exception e) {
            log.error("检查缓存存在性失败, key: {}", key, e);
            return false;
        }
    }

    @Override
    public <T> boolean setIfAbsent(String key, T value) {
        return setIfAbsent(key, value, CacheTTL.DEFAULT);
    }

    @Override
    public <T> boolean setIfAbsent(String key, T value, Duration ttl) {
        try {
            boolean success = redisUtil.setIfAbsent(key, value);
            if (success) {
                redisUtil.expire(key, ttl.getSeconds());
                log.debug("缓存设置成功(仅当不存在), key: {}", key);
            }
            return success;
        } catch (Exception e) {
            log.error("缓存设置失败(仅当不存在), key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            Long value = redisUtil.increment(key, delta);
            log.debug("缓存递增成功, key: {}, delta: {}, result: {}", key, delta, value);
            return value;
        } catch (Exception e) {
            log.error("缓存递增失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public Long increment(String key) {
        return increment(key, 1L);
    }

    @Override
    public void decrement(String key, long delta) {
        try {
            redisUtil.decrement(key, delta);
            log.debug("缓存递减成功, key: {}, delta: {}", key, delta);
        } catch (Exception e) {
            log.error("缓存递减失败, key: {}", key, e);
        }
    }

    @Override
    public void decrement(String key) {
        decrement(key, 1L);
    }

    @Override
    public Long getExpire(String key) {
        try {
            return redisUtil.getExpire(key);
        } catch (Exception e) {
            log.error("获取缓存过期时间失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public void expire(String key, Duration ttl) {
        try {
            redisUtil.expire(key, ttl.getSeconds());
            log.debug("缓存过期时间设置成功, key: {}, ttl: {}", key, ttl);
        } catch (Exception e) {
            log.error("缓存过期时间设置失败, key: {}", key, e);
        }
    }

    @Override
    public <T> void batchSet(Map<String, T> data) {
        batchSet(data, CacheTTL.DEFAULT);
    }

    @Override
    public <T> void batchSet(Map<String, T> data, Duration ttl) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> objectMap = (Map<String, Object>) data;
            boolean result = redisUtil.batchSet(objectMap, ttl.getSeconds());
            log.debug("批量缓存设置结果: {}, 数量: {}", result, data.size());
        } catch (Exception e) {
            log.error("批量缓存设置失败", e);
        }
    }

    @Override
    public <T> List<T> batchGet(List<String> keys, Class<T> clazz) {
        try {
            List<T> result = redisUtil.batchGet(keys, clazz);
            log.debug("批量缓存获取成功, 数量: {}, clazz: {}", result.size(), clazz);
            return result;
        } catch (Exception e) {
            log.error("批量缓存获取失败", e);
            return List.of();
        }
    }

    @Override
    public List<Object> batchGet(List<String> keys) {
        try {
            List<Object> result = redisUtil.batchGet(keys);
            log.debug("批量缓存获取成功, 数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("批量缓存获取失败", e);
            return List.of();
        }
    }
}