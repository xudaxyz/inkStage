package com.inkstage.service;

import com.inkstage.enums.CountType;

import java.util.List;

/**
 * 通用计数服务接口
 * <p>
 * 提供系统中所有计数字段的统一更新和查询能力。
 * 支持单条和批量计数操作，采用 Redis 缓存 + 异步同步数据库的模式。
 */
public interface CountService {

    /**
     * 更新计数（通用方法）
     *
     * @param countType 计数类型
     * @param targetId  目标记录ID
     * @param delta     增量（正数增加，负数减少）
     */
    void updateCount(CountType countType, Long targetId, int delta);

    /**
     * 批量更新计数
     * <p>
     * 一次业务操作触发多个计数更新时使用，确保所有计数在同一批次中完成。
     *
     * @param countType 计数类型
     * @param targetIds 目标记录ID列表
     * @param delta     增量（正数增加，负数减少）
     */
    void batchUpdate(CountType countType, List<Long> targetIds, int delta);

    /**
     * 获取计数
     *
     * @param countType 计数类型
     * @param targetId  目标记录ID
     * @return 计数值
     */
    long getCount(CountType countType, Long targetId);

    /**
     * 同步计数到数据库
     * <p>
     * 手动触发同步，用于补偿/修复场景
     *
     * @param countType 计数类型
     * @param targetId  目标记录ID
     * @param delta     增量
     */
    void syncToDatabase(CountType countType, Long targetId, int delta);

    /**
     * 从数据库直接获取计数
     * <p>
     * 直接查询数据库中的计数值，不经过Redis缓存。
     * 用于对账、补偿等需要获取真实数据的场景。
     *
     * @param countType 计数类型
     * @param targetId  目标记录ID
     * @return 数据库中的计数值，如果记录不存在返回null
     */
    Long getCountFromDatabase(CountType countType, Long targetId);
}
