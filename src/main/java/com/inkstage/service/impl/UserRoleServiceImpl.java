package com.inkstage.service.impl;

import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.StatusEnum;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.mapper.UserRoleMapper;
import com.inkstage.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色关联Service实现类
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

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
        
        userRoleMapper.insert(userRole);
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) {
        return userRoleMapper.selectByUserId(userId);
    }

    @Override
    public Boolean updateUserRole(Long userId, UserRoleEnum userRole) {
        int result = userRoleMapper.updateUserRole(userId, userRole);
        return result > 0;
    }
}