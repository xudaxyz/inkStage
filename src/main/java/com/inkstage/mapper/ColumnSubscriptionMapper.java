package com.inkstage.mapper;

import com.inkstage.entity.model.ColumnSubscription;
import com.inkstage.vo.front.MyColumnSubscriptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专栏订阅Mapper接口
 */
@Mapper
public interface ColumnSubscriptionMapper {

    // ==================== 查询（Read） ====================

    /**
     * 检查订阅关系是否存在
     * @param userId 订阅者ID
     * @param columnId 专栏ID
     * @return 订阅关系数量
     */
    int checkSubscriptionStatus(@Param("userId") Long userId, @Param("columnId") Long columnId);

    /**
     * 查询用户的订阅专栏列表（包含专栏详情）
     * @param userId 订阅者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订阅专栏列表
     */
    List<MyColumnSubscriptionVO> findMySubscriptions(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询专栏的订阅用户ID列表
     * @param columnId 专栏ID
     * @return 订阅用户ID列表
     */
    List<Long> findSubscriberIds(@Param("columnId") Long columnId);

    // ==================== 新增（Create） ====================

    /**
     * 插入订阅关系
     * @param columnSubscription 订阅关系
     * @return 影响行数
     */
    int insert(ColumnSubscription columnSubscription);

    // ==================== 删除（Delete） ====================

    /**
     * 删除订阅关系
     * @param userId 订阅者ID
     * @param columnId 专栏ID
     * @return 影响行数
     */
    int delete(@Param("userId") Long userId, @Param("columnId") Long columnId);

    // ==================== 统计（Count） ====================

    /**
     * 统计用户的订阅专栏数
     * @param userId 订阅者ID
     * @return 订阅数
     */
    long countSubscriptions(@Param("userId") Long userId);

    /**
     * 统计专栏的订阅数
     * @param columnId 专栏ID
     * @return 订阅数
     */
    long countSubscribers(@Param("columnId") Long columnId);
}
