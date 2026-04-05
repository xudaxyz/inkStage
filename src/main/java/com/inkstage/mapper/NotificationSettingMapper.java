package com.inkstage.mapper;

import com.inkstage.entity.model.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知设置Mapper
 */
@Mapper
public interface NotificationSettingMapper {

    // ==================== 查询（Read） ====================

    /**
     * 根据用户ID获取通知设置
     *
     * @param userId 用户ID
     * @return 通知设置
     */
    NotificationSetting selectByUserId(@Param("userId") Long userId);

    // ==================== 新增（Create） ====================

    /**
     * 插入通知设置
     *
     * @param setting 通知设置
     * @return 影响行数
     */
    int insert(NotificationSetting setting);

    // ==================== 更新（Update） ====================

    /**
     * 更新通知设置
     *
     * @param setting 通知设置
     * @return 影响行数
     */
    int update(NotificationSetting setting);

    // ==================== 删除（Delete） ====================

    /**
     * 根据用户ID删除通知设置
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 检查通知是否启用
     *
     * @param userId               用户ID
     * @param notificationTypeCode 通知类型代码
     * @return 通知是否启用 1表示启用, 0表示不启用
     */
    Integer checkNotificationEnabled(@Param("userId") Long userId, @Param("notificationTypeCode") Integer notificationTypeCode);

    /**
     * 更新单个通知设置
     *
     * @param userId               用户ID
     * @param notificationTypeName 通知类型名称
     * @param notificationValue    通知值
     * @return 影响行数
     */
    boolean updateNotificationSetting(@Param("userId") Long userId, @Param("notificationTypeName") String notificationTypeName, @Param("notificationValue") Boolean notificationValue);
}
