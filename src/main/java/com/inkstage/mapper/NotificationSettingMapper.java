package com.inkstage.mapper;

import com.inkstage.entity.model.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知设置Mapper
 */
@Mapper
public interface NotificationSettingMapper {

    /**
     * 根据用户ID获取通知设置
     */
    NotificationSetting selectByUserId(@Param("userId") Long userId);

    /**
     * 插入通知设置
     */
    int insert(NotificationSetting setting);

    /**
     * 更新通知设置
     */
    int update(NotificationSetting setting);

    /**
     * 根据用户ID删除通知设置
     */
    int deleteByUserId(@Param("userId") Long userId);
}
