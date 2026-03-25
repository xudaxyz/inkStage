package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.SystemAnnouncement;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.StatusEnum;

import java.util.List;

/**
 * 系统公告服务
 */
public interface SystemAnnouncementService {

    /**
     * 创建公告
     */
    boolean createAnnouncement(SystemAnnouncement announcement);

    /**
     * 更新公告
     */
    boolean updateAnnouncement(SystemAnnouncement announcement);

    /**
     * 删除公告
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 获取公告详情
     */
    SystemAnnouncement getAnnouncementById(Long id);

    /**
     * 分页查询公告列表
     */
    PageResult<SystemAnnouncement> getAnnouncementPage(Integer pageNum, Integer pageSize,
                                                    AnnouncementType type, StatusEnum status,
                                                    String keyword);

    /**
     * 获取所有公告
     */
    List<SystemAnnouncement> getAllAnnouncements();

    /**
     * 获取已发布的公告
     */
    List<SystemAnnouncement> getPublishedAnnouncements();

    /**
     * 发布公告
     */
    boolean publishAnnouncement(Long id);

    /**
     * 过期公告
     */
    boolean expireAnnouncement(Long id);

    /**
     * 增加阅读量
     */
    boolean incrementReadCount(Long id);
}
