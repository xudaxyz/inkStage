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
     * @param userId 用户ID
     * @return 通知设置
     */
    NotificationSetting selectByUserId(@Param("userId") Long userId);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入通知设置
     * @param setting 通知设置
     * @return 影响行数
     */
    int insert(NotificationSetting setting);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新通知设置
     * @param setting 通知设置
     * @return 影响行数
     */
    int update(NotificationSetting setting);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据用户ID删除通知设置
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);
}
