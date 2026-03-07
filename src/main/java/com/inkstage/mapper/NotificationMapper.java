package com.inkstage.mapper;

import com.inkstage.entity.model.Notification;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.ReadStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 根据用户ID查询通知列表
     */
    List<Notification> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和通知类型查询通知列表
     */
    List<Notification> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);
    
    /**
     * 根据ID更新通知为已读
     */
    int updateReadStatus(@Param("id") Long id, @Param("readStatus") ReadStatus readStatus);
    
    /**
     * 根据用户ID更新所有通知为已读
     */
    int updateAllReadStatus(@Param("userId") Long userId, @Param("readStatus") ReadStatus readStatus);
    
    /**
     * 根据ID删除通知
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据用户ID删除所有通知
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 插入通知
     */
    int insert(Notification notification);
    
    /**
     * 根据ID查询通知
     */
    Notification selectById(@Param("id") Long id);
    
    /**
     * 查询用户未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID分页查询通知列表
     */
    List<Notification> selectByUserIdWithPage(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据用户ID和通知类型分页查询通知列表
     */
    List<Notification> selectByUserIdAndTypeWithPage(@Param("userId") Long userId, @Param("type") NotificationType type, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计用户通知总数
     */
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户指定类型通知总数
     */
    int countByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);
}
