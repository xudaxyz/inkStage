package com.inkstage.cache.utils;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 提供常用的Redis操作方法
 */
@Slf4j
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 初始化时确保ObjectMapper配置正确
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 创建并配置专用的ObjectMapper
        this.objectMapper = JsonMapper.builder().build();
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Error setting Redis key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置值(带过期时间)
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     * @param unit   时间单位
     * @return 是否成功
     */
    public boolean set(String key, Object value, long expire, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, unit);
            return true;
        } catch (Exception e) {
            log.error("Error setting Redis key: {} with expiration", key, e);
            return false;
        }
    }

    /**
     * 设置值(带过期时间, 默认时间单位为秒)
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间(秒)
     * @return 是否成功
     */
    public boolean set(String key, Object value, long expire) {
        return set(key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * 获取指定类型的对象
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的对象或null
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }

            // 使用更安全的类型转换方式
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }

            // 如果不是目标类型, 尝试使用JSON转换
            String json = objectMapper.writeValueAsString(value);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error getting or casting Redis value for key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取指定类型的对象, 支持泛型集合类型(如List<Article>, Map<String, User>等)
     *
     * @param key     键
     * @param typeRef 类型引用, 用于保留泛型类型信息
     * @param <T>     返回类型泛型
     * @return 类型安全的对象或null
     */
    public <T> T getWithType(String key, TypeReference<T> typeRef) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }

            // 统一转换为JSON字符串, 再解析为目标类型
            String json = value instanceof String ?
                    (String) value : objectMapper.writeValueAsString(value);
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error("Error getting or casting Redis value for key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取对象(自动类型转换)
     *
     * @param key 键
     * @return 对象或null
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting Redis key: {}", key, e);
            return null;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key    键
     * @param expire 过期时间
     * @param unit   时间单位
     */
    public void expire(String key, long expire, TimeUnit unit) {
        try {
            redisTemplate.expire(key, expire, unit);
        } catch (Exception e) {
            log.error("Error setting expiration for Redis key: {}", key, e);
        }
    }

    /**
     * 设置过期时间(默认时间单位为秒)
     *
     * @param key    键
     * @param expire 过期时间(秒)
     */
    public void expire(String key, long expire) {
        expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 删除键
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting Redis key: {}", key, e);
        }
    }

    /**
     * 批量删除键
     *
     * @param keys 键集合
     * @return 删除的键数量
     */
    public long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Error deleting Redis keys: {}", keys, e);
            return 0;
        }
    }

    /**
     * 删除匹配模式的键
     *
     * @param pattern 键模式
     */
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Error deleting Redis keys with pattern: {}", pattern, e);
        }
    }

    /**
     * 清空所有缓存
     *
     * @return 是否成功
     */
    public boolean clearAll() {
        try {
            // 获取所有键并删除
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            return true;
        } catch (Exception e) {
            log.error("Error clearing all Redis keys", e);
            return false;
        }
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking Redis key existence: {}", key, e);
            return false;
        }
    }

    /**
     * 增加计数
     *
     * @param key   键
     * @param delta 增量
     * @return 增加后的值
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Error incrementing Redis key: {}", key, e);
            return null;
        }
    }

    /**
     * 增加计数(默认增量为1)
     *
     * @param key 键
     * @return 增加后的值
     */
    public Long increment(String key) {
        return increment(key, 1L);
    }

    /**
     * 减少计数
     *
     * @param key   键
     * @param delta 减量
     */
    public void decrement(String key, long delta) {
        try {
            redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Error decrementing Redis key: {}", key, e);
        }
    }

    /**
     * 减少计数(默认减量为1)
     *
     * @param key 键
     */
    public void decrement(String key) {
        decrement(key, 1L);
    }

    /**
     * 获取键的过期时间
     *
     * @param key 键
     * @return 过期时间(秒), -1表示永久有效, -2表示键不存在
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting expiration for Redis key: {}", key, e);
            return null;
        }
    }

    // ============================ Hash 操作 ============================

    /**
     * 向哈希表中放入一个键值对
     *
     * @param key     哈希表键
     * @param hashKey 字段名
     * @param value   字段值
     * @return 是否成功
     */
    public boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("Error setting hash field: {} for key: {}", hashKey, key, e);
            return false;
        }
    }

    /**
     * 从哈希表中获取一个字段值
     *
     * @param key     哈希表键
     * @param hashKey 字段名
     * @return 字段值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Error getting hash field: {} for key: {}", hashKey, key, e);
            return null;
        }
    }

    /**
     * 从哈希表中获取一个字段值, 并转换为指定类型
     *
     * @param key     哈希表键
     * @param hashKey 字段名
     * @param clazz   目标类型
     * @param <T>     返回类型泛型
     * @return 类型安全的字段值
     */
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return value == null ? null : clazz.cast(value);
        } catch (Exception e) {
            log.error("Error getting or casting hash value for key: {} and hashKey: {}", key, hashKey, e);
            return null;
        }
    }

    /**
     * 获取哈希表中所有键值对
     *
     * @param key 哈希表键
     * @return 所有键值对
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Error getting all hash entries for key: {}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 获取哈希表中所有键值对, 并转换为指定类型
     *
     * @param key        哈希表键
     * @param keyClass   键类型
     * @param valueClass 值类型
     * @param <K>        键类型泛型
     * @param <V>        值类型泛型
     * @return 类型安全的键值对
     */
    public <K, V> Map<K, V> hGetAll(String key, Class<K> keyClass, Class<V> valueClass) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Map<K, V> result = new HashMap<>(entries.size());
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    result.put(keyClass.cast(entry.getKey()), valueClass.cast(entry.getValue()));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting hash entries for key: {}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 删除哈希表中的指定字段
     *
     * @param key      哈希表键
     * @param hashKeys 字段名数组
     * @return 删除的字段数量
     */
    public long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("Error deleting hash fields: {} for key: {}", Arrays.toString(hashKeys), key, e);
            return 0;
        }
    }

    /**
     * 判断哈希表中是否存在指定字段
     *
     * @param key     哈希表键
     * @param hashKey 字段名
     * @return 是否存在
     */
    public boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("Error checking hash field existence: {} for key: {}", hashKey, key, e);
            return false;
        }
    }

    // ============================ List 操作 ============================

    /**
     * 将值插入到列表头部
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Error pushing value to list head: {}", key, e);
            return null;
        }
    }

    /**
     * 将多个值插入到列表头部
     *
     * @param key    键
     * @param values 值列表
     * @return 列表长度
     */
    public Long lPushAll(String key, Collection<Object> values) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, values);
        } catch (Exception e) {
            log.error("Error pushing multiple values to list head: {}", key, e);
            return null;
        }
    }

    /**
     * 将值插入到列表尾部
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long rPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Error pushing value to list tail: {}", key, e);
            return null;
        }
    }

    /**
     * 将多个值插入到列表尾部
     *
     * @param key    键
     * @param values 值列表
     * @return 列表长度
     */
    public Long rPushAll(String key, Collection<Object> values) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, values);
        } catch (Exception e) {
            log.error("Error pushing multiple values to list tail: {}", key, e);
            return null;
        }
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Error getting list range for key: {}", key, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取列表指定范围内的元素, 并转换为指定类型
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素列表
     */
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        try {
            List<Object> values = redisTemplate.opsForList().range(key, start, end);
            List<T> result = new ArrayList<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    result.add(clazz.cast(value));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting list range for key: {}", key, e);
            return Collections.emptyList();
        }
    }

    /**
     * 从列表中弹出一个元素(头部)
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("Error popping value from list head: {}", key, e);
            return null;
        }
    }

    /**
     * 从列表中弹出一个元素(尾部)
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("Error popping value from list tail: {}", key, e);
            return null;
        }
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public Long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("Error getting list size for key: {}", key, e);
            return null;
        }
    }

    // ============================ Set 操作 ============================

    /**
     * 向集合中添加元素
     *
     * @param key    键
     * @param values 值列表
     */
    public void sAdd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Error adding values to set: {}", key, e);
        }
    }

    /**
     * 获取集合中的所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Error getting all members from set: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取集合中的所有元素, 并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素集合
     */
    public <T> Set<T> sMembers(String key, Class<T> clazz) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            Set<T> result = new HashSet<>(members.size());
            for (Object member : members) {
                if (member != null) {
                    result.add(clazz.cast(member));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting set members for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 判断集合中是否存在指定元素
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public boolean sIsMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("Error checking if value is member of set: {}", key, e);
            return false;
        }
    }

    /**
     * 获取集合大小
     *
     * @param key 键
     * @return 集合大小
     */
    public Long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("Error getting set size: {}", key, e);
            return null;
        }
    }

    /**
     * 从集合中移除元素
     *
     * @param key    键
     * @param values 值列表
     */
    public void sRemove(String key, Object... values) {
        try {
            redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("Error removing values from set: {}", key, e);
        }
    }

    /**
     * 从集合中随机弹出一个元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object sPop(String key) {
        try {
            return redisTemplate.opsForSet().pop(key);
        } catch (Exception e) {
            log.error("Error popping value from set: {}", key, e);
            return null;
        }
    }

    /**
     * 从集合中随机弹出指定数量的元素
     *
     * @param key   键
     * @param count 弹出数量
     * @return 弹出的元素列表
     */
    public List<Object> sPop(String key, long count) {
        try {
            return redisTemplate.opsForSet().pop(key, count);
        } catch (Exception e) {
            log.error("Error popping {} values from set: {}", count, key, e);
            return Collections.emptyList();
        }
    }

    // ============================ ZSet 操作 ============================

    /**
     * 向有序集合中添加元素
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("Error adding value to ZSet: {}", key, e);
            return false;
        }
    }

    /**
     * 获取有序集合中指定范围内的元素(按分数从小到大排序)
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @return 元素集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("Error getting ZSet range for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定范围内的元素(按分数从小到大排序), 并转换为指定类型
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素集合
     */
    public <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().range(key, start, end);
            Set<T> result = new HashSet<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    result.add(clazz.cast(value));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting ZSet range for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定范围内的元素(按分数从大到小排序)
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @return 元素集合
     */
    public Set<Object> zRevRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("Error getting reversed ZSet range for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定范围内的元素(按分数从大到小排序), 并转换为指定类型
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置, -1表示最后一个元素
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素集合
     */
    public <T> Set<T> zRevRange(String key, long start, long end, Class<T> clazz) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().reverseRange(key, start, end);
            Set<T> result = new HashSet<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    result.add(clazz.cast(value));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting reversed ZSet range for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素(按分数从小到大排序)
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Error getting ZSet range by score for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素(按分数从小到大排序), 并转换为指定类型
     *
     * @param key   键
     * @param min   最小分数
     * @param max   最大分数
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素集合
     */
    public <T> Set<T> zRangeByScore(String key, double min, double max, Class<T> clazz) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, min, max);
            Set<T> result = new HashSet<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    result.add(clazz.cast(value));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting ZSet range by score for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素(按分数从大到小排序)
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<Object> zRevRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Error getting reversed ZSet range by score for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素(按分数从大到小排序), 并转换为指定类型
     *
     * @param key   键
     * @param min   最小分数
     * @param max   最大分数
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的元素集合
     */
    public <T> Set<T> zRevRangeByScore(String key, double min, double max, Class<T> clazz) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
            Set<T> result = new HashSet<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    result.add(clazz.cast(value));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting or casting reversed ZSet range by score for key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取有序集合中元素的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("Error getting score for value in ZSet: {}", key, e);
            return null;
        }
    }

    /**
     * 获取有序集合大小
     *
     * @param key 键
     * @return 集合大小
     */
    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("Error getting ZSet size: {}", key, e);
            return null;
        }
    }

    /**
     * 从有序集合中移除元素
     *
     * @param key    键
     * @param values 值列表
     * @return 移除的元素数量
     */
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("Error removing values from ZSet: {}", key, e);
            return null;
        }
    }

    // ==================== 批量操作 ====================

    /**
     * 批量设置值
     *
     * @param keyValueMap 键值对映射
     * @return 是否成功
     */
    public boolean batchSet(Map<String, Object> keyValueMap) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
                    redisTemplate.opsForValue().set(entry.getKey(), entry.getValue());
                }
                return null;
            });
            return true;
        } catch (Exception e) {
            log.error("Error batch setting Redis keys", e);
            return false;
        }
    }

    /**
     * 批量设置值(带过期时间)
     *
     * @param keyValueMap 键值对映射
     * @param expire      过期时间
     * @param unit        时间单位
     * @return 是否成功
     */
    public boolean batchSet(Map<String, Object> keyValueMap, long expire, TimeUnit unit) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
                    redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), expire, unit);
                }
                return null;
            });
            return true;
        } catch (Exception e) {
            log.error("Error batch setting Redis keys with expiration", e);
            return false;
        }
    }

    /**
     * 批量获取值
     *
     * @param keys 键集合
     * @return 值列表
     */
    public List<Object> batchGet(Collection<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("Error batch getting Redis keys", e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量获取值(指定类型)
     *
     * @param keys  键集合
     * @param clazz 目标类型
     * @param <T>   返回类型泛型
     * @return 类型安全的值列表
     */
    public <T> List<T> batchGet(Collection<String> keys, Class<T> clazz) {
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            List<T> result = new ArrayList<>(values.size());
            for (Object value : values) {
                if (value != null) {
                    if (clazz.isInstance(value)) {
                        result.add(clazz.cast(value));
                    } else {
                        String json = objectMapper.writeValueAsString(value);
                        result.add(objectMapper.readValue(json, clazz));
                    }
                } else {
                    result.add(null);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error batch getting or casting Redis values", e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量设置过期时间
     *
     * @param keyExpireMap 键和过期时间映射
     * @param unit         时间单位
     * @return 成功设置的键数量
     */
    public int batchExpire(Map<String, Long> keyExpireMap, TimeUnit unit) {
        int successCount = 0;
        try {
            for (Map.Entry<String, Long> entry : keyExpireMap.entrySet()) {
                if (redisTemplate.expire(entry.getKey(), entry.getValue(), unit)) {
                    successCount++;
                }
            }
        } catch (Exception e) {
            log.error("Error batch setting expiration for Redis keys", e);
        }
        return successCount;
    }

    /**
     * 批量判断键是否存在
     *
     * @param keys 键集合
     * @return 键存在性映射
     */
    public Map<String, Boolean> batchHasKey(Collection<String> keys) {
        Map<String, Boolean> result = new HashMap<>(keys.size());
        try {
            for (String key : keys) {
                result.put(key, redisTemplate.hasKey(key));
            }
        } catch (Exception e) {
            log.error("Error batch checking Redis key existence", e);
            // 发生异常时, 所有键都标记为不存在
            for (String key : keys) {
                result.put(key, false);
            }
        }
        return result;
    }

    /**
     * 批量删除键
     *
     * @param keys 键集合
     * @return 删除的键数量
     */
    public long batchDelete(Collection<String> keys) {
        return delete(keys);
    }

    /**
     * 批量删除匹配模式的键
     *
     * @param patterns 键模式集合
     * @return 删除的键数量
     */
    public long batchDeletePattern(Collection<String> patterns) {
        long deleteCount = 0;
        try {
            for (String pattern : patterns) {
                Set<String> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    deleteCount += redisTemplate.delete(keys);
                }
            }
        } catch (Exception e) {
            log.error("Error batch deleting Redis keys with patterns", e);
        }
        return deleteCount;
    }
}