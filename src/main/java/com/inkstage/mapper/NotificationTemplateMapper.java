package com.inkstage.mapper;

import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知模板Mapper接口
 */
@Mapper
public interface NotificationTemplateMapper {

    /**
     * 根据ID查询模板
     */
    NotificationTemplate selectById(@Param("id") Long id);

    /**
     * 根据编码查询模板
     */
    NotificationTemplate selectByCode(@Param("code") String code);

    /**
     * 根据通知类型查询模板
     */
    List<NotificationTemplate> selectByType(@Param("type") NotificationType type);

    /**
     * 根据通知类型和渠道查询模板
     */
    NotificationTemplate selectByTypeAndChannel(@Param("type") NotificationType type, @Param("channel") NotificationChannel channel);

    /**
     * 查询所有模板
     */
    List<NotificationTemplate> selectAll();

    /**
     * 根据状态查询模板
     */
    List<NotificationTemplate> selectByStatus(@Param("status") StatusEnum status);

    /**
     * 插入模板
     */
    int insert(NotificationTemplate template);

    /**
     * 更新模板
     */
    int update(NotificationTemplate template);

    /**
     * 根据ID删除模板(逻辑删除)
     */
    int deleteById(@Param("id") Long id);

    /**
     * 分页查询模板列表
     */
    List<NotificationTemplate> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize,
                                          @Param("type") NotificationType type, @Param("status") StatusEnum status,
                                          @Param("keyword") String keyword);

    /**
     * 查询模板总数
     */
    long count(@Param("type") NotificationType type, @Param("status") StatusEnum status,
               @Param("keyword") String keyword);

    /**
     * 检查编码是否存在
     */
    boolean existsByCode(@Param("code") String code);
}
