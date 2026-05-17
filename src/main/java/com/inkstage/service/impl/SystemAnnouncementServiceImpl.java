package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.common.PageResult;
import com.inkstage.entity.model.SystemAnnouncement;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.CountType;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.mapper.SystemAnnouncementMapper;
import com.inkstage.service.SystemAnnouncementService;
import com.inkstage.utils.SnowflakeIdGenerator;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

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
    private final CacheManager cacheManager;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final CountProducer countProducer;

    @Override
    @Transactional
    public boolean createAnnouncement(SystemAnnouncement announcement) {
        announcement.setCreateUserId(UserContext.getCurrentUserId());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setStatus(StatusEnum.DISABLED);
        announcement.setReadCount(0);

        announcement.setId(snowflakeIdGenerator.nextId());
        boolean result = announcementMapper.insert(announcement) > 0;

        cacheManager.deletePattern(CacheKey.HOT_ANNOUNCEMENT);

        return result;
    }

    @Override
    @Transactional
    public boolean updateAnnouncement(SystemAnnouncement announcement) {
        SystemAnnouncement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在: " + announcement.getId());
        }

        announcement.setUpdateUserId(UserContext.getCurrentUserId());
        announcement.setUpdateTime(LocalDateTime.now());

        boolean result = announcementMapper.update(announcement) > 0;

        cacheManager.deletePattern(CacheKey.HOT_ANNOUNCEMENT);

        return result;
    }

    @Override
    @Transactional
    public boolean deleteAnnouncement(Long id) {
        boolean result = announcementMapper.deleteById(id) > 0;

        cacheManager.deletePattern(CacheKey.HOT_DATA + "announcement*");

        return result;
    }

    @Override
    public SystemAnnouncement getAnnouncementById(Long id) {
        String cacheKey = CacheKey.keyForAnnouncementDetail(id);
        SystemAnnouncement result = cacheManager.get(cacheKey, SystemAnnouncement.class);
        if (result != null) {
            return result;
        }
        result = announcementMapper.selectById(id);
        if (result != null) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
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
    public List<SystemAnnouncement> getPublishedAnnouncements() {
        String cacheKey = CacheKey.keyForHotAnnouncementPublished();
        List<SystemAnnouncement> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null && !result.isEmpty()) {
            return result;
        }
        result = announcementMapper.selectByStatus(StatusEnum.ENABLED);
        if (result != null && !result.isEmpty()) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean publishAnnouncement(Long id) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setId(id);
        announcement.setStatus(StatusEnum.ENABLED);
        announcement.setPublishTime(LocalDateTime.now());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());

        boolean result = announcementMapper.update(announcement) > 0;

        cacheManager.deletePattern(CacheKey.HOT_DATA + "announcement*");

        return result;
    }

    @Override
    @Transactional
    public boolean expireAnnouncement(Long id) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setId(id);
        announcement.setStatus(StatusEnum.DISABLED);
        announcement.setExpireTime(LocalDateTime.now());
        announcement.setUpdateUserId(UserContext.getCurrentUserId());

        boolean result = announcementMapper.update(announcement) > 0;

        cacheManager.deletePattern(CacheKey.HOT_DATA + "announcement*");

        return result;
    }

    @Override
    public boolean incrementReadCount(Long id) {
        countProducer.sendCountMessage(CountType.ANNOUNCEMENT_READ, id, 1);
        return true;
    }
}
