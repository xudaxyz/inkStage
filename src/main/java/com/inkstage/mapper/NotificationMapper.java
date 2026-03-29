package com.inkstage.mapper;

import com.inkstage.entity.model.Notification;
import com.inkstage.enums.NotificationCategory;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.ReadStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper {

    // ==================== 查询（Read） ====================

    /**
     * 根据ID查询通知
     *
     * @param id 通知ID
     * @return 通知信息
     */
    Notification selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询通知列表
     *
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和通知类型查询通知列表
     *
     * @param userId           用户ID
     * @param notificationType 通知类型
     * @return 通知列表
     */
    List<Notification> selectByUserIdAndType(@Param("userId") Long userId, @Param("notificationType") NotificationType notificationType);

    /**
     * 根据用户ID分页查询通知列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 通知列表
     */
    List<Notification> selectByUserIdWithPage(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID和通知类型分页查询通知列表
     *
     * @param userId           用户ID
     * @param notificationType 通知类型
     * @param offset           偏移量
     * @param limit            限制数量
     * @return 通知列表
     */
    List<Notification> selectByUserIdAndTypeWithPage(@Param("userId") Long userId, @Param("notificationType") NotificationType notificationType, @Param("offset") Integer offset, @Param("limit") Integer limit);

    // ==================== 新增（Create） ====================

    /**
     * 插入通知
     *
     * @param notification 通知信息
     * @return 影响行数
     */
    int insert(Notification notification);

    // ==================== 更新（Update） ====================

    /**
     * 根据ID更新通知为已读
     *
     * @param id         通知ID
     * @param readStatus 阅读状态
     * @return 影响行数
     */
    int updateReadStatus(@Param("id") Long id, @Param("readStatus") ReadStatus readStatus);

    /**
     * 根据用户ID更新所有通知为已读
     *
     * @param userId     用户ID
     * @param readStatus 阅读状态
     * @return 影响行数
     */
    int updateAllReadStatus(@Param("userId") Long userId, @Param("readStatus") ReadStatus readStatus);

    // ==================== 删除（Delete） ====================

    /**
     * 根据ID删除通知
     *
     * @param id 通知ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户ID删除所有通知
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    // ==================== 统计（Count） ====================

    /**
     * 查询用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 统计用户通知总数
     *
     * @param userId 用户ID
     * @return 通知总数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户指定类型通知总数
     *
     * @param userId           用户ID
     * @param notificationType 通知类型
     * @return 通知总数
     */
    int countByUserIdAndType(@Param("userId") Long userId, @Param("notificationType") NotificationType notificationType);

    // ==================== 分类相关方法 ====================

    /**
     * 根据用户ID和通知分类查询通知列表
     *
     * @param userId               用户ID
     * @param notificationCategory 通知分类
     * @return 通知列表
     */
    List<Notification> selectByUserIdAndCategory(@Param("userId") Long userId, @Param("notificationCategory") NotificationCategory notificationCategory);

    /**
     * 根据用户ID和通知分类分页查询通知列表
     *
     * @param userId               用户ID
     * @param notificationCategory 通知分类
     * @param offset               偏移量
     * @param limit                限制数量
     * @return 通知列表
     */
    List<Notification> selectByUserIdAndCategoryWithPage(@Param("userId") Long userId, @Param("notificationCategory") NotificationCategory notificationCategory, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID和通知分类更新所有通知为已读
     *
     * @param userId               用户ID
     * @param notificationCategory 通知分类
     * @param readStatus           阅读状态
     * @return 影响行数
     */
    int updateByUserIdAndCategoryReadStatus(@Param("userId") Long userId, @Param("notificationCategory") NotificationCategory notificationCategory, @Param("readStatus") ReadStatus readStatus);

    /**
     * 统计用户指定分类未读通知数量
     *
     * @param userId               用户ID
     * @param notificationCategory 通知分类
     * @return 未读通知数量
     */
    int countUnreadByUserIdAndCategory(@Param("userId") Long userId, @Param("notificationCategory") NotificationCategory notificationCategory);

    /**
     * 统计用户各分类未读通知数量
     *
     * @param userId 用户ID
     * @return 各分类未读通知数量
     */
    List<Map<String, Object>> countUnreadByUserIdGroupByCategory(@Param("userId") Long userId);

    /**
     * 统计用户指定分类通知总数
     *
     * @param userId               用户ID
     * @param notificationCategory 通知分类
     * @return 通知总数
     */
    int countByUserIdAndCategory(@Param("userId") Long userId, @Param("notificationCategory") NotificationCategory notificationCategory);
} 
