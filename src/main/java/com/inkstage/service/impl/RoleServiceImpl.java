package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.entity.model.Role;
import com.inkstage.mapper.RoleMapper;
import com.inkstage.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final CacheManager cacheManager;

    @Override
    public Role getRoleById(Long id) {
        String cacheKey = CacheKey.keyForRole(id);
        Role role = cacheManager.get(cacheKey, Role.class);
        if (role != null) {
            return role;
        }
        role = roleMapper.selectByPrimaryKey(id.intValue());
        if (role != null) {
            cacheManager.set(cacheKey, role, CacheTTL.ROLES);
        }
        return role;
    }

}