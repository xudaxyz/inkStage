package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.entity.model.Role;
import com.inkstage.mapper.RoleMapper;
import com.inkstage.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ROLES, key = "#id")
    public Role getRoleById(Long id) {
        return roleMapper.selectByPrimaryKey(id.intValue());
    }

}