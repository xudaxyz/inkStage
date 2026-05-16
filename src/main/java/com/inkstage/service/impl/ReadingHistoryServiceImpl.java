package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ReadingHistoryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.ReadingHistory;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.ReadingHistoryMapper;
import com.inkstage.service.FileService;
import com.inkstage.service.ReadingHistoryService;
import com.inkstage.utils.SnowflakeIdGenerator;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ReadingHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 阅读历史服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingHistoryServiceImpl implements ReadingHistoryService {

    private final ReadingHistoryMapper readingHistoryMapper;
    private final ArticleMapper articleMapper;
    private final FileService fileService;
    private final CacheManager cacheManager;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public boolean saveOrUpdateReadingHistory(ReadingHistoryDTO dto) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法保存阅读历史");
                return false;
            }

            // 检查文章是否存在
            Article article = articleMapper.findById(dto.getArticleId());
            if (article == null) {
                log.warn("文章不存在: {}", dto.getArticleId());
                return false;
            }

            // 构建阅读历史实体
            ReadingHistory readingHistory = new ReadingHistory();
            readingHistory.setId(snowflakeIdGenerator.nextId());
            readingHistory.setUserId(userId);
            readingHistory.setArticleId(dto.getArticleId());
            // 确保进度值在0-100之间
            int progress = dto.getProgress();
            readingHistory.setProgress(Math.clamp(progress, 0, 100));
            // 确保持续时间不为负
            readingHistory.setDuration(Math.max(0, dto.getDuration()));
            readingHistory.setLastReadTime(LocalDateTime.now());
            // 确保滚动位置不为负
            readingHistory.setScrollPosition(Math.max(0, dto.getScrollPosition()));

            // 保存或更新阅读历史
            int result = readingHistoryMapper.saveOrUpdate(readingHistory);
            
            cacheManager.deletePattern(CacheKey.READING_HISTORY + userId);
            
            return result > 0;
        } catch (Exception e) {
            log.error("保存阅读历史失败", e);
            return false;
        }
    }

    @Override
    public PageResult<ReadingHistoryVO> getReadingHistoryList(Integer page, Integer size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return getReadingHistoryListWithDetails(page, size);
        }
        
        String cacheKey = CacheKey.keyForReadingHistory(userId, page, size);
        PageResult<ReadingHistoryVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {});
        if (result != null) {
            return result;
        }
        
        result = getReadingHistoryListWithDetails(page, size);
        if (result != null && result.getTotal() > 0) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
    }

    @Override
    public PageResult<ReadingHistoryVO> getReadingHistoryListWithDetails(Integer page, Integer size) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录");
                return PageResult.build(null, 0L, page, size);
            }

            // 计算偏移量
            int offset = (page - 1) * size;

            // 查询阅读历史列表（带详细信息）
            List<ReadingHistoryVO> voList = readingHistoryMapper.findByUserIdWithDetails(userId, offset, size);
            int total = readingHistoryMapper.countByUserId(userId);

            // 处理头像和封面图
            fileService.ensureImageFullUrl(voList);

            return PageResult.build(voList, (long) total, page, size);
        } catch (Exception e) {
            log.error("获取阅读历史列表失败", e);
            return PageResult.build(null, 0L, page, size);
        }
    }

    @Override
    public boolean deleteReadingHistory(Long articleId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法删除阅读历史");
                return false;
            }

            int result = readingHistoryMapper.deleteByUserIdAndArticleId(userId, articleId);
            
            cacheManager.deletePattern(CacheKey.READING_HISTORY + userId);
            
            return result > 0;
        } catch (Exception e) {
            log.error("删除阅读历史失败", e);
            return false;
        }
    }

    @Override
    public boolean clearReadingHistory() {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法清空阅读历史");
                return false;
            }

            int result = readingHistoryMapper.deleteByUserId(userId);
            
            cacheManager.deletePattern(CacheKey.READING_HISTORY + userId);
            
            return result > 0;
        } catch (Exception e) {
            log.error("清空阅读历史失败", e);
            return false;
        }
    }

    @Override
    public ReadingHistoryVO getReadingHistoryByArticleId(Long articleId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，不获取阅读历史");
                return null;
            }

            String cacheKey = CacheKey.keyForUserArticleReadingHistory(userId, articleId);
            ReadingHistoryVO vo = cacheManager.getWithType(cacheKey, new TypeReference<>() {});
            if (vo != null) {
                return vo;
            }

            vo = readingHistoryMapper.findByUserIdAndArticleIdWithDetails(userId, articleId);
            if (vo != null) {
                fileService.ensureImageFullUrl(vo);
                cacheManager.set(cacheKey, vo, CacheTTL.DEFAULT);
            }
            return vo;
        } catch (Exception e) {
            log.error("获取阅读历史失败", e);
            return null;
        }
    }

    @Override
    public List<ReadingHistoryVO> getReadingHistoriesByArticleIds(List<Long> articleIds) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法获取阅读历史");
                return new ArrayList<>();
            }

            // 使用批量查询方法
            List<ReadingHistoryVO> voList = readingHistoryMapper.findByUserIdAndArticleIdsWithDetails(userId, articleIds);

            // 处理头像和封面图
            for (ReadingHistoryVO vo : voList) {
                // 处理头像
                if (vo.getAvatar() != null) {
                    vo.setAvatar(fileService.convertToFullUrl(vo.getAvatar()));
                }
                // 处理封面图
                if (vo.getCoverImage() != null) {
                    vo.setCoverImage(fileService.convertToFullUrl(vo.getCoverImage()));
                }
            }
            return voList;
        } catch (Exception e) {
            log.error("批量获取阅读历史失败", e);
            return new ArrayList<>();
        }
    }

}