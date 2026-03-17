package com.inkstage.security;

import com.inkstage.entity.model.Role;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.mapper.RoleMapper;
import com.inkstage.mapper.UserRoleMapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义UserDetails实现类, 用于包装用户信息
 */
public class UserDetailsImpl implements UserDetails {

    @Getter
    private final User user;
    private final List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.user = user;
        // 初始化权限集合
        this.authorities = new ArrayList<>();
        // 添加默认角色
        this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 加载用户实际角色
        if (user != null && user.getId() != null) {
            List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getId());
            for (UserRole userRole : userRoles) {
                if (userRole != null && userRole.getRoleId() != null) {
                    Role role = roleMapper.selectByPrimaryKey(userRole.getRoleId());
                    if (role != null && role.getCode() != null) {
                        this.authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @NotNull
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserStatus.NORMAL.equals(user.getStatus());
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.NORMAL.equals(user.getStatus());
    }
}
