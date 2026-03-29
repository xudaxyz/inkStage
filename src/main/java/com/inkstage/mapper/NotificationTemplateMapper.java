package com.inkstage.mapper;

import com.inkstage.dto.admin.NotificationTemplateQueryDTO;
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
    List<NotificationTemplate> selectByType(@Param("notificationType") NotificationType notificationType);

    /**
     * 根据通知类型和渠道查询模板
     */
    NotificationTemplate selectByTypeAndChannel(@Param("notificationType") NotificationType notificationType, @Param("notificationChannel") NotificationChannel notificationChannel);

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
    List<NotificationTemplate> selectPageByQuery(@Param("query") NotificationTemplateQueryDTO queryDTO, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询模板总数
     */
    long countByQuery(@Param("query") NotificationTemplateQueryDTO queryDTO);

    /**
     * 检查编码是否存在
     */
    boolean existsByCode(@Param("code") String code);
}
