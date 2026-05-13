package com.inkstage.cache.service;

import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 缓存管理器接口
 * <p>
 * 提供统一的缓存操作入口，封装底层Redis操作，提供类型安全的缓存读写能力。
 * 支持自动默认TTL、自定义TTL、批量操作、原子操作等功能。
 * </p>
 */
public interface CacheManager {

    /**
     * 设置缓存（使用默认TTL）
     * <p>默认TTL为1小时</p>
     *
     * @param key   缓存键
     * @param value 缓存值，可以是任意可序列化对象
     * @param <T>   值的类型泛型
     */
    <T> void set(String key, T value);

    /**
     * 设置缓存（指定TTL）
     *
     * @param key   缓存键
     * @param value 缓存值，可以是任意可序列化对象
     * @param ttl   过期时间
     * @param <T>   值的类型泛型
     */
    <T> void set(String key, T value, Duration ttl);

    /**
     * 获取缓存（类型安全）
     * <p>自动进行类型转换，支持对象和基本类型</p>
     *
     * @param key   缓存键
     * @param clazz 目标类型的Class对象
     * @param <T>   返回类型泛型
     * @return 缓存的值，如果缓存不存在或转换失败返回null
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 获取缓存（支持泛型集合类型）
     *
     * @param key     缓存键
     * @param typeRef 类型引用，使用TypeReference保留泛型信息
     * @param <T>     返回类型泛型
     * @return 缓存的值，如果缓存不存在或转换失败返回null
     */
    <T> T getWithType(String key, TypeReference<T> typeRef);

    /**
     * 获取缓存（不进行类型转换）
     *
     * @param key 缓存键
     * @return 缓存的值，如果缓存不存在返回null
     */
    Object get(String key);

    /**
     * 删除指定缓存键
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 批量删除缓存键
     *
     * @param keys 缓存键列表
     * @return 成功删除的键数量
     */
    long batchDelete(List<String> keys);

    /**
     * 模式匹配删除缓存键
     * <p>使用Redis的KEYS命令进行模式匹配，支持通配符*和?</p>
     * <p>注意：在大规模缓存环境中使用需谨慎，可能影响性能</p>
     *
     * @param pattern 键模式，如"inkstage:article:*"
     */
    void deletePattern(String pattern);

    /**
     * 判断缓存键是否存在
     *
     * @param key 缓存键
     * @return 如果存在返回true，否则返回false
     */
    boolean exists(String key);

    /**
     * 设置缓存（仅当键不存在时）
     * <p>使用默认TTL（1小时），原子操作，适用于分布式锁等场景</p>
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   值的类型泛型
     * @return 如果设置成功返回true，如果键已存在返回false
     */
    <T> boolean setIfAbsent(String key, T value);

    /**
     * 设置缓存（仅当键不存在时，指定TTL）
     * <p>原子操作，适用于分布式锁等场景</p>
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param ttl   过期时间
     * @param <T>   值的类型泛型
     * @return 如果设置成功返回true，如果键已存在返回false
     */
    <T> boolean setIfAbsent(String key, T value, Duration ttl);

    /**
     * 原子递增（指定增量）
     * <p>适用于计数器场景，如文章阅读量、点赞数等</p>
     *
     * @param key   缓存键
     * @param delta 增量值，正数为增加，负数为减少
     * @return 递增后的值，如果操作失败返回null
     */
    Long increment(String key, long delta);

    /**
     * 原子递增（默认增量为1）
     *
     * @param key 缓存键
     * @return 递增后的值，如果操作失败返回null
     */
    Long increment(String key);

    /**
     * 原子递减（指定减量）
     *
     * @param key   缓存键
     * @param delta 减量值
     */
    void decrement(String key, long delta);

    /**
     * 原子递减（默认减量为1）
     *
     * @param key 缓存键
     */
    void decrement(String key);

    /**
     * 获取缓存键的剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（秒），-1表示永久有效，-2表示键不存在，null表示获取失败
     */
    Long getExpire(String key);

    /**
     * 设置缓存键的过期时间
     * <p>可用于更新已存在缓存的过期时间</p>
     *
     * @param key 缓存键
     * @param ttl 过期时间
     */
    void expire(String key, Duration ttl);

    /**
     * 批量设置缓存（使用默认TTL）
     *
     * @param data 键值对映射
     * @param <T>  值的类型泛型
     */
    <T> void batchSet(Map<String, T> data);

    /**
     * 批量设置缓存（指定TTL）
     *
     * @param data 键值对映射
     * @param ttl  过期时间
     * @param <T>  值的类型泛型
     */
    <T> void batchSet(Map<String, T> data, Duration ttl);

    /**
     * 批量获取缓存（类型安全）
     *
     * @param keys  缓存键列表
     * @param clazz 目标类型的Class对象
     * @param <T>   返回类型泛型
     * @return 值列表，顺序与键列表对应，不存在的键对应位置为null
     */
    <T> List<T> batchGet(List<String> keys, Class<T> clazz);

    /**
     * 批量获取缓存（不进行类型转换）
     *
     * @param keys 缓存键列表
     * @return 值列表，顺序与键列表对应，不存在的键对应位置为null
     */
    List<Object> batchGet(List<String> keys);
}