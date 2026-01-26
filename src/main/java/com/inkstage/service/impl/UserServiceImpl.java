package com.inkstage.service.impl;

import com.inkstage.common.exception.BusinessException;
import com.inkstage.common.model.ResponseMessage;
import com.inkstage.utils.IPUtil;
import com.inkstage.constant.InkConstant;
import com.inkstage.entity.model.User;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.Gender;
import com.inkstage.enums.UserStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isUsernameExists(String username) {
        User user = userMapper.selectByUsername(username);
        return user != null;
    }

    @Override
    public User createUser(User user) {
        try {
            // 检查用户名是否已存在
            if (isUsernameExists(user.getUsername())) {
                log.warn("用户名已存在: {}", user.getUsername());
                throw new BusinessException(ResponseMessage.USERNAME_EXISTS);
            }
            // 设置用户默认值
            LocalDateTime now = LocalDateTime.now();
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setAvatar(InkConstant.DEFAULT_AVATAR_URL);
            user.setGender(Gender.UNKNOWN);
            user.setNickname(user.getUsername());
            user.setArticleCount(0);
            user.setCommentCount(0);
            user.setFollowCount(0);
            user.setFollowerCount(0);
            user.setLikeCount(0);
            user.setPrivacy(VisibleStatus.PUBLIC);
            user.setStatus(UserStatus.NORMAL);
            user.setLastLoginIp(IPUtil.getClientIp());
            user.setRegisterIp(IPUtil.getClientIp());
            user.setRegisterTime(now);
            user.setDeleted(DeleteStatus.NOT_DELETED);
            
            // 加密密码
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // 插入用户
            userMapper.insert(user);
            log.info("用户创建成功: {}", user.getUsername());
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户创建失败: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}