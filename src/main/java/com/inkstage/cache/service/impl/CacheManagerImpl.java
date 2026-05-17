package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            log.debug("缓存设置成功(无随机偏移), key: {}, ttl: {}s", key, ttl.getSeconds());

        } catch (Exception e) {
            log.error("缓存设置失败, key: {}", key, e);
        }
    }

    /**
     * 设置缓存，添加随机偏移
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param ttl   过期时间
     * @param <T>   值类型
     */
    public <T> void setWithRandomOffset(String key, T value, Duration ttl) {
        try {
            // 为TTL添加随机偏移，防止缓存雪崩
            Duration ttlWithOffset = CacheTTL.withRandomOffset(ttl);
            redisUtil.set(key, value, ttlWithOffset.getSeconds());
            log.debug("缓存设置成功, key: {}, baseTTL: {}s, actualTTL: {}s", key, ttl.getSeconds(), ttlWithOffset.getSeconds());
        } catch (Exception e) {
            log.error("缓存设置失败, key: {}", key, e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            return redisUtil.get(key, clazz);
        } catch (Exception e) {
            log.error("从缓存获取失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> T getWithType(String key, TypeReference<T> typeRef) {
        try {
            return redisUtil.getWithType(key, typeRef);
        } catch (Exception e) {
            log.error("从缓存获取失败(泛型), key: {}", key, e);
            return null;
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisUtil.get(key);
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
    public Set<String> scanKeys(String pattern) {
        try {
            return redisUtil.scanKeys(pattern);
        } catch (Exception e) {
            log.error("缓存键扫描失败, pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }

    @Override
    public void deletePattern(String pattern) {
        try {
            if (pattern.isEmpty()) {
                return;
            }
            if (!pattern.endsWith("*")) {
                pattern += pattern.endsWith(":") ? "*" : ":*";
            }
            redisUtil.deletePattern(pattern);
            log.debug("缓存删除成功, pattern: {}", pattern);
        } catch (Exception e) {
            log.error("缓存删除失败, pattern: {}", pattern, e);
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
            boolean success = redisUtil.setIfAbsent(key, value, ttl.getSeconds());
            if (success) {
                log.debug("缓存设置成功(仅当不存在), key: {}, ttl: {}", key, ttl);
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
            log.debug("批量缓存设置结果: {}, 数量: {}, ttl: {}", result, data.size(), ttl);
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

    // ==================== Set 集合操作 ====================

    @Override
    public void sAdd(String key, Object... values) {
        try {
            redisUtil.sAdd(key, values);
            log.debug("集合添加元素成功, key: {}, values: {}", key, values);
        } catch (Exception e) {
            log.error("集合添加元素失败, key: {}", key, e);
        }
    }

    @Override
    public Set<Object> sMembers(String key) {
        try {
            Set<Object> result = redisUtil.sMembers(key);
            log.debug("从缓存获取集合成功, key: {}, size: {}", key, result != null ? result.size() : 0);
            return result != null ? result : Collections.emptySet();
        } catch (Exception e) {
            log.error("从缓存获取集合失败, key: {}", key, e);
            return Collections.emptySet();
        }
    }

    @Override
    public void sRemove(String key, Object... values) {
        try {
            redisUtil.sRemove(key, values);
            log.debug("从集合移除元素成功, key: {}, values: {}", key, values);
        } catch (Exception e) {
            log.error("从集合移除元素失败, key: {}", key, e);
        }
    }

    @Override
    public Long sSize(String key) {
        try {
            Long result = redisUtil.sSize(key);
            log.debug("获取集合大小成功, key: {}, size: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("获取集合大小失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> boolean sIsMember(String key, T value) {
        try {
            boolean result = redisUtil.sIsMember(key, value);
            log.debug("判断元素是否在集合中, key: {}, value: {}, result: {}", key, value, result);
            return result;
        } catch (Exception e) {
            log.error("判断元素是否在集合中失败, key: {}", key, e);
            return false;
        }
    }
}