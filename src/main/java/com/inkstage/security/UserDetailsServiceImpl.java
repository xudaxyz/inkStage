package com.inkstage.security;

import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.User;
import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

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

        // 构建UserDetails对象, 目前返回空权限集合
        return new UserDetailsImpl(user);
    }
}