package com.inkstage.mapper;

import com.inkstage.entity.model.DashboardStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仪表盘统计数据Mapper接口
 */
@Mapper
public interface DashboardStatsMapper {

    // ==================== 查询（Read） ====================

    /**
     * 根据ID查询统计数据
     *
     * @param id 统计数据ID
     * @return 统计数据实体
     */
    DashboardStats findById(Long id);

    /**
     * 根据统计键查询统计数据
     *
     * @param statKey 统计键
     * @return 统计数据实体
     */
    DashboardStats findByStatKey(@Param("statKey") String statKey);

    /**
     * 根据数据类型查询统计数据
     *
     * @param dataType 数据类型
     * @return 统计数据列表
     */
    List<DashboardStats> findByDataType(@Param("dataType") String dataType);

    /**
     * 根据统计键和时间值查询统计数据
     *
     * @param statKey   统计键
     * @param timeValue 时间值
     * @return 统计数据实体
     */
    DashboardStats findByStatKeyAndTimeValue(@Param("statKey") String statKey, @Param("timeValue") String timeValue);

    /**
     * 查询指定统计键的趋势数据
     *
     * @param statKey 统计键
     * @param limit   限制数量
     * @return 统计数据列表
     */
    List<DashboardStats> findTrendData(@Param("statKey") String statKey, @Param("limit") Integer limit);

    // ==================== 新增（Create） ====================

    /**
     * 插入统计数据
     *
     * @param dashboardStats 统计数据实体
     * @return 影响行数
     */
    int insert(DashboardStats dashboardStats);

    // ==================== 更新（Update） ====================

    /**
     * 更新统计数据
     *
     * @param dashboardStats 统计数据实体
     * @return 影响行数
     */
    int update(DashboardStats dashboardStats);

    /**
     * 根据统计键更新统计值
     *
     * @param statKey   统计键
     * @param statValue 统计值
     * @return 影响行数
     */
    int updateByStatKey(@Param("statKey") String statKey, @Param("statValue") String statValue);

    /**
     * 根据统计键和时间值更新统计值
     *
     * @param statKey   统计键
     * @param timeValue 时间值
     * @param statValue 统计值
     * @return 影响行数
     */
    int updateByStatKeyAndTimeValue(@Param("statKey") String statKey, @Param("timeValue") String timeValue, @Param("statValue") String statValue);

    // ==================== 删除（Delete） ====================

    /**
     * 根据ID删除统计数据
     *
     * @param id 统计数据ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据统计键删除统计数据
     *
     * @param statKey 统计键
     * @return 影响行数
     */
    int deleteByStatKey(@Param("statKey") String statKey);

    /**
     * 删除指定时间之前的趋势数据
     *
     * @param timeValue 时间值
     */
    void deleteOldTrendData(@Param("timeValue") String timeValue);
}
