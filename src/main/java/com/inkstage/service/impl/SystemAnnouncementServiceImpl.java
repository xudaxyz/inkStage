package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.SystemAnnouncement;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.mapper.SystemAnnouncementMapper;
import com.inkstage.service.SystemAnnouncementService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    private final SystemAnnouncementMapper announcementMapper;

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean createAnnouncement(SystemAnnouncement announcement) {
        announcement.setCreateUserId(UserContext.getCurrentUserId());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setStatus(StatusEnum.DISABLED);
        announcement.setReadCount(0);

        return announcementMapper.insert(announcement) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean updateAnnouncement(SystemAnnouncement announcement) {
        SystemAnnouncement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在: " + announcement.getId());
        }

        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        announcement.setUpdateTime(LocalDateTime.now());

        return announcementMapper.update(announcement) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean deleteAnnouncement(Long id) {
        return announcementMapper.deleteById(id) > 0;
    }

    @Override
    @Cacheable(value = "announcement",
            key = "#id",
            unless = "#result == null")
    public SystemAnnouncement getAnnouncementById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public PageResult<SystemAnnouncement> getAnnouncementPage(Integer pageNum, Integer pageSize,
                                                              AnnouncementType type, StatusEnum status,
                                                              String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<SystemAnnouncement> list = announcementMapper.selectPage(offset, pageSize, type, status, keyword);
        long total = announcementMapper.count(type, status, keyword);
        return PageResult.build(list, total, pageNum, pageSize);
    }

    @Override
    public List<SystemAnnouncement> getAllAnnouncements() {
        return announcementMapper.selectAll();
    }

    @Override
    @Cacheable(value = "announcement",
            key = "'published'",
            unless = "#result == null or #result.isEmpty()")
    public List<SystemAnnouncement> getPublishedAnnouncements() {
        return announcementMapper.selectByStatus(StatusEnum.ENABLED);
    }

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean publishAnnouncement(Long id) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setId(id);
        announcement.setStatus(StatusEnum.ENABLED);
        announcement.setPublishTime(LocalDateTime.now());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        return announcementMapper.update(announcement) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean expireAnnouncement(Long id) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setId(id);
        announcement.setStatus(StatusEnum.DISABLED);
        announcement.setExpireTime(LocalDateTime.now());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        return announcementMapper.update(announcement) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "announcement", allEntries = true)
    public boolean incrementReadCount(Long id) {
        return announcementMapper.incrementReadCount(id) > 0;
    }
}
