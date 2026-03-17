package com.inkstage.security;

import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.User;
import com.inkstage.mapper.RoleMapper;
import com.inkstage.mapper.UserRoleMapper;
import com.inkstage.service.UserCacheService;
import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户详情服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 根据账号类型查询用户
        User user;
        if (username.contains("@")) {
            // 邮箱
            user = userService.getUserByEmail(username);
        } else if (username.matches("^1[3-9]\\d{9}$")) {
            // 手机号
            user = userService.getUserByPhone(username);
        } else {
            // 用户名
            user = userService.getUserByUsername(username);
        }

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException(ResponseMessage.USER_NOT_FOUND.getMessage() + ": " + username);
        }

        // 缓存用户信息
        userCacheService.cacheUser(user);

        // 构建UserDetails对象
        return new UserDetailsImpl(user, userRoleMapper, roleMapper);
    }

    /**
     * 根据用户ID加载用户详情
     * 支持从缓存中获取
     *
     * @param userId 用户ID
     * @return UserDetails对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        // 先尝试从缓存中获取
        Optional<User> cachedUser = userCacheService.getUserFromCache(userId);
        if (cachedUser.isPresent()) {
            log.debug("从缓存中获取用户信息: {}", userId);
            return new UserDetailsImpl(cachedUser.get(), userRoleMapper, roleMapper);
        }

        // 从数据库中获取
        try {
            User user = userService.getUserById(Long.parseLong(userId));
            if (user == null) {
                log.error("用户ID: {}不存在", userId);
                throw new UsernameNotFoundException(ResponseMessage.USER_NOT_FOUND.getMessage() + ": " + userId);
            }

            // 缓存用户信息
            userCacheService.cacheUser(user);

            return new UserDetailsImpl(user, userRoleMapper, roleMapper);
        } catch (NumberFormatException e) {
            log.error("无效的用户ID格式: {}", userId, e);
            throw new UsernameNotFoundException(ResponseMessage.USER_NOT_FOUND.getMessage() + ": " + userId);
        }
    }
}