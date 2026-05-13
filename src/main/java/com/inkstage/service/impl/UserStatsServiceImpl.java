package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.FileService;
import com.inkstage.service.UserStatsService;
import com.inkstage.vo.front.HotUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 用户统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserMapper userMapper;
    private final FileService fileService;
    private final CacheManager cacheManager;

    @Override
    public List<HotUserVO> getHotUsers(Integer limit) {
        try {
            log.debug("获取热门用户, 限制数量: {}", limit);

            String cacheKey = CacheKey.keyForUserHot(limit);
            List<HotUserVO> hotUsers = cacheManager.getWithType(cacheKey, new TypeReference<>() {
            });
            if (hotUsers != null && !hotUsers.isEmpty()) {
                return hotUsers;
            }

            // 查询热门用户
            hotUsers = userMapper.findHotUsers(limit);

            fileService.ensureImageFullUrl(hotUsers);

            if (hotUsers != null && !hotUsers.isEmpty()) {
                cacheManager.set(cacheKey, hotUsers, CacheTTL.DEFAULT);
                log.info("获取热门用户成功, 数量: {}", hotUsers.size());
            }

            return hotUsers;
        } catch (Exception e) {
            log.error("获取热门用户失败, 限制数量: {}", limit, e);
            // 发生异常时，返回空列表，避免影响用户体验
            return List.of();
        }
    }
}