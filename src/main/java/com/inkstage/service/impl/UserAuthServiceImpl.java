package com.inkstage.service.impl;

import com.inkstage.common.exception.BusinessException;
import com.inkstage.common.model.ResponseMessage;
import com.inkstage.constant.AuthTypeConstant;
import com.inkstage.dto.OAuth2RegisterDTO;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserAuth;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.StatusEnum;
import com.inkstage.mapper.UserAuthMapper;
import com.inkstage.service.TokenService;
import com.inkstage.service.UserAuthService;
import com.inkstage.service.UserRoleService;
import com.inkstage.service.UserService;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户认证Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthMapper userAuthMapper;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public TokenResponse register(OAuth2RegisterDTO oAuth2RegisterDTO) {
        if (oAuth2RegisterDTO == null) {
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }

        try {
            String encodePassword = passwordEncoder.encode(oAuth2RegisterDTO.getPassword());
            // 创建用户
            User user = new User();
            user.setUsername(oAuth2RegisterDTO.getUsername());
            user.setNickname(oAuth2RegisterDTO.getUsername());
            user.setPassword(encodePassword);
            User newUser = userService.createUser(user);

            // 创建用户认证信息
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(newUser.getId());
            userAuth.setAuthType(AuthTypeConstant.USERNAME);
            userAuth.setAuthIdentifier(oAuth2RegisterDTO.getUsername()); // 存储用户名
            userAuth.setAuthCredential(encodePassword); // 存储加密后的密码
            userAuth.setEnabled(StatusEnum.ENABLED);
            userAuth.setCreateTime(LocalDateTime.now());
            userAuth.setLastAuthTime(LocalDateTime.now());
            userAuth.setDeleted(DeleteStatus.NOT_DELETED);
            userAuthMapper.insert(userAuth);

            // 为用户分配角色
            userRoleService.createUserRole(newUser);

            // 生成OAuth2令牌
            return tokenService.generateTokenForUser(newUser, oAuth2RegisterDTO);
        } catch (BusinessException e) {
            log.error("用户注册失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }
    }
}