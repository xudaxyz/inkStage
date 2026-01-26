package com.inkstage.service.impl;

import com.inkstage.common.model.ResponseMessage;
import com.inkstage.entity.model.User;
import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // 根据用户名查询用户
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.error("用户名不存在: {}", username);
            throw new UsernameNotFoundException(ResponseMessage.USERNAME_NOT_FOUND.getMessage() + ": " + username);
        }

        // 构建UserDetails对象，目前返回空权限集合
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of());
    }
}