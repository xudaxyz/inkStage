package com.inkstage.service.impl;

import com.inkstage.service.UserStatsService;
import com.inkstage.utils.RedisUtil;
import com.inkstage.vo.front.HotUserVO;
import com.inkstage.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

    @Override
    public List<HotUserVO> getHotUsers(Integer limit) {
        try {
            log.debug("获取热门用户, 限制数量: {}", limit);

            // 生成缓存键
            String cacheKey = "user:hot:" + limit;

            // 尝试从缓存获取
            List<HotUserVO> hotUsers = redisUtil.getWithType(cacheKey, new TypeReference<>() {});
            if (hotUsers != null) {
                log.debug("从缓存获取热门用户成功, 缓存键: {}", cacheKey);
                return hotUsers;
            }

            // 查询热门用户
            hotUsers = userMapper.findHotUsers(limit);

            // 更新缓存
            redisUtil.set(cacheKey, hotUsers, 30, TimeUnit.MINUTES);
            log.debug("更新热门用户缓存, 缓存键: {}", cacheKey);

            log.info("获取热门用户成功, 数量: {}", hotUsers.size());
            return hotUsers;
        } catch (Exception e) {
            log.error("获取热门用户失败, 限制数量: {}", limit, e);
            // 发生异常时，返回空列表，避免影响用户体验
            return List.of();
        }
    }
}