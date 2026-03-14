package com.inkstage.service;

import com.inkstage.vo.front.HotUserVO;

import java.util.List;

/**
 * 用户统计服务接口
 */
public interface UserStatsService {

    /**
     * 获取热门用户
     *
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<HotUserVO> getHotUsers(Integer limit);
}