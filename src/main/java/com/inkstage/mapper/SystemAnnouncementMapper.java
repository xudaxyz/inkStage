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
     *
     * @param id 公告id
     * @return 系统公告
     */
    SystemAnnouncement selectById(@Param("id") Long id);

    /**
     * 插入公告
     *
     * @param announcement 公告实体
     * @return 影响条数
     */
    int insert(SystemAnnouncement announcement);

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     * @return 影响条数
     */
    int update(SystemAnnouncement announcement);

    /**
     * 根据ID删除公告(逻辑删除)
     *
     * @param id 公告id
     * @return 影响条数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 分页查询公告列表
     *
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param type     公共类型
     * @param status   公告状态
     * @param keyword  关键词
     * @return 公告列表
     */
    List<SystemAnnouncement> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize,
                                        @Param("type") AnnouncementType type, @Param("status") StatusEnum status,
                                        @Param("keyword") String keyword);

    /**
     * 查询公告总数
     *
     * @param type    公告类型
     * @param status  公告状态
     * @param keyword 关键词
     * @return 公告条数
     */
    long count(@Param("type") AnnouncementType type, @Param("status") StatusEnum status,
               @Param("keyword") String keyword);

    /**
     * 查询所有公告
     *
     * @return 所有公告列表
     */
    List<SystemAnnouncement> selectAll();

    /**
     * 根据状态查询公告
     *
     * @param status 状态
     * @return 系统公告列表
     */
    List<SystemAnnouncement> selectByStatus(@Param("status") StatusEnum status);


    /**
     * 获取公告阅读数
     *
     * @param id 公告id
     * @return 阅读数
     */
    Long getReadCount(@Param("id") Long id);

    /**
     * 增加公告阅读量
     *
     * @param id    id
     * @param delta 偏移量
     * @return 成功条数
     */
    int updateReadCount(@Param("id") Long id, @Param("delta") int delta);
}
