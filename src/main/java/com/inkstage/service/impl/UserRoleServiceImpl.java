package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.mapper.UserRoleMapper;
import com.inkstage.service.UserRoleService;
import com.inkstage.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色关联Service实现类
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final CacheManager cacheManager;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void createUserRole(User user) {
        // 为新注册用户分配默认角色(普通用户)
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(UserRoleEnum.USER.getCode());
        userRole.setAssignedBy(0L); // 默认为系统分配
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setStatus(StatusEnum.ENABLED); // 启用状态
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setDeleted(DeleteStatus.NOT_DELETED);
        userRole.setId(snowflakeIdGenerator.nextId());

        userRoleMapper.insert(userRole);

        cacheManager.delete(CacheKey.keyForUserRoles(user.getId()));
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) {
        String cacheKey = CacheKey.keyForUserRoles(userId);
        List<UserRole> roles = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (roles != null) {
            return roles;
        }
        roles = userRoleMapper.selectByUserId(userId);
        if (roles != null && !roles.isEmpty()) {
            cacheManager.set(cacheKey, roles, CacheTTL.USER_ROLES);
        }
        return roles;
    }

    @Override
    public Boolean updateUserRole(Long userId, UserRoleEnum userRole) {
        int result = userRoleMapper.updateUserRole(userId, userRole);

        cacheManager.delete(CacheKey.keyForUserRoles(userId));

        return result > 0;
    }
}