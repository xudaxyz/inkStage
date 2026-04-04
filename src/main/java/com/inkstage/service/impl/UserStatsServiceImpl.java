package com.inkstage.service.impl;

import com.inkstage.mapper.UserMapper;
import com.inkstage.service.FileService;
import com.inkstage.service.UserStatsService;
import com.inkstage.vo.front.HotUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Override
    @Cacheable(value = "user:hot",
            key = "#limit",
            unless = "#result == null or #result.isEmpty()")
    public List<HotUserVO> getHotUsers(Integer limit) {
        try {
            log.debug("获取热门用户, 限制数量: {}", limit);

            // 查询热门用户
            List<HotUserVO> hotUsers = userMapper.findHotUsers(limit);
            // 确保用户头像 URL 完整
            fileService.ensureHotUserImgAreFullUrl(hotUsers);

            log.info("获取热门用户成功, 数量: {}", hotUsers.size());
            return hotUsers;
        } catch (Exception e) {
            log.error("获取热门用户失败, 限制数量: {}", limit, e);
            // 发生异常时，返回空列表，避免影响用户体验
            return List.of();
        }
    }
}