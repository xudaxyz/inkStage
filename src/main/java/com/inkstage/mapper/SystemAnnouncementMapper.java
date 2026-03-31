package com.inkstage.mapper;

import com.inkstage.entity.model.SystemAnnouncement;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.common.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统公告Mapper接口
 */
@Mapper
public interface SystemAnnouncementMapper {

    /**
     * 根据ID查询公告
     */
    SystemAnnouncement selectById(@Param("id") Long id);

    /**
     * 插入公告
     */
    int insert(SystemAnnouncement announcement);

    /**
     * 更新公告
     */
    int update(SystemAnnouncement announcement);

    /**
     * 根据ID删除公告(逻辑删除)
     */
    int deleteById(@Param("id") Long id);

    /**
     * 分页查询公告列表
     */
    List<SystemAnnouncement> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize,
                                       @Param("type") AnnouncementType type, @Param("status") StatusEnum status,
                                       @Param("keyword") String keyword);

    /**
     * 查询公告总数
     */
    long count(@Param("type") AnnouncementType type, @Param("status") StatusEnum status,
               @Param("keyword") String keyword);

    /**
     * 查询所有公告
     */
    List<SystemAnnouncement> selectAll();

    /**
     * 根据状态查询公告
     */
    List<SystemAnnouncement> selectByStatus(@Param("status") StatusEnum status);

    /**
     * 增加阅读量
     */
    int incrementReadCount(@Param("id") Long id);
}
