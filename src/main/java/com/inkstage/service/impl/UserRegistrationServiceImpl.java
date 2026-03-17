package com.inkstage.service.impl;

import com.inkstage.entity.model.User;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.UserRegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户注册服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserMapper userMapper;

    @Override
    public boolean isUsernameExists(String username) {
        try {
            log.debug("检查用户名是否已存在, 用户名: {}", username);
            User user = userMapper.findByUsername(username);
            boolean exists = user != null;
            log.info("用户名 {} 已存在", exists ? "" : "不");
            return exists;
        } catch (Exception e) {
            log.error("检查用户名是否已存在失败, 用户名: {}", username, e);
            throw new BusinessException("检查用户名失败");
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try {
            log.debug("检查邮箱是否已存在, 邮箱: {}", email);
            User user = userMapper.findByEmail(email);
            boolean exists = user != null;
            log.info("邮箱{}已存在", exists ? "" : "不");
            return exists;
        } catch (Exception e) {
            log.error("检查邮箱是否已存在失败, 邮箱: {}", email, e);
            throw new BusinessException("检查邮箱失败");
        }
    }

    @Override
    public boolean isPhoneExists(String phone) {
        try {
            log.debug("检查手机号是否已存在, 手机号: {}", phone);
            User user = userMapper.findByPhone(phone);
            boolean exists = user != null;
            log.info("手机号{}已存在", exists ? "" : "不");
            return exists;
        } catch (Exception e) {
            log.error("检查手机号是否已存在失败, 手机号: {}", phone, e);
            throw new BusinessException("检查手机号失败");
        }
    }

    @Override
    public User createUser(User user) {
        try {
            log.debug("创建用户, 用户名: {}", user.getUsername());
            // 检查用户名是否已存在
            if (isUsernameExists(user.getUsername())) {
                throw new BusinessException("用户名已存在");
            }
            // 检查邮箱是否已存在
            if (user.getEmail() != null && isEmailExists(user.getEmail())) {
                throw new BusinessException("邮箱已存在");
            }
            // 检查手机号是否已存在
            if (user.getPhone() != null && isPhoneExists(user.getPhone())) {
                throw new BusinessException("手机号已存在");
            }
            // 执行创建
            int result = userMapper.insert(user);
            if (result == 0) {
                log.warn("创建用户失败, 用户名: {}", user.getUsername());
                throw new BusinessException("创建用户失败");
            }
            return user;
        } catch (Exception e) {
            log.error("创建用户失败, 用户名: {}", user.getUsername(), e);
            throw new BusinessException("创建用户失败");
        }
    }
}