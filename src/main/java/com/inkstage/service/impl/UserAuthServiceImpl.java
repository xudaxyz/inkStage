package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.AuthDTO;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserAuth;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.VerificationStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.auth.AuthType;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.UserAuthMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.*;
import com.inkstage.utils.IPUtil;
import com.inkstage.cache.utils.RedisUtil;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.inkstage.cache.constant.RedisKeyConstants.LOGIN_ATTEMPT_PREFIX;
import static com.inkstage.cache.constant.RedisKeyConstants.LOGIN_LOCK_PREFIX;

/**
 * 用户认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;
    private final TokenService tokenService;
    private final TokenStoreService tokenStoreService;
    private final VerifyCodeService verifyCodeService;
    private final UserRoleService userRoleService;
    private final RedisUtil redisUtil;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_TIME_MINUTES = 15;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TokenResponse login(AuthDTO authDTO) {
        log.info("用户登录，账号: {}", authDTO.getAccount());

        // 检查登录尝试次数
        checkLoginAttempts(authDTO.getAccount());

        try {
            // 根据账号类型获取用户
            User user = getUserByAccount(authDTO.getAccount());
            if (user == null) {
                incrementLoginAttempts(authDTO.getAccount());
                throw new BusinessException("账号或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == UserStatus.DISABLED) {
                throw new BusinessException("账号已被禁用");
            }
            if (user.getStatus() != UserStatus.NORMAL) {
                throw new BusinessException("账号状态异常");
            }

            // 验证凭证
            if (!validateCredentials(user, authDTO)) {
                incrementLoginAttempts(authDTO.getAccount());
                throw new BusinessException("账号或密码错误");
            }

            // 检查用户是否有角色，如果没有，分配默认角色
            List<UserRole> userRoles = userRoleService.getUserRoles(user.getId());
            if (userRoles.isEmpty()) {
                userRoleService.createUserRole(user);
                log.info("为用户 {} 分配默认角色", user.getId());
            }

            // 重置登录尝试次数
            resetLoginAttempts(authDTO.getAccount());

            // 生成令牌
            TokenResponse tokenResponse = tokenService.generateTokenForUser(user, authDTO);
            log.info("用户登录成功，用户ID: {}", user.getId());
            return tokenResponse;
        } catch (BusinessException e) {
            log.warn("用户登录失败，账号: {}, 原因: {}", authDTO.getAccount(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户登录异常，账号: {}", authDTO.getAccount(), e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }

    @Override
    public TokenResponse adminLogin(AuthDTO authDTO) {
        User user = getUserByAccount(authDTO.getAccount());
        if (user == null) {
            incrementLoginAttempts(authDTO.getAccount());
            throw new BusinessException(ResponseMessage.ACCOUNT_PASSWORD_INCORRECT);
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.fromCode(user.getRoleId());
        if (UserRoleEnum.SUPER_ADMIN != userRoleEnum && UserRoleEnum.ADMIN != userRoleEnum) {
            incrementLoginAttempts(authDTO.getAccount());
            throw new BusinessException(ResponseMessage.ADMIN_ONLY);
        }

        UserAuth userAuth = userAuthMapper.findByUserIdAndType(user.getId(), AuthType.USERNAME);
        if (userAuth == null || !passwordEncoder.matches(authDTO.getPassword(), userAuth.getAuthCredential())) {
            incrementLoginAttempts(authDTO.getAccount());
            throw new BusinessException(ResponseMessage.ACCOUNT_PASSWORD_INCORRECT);
        }

        // 重置登录尝试次数
        resetLoginAttempts(authDTO.getAccount());

        // 生成令牌
        return tokenService.generateTokenForUser(user, authDTO);
    }

    @Override
    @Transactional
    public TokenResponse register(AuthDTO authDTO) {
        log.info("用户注册，账号: {}", authDTO.getAccount());

        try {
            // 检查账号是否已存在
            if (getUserByAccount(authDTO.getAccount()) != null) {
                throw new BusinessException("账号已存在");
            }

            // 注册类型为邮箱或手机时验证验证码
            if (authDTO.getAuthType() == AuthType.EMAIL || authDTO.getAuthType() == AuthType.PHONE) {
                if (!verifyCodeService.verifyCode(authDTO.getAccount(), authDTO.getCode(), "register")) {
                    throw new BusinessException("验证码错误或已过期");
                }
            }


            // 对密码进行统一加密
            authDTO.setPassword(passwordEncoder.encode(authDTO.getPassword()));
            // 创建用户
            User user = createUser(authDTO);

            // 为新用户分配默认角色
            userRoleService.createUserRole(user);

            // 创建用户认证信息
            createUserAuth(user, authDTO);

            // 生成令牌
            TokenResponse tokenResponse = tokenService.generateTokenForUser(user, authDTO);
            log.info("用户注册成功，用户ID: {}", user.getId());
            return tokenResponse;
        } catch (BusinessException e) {
            log.warn("用户注册失败，账号: {}, 原因: {}", authDTO.getAccount(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户注册异常，账号: {}", authDTO.getAccount(), e);
            throw new BusinessException("注册失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void logout(Long userId, String refreshToken) {
        log.info("用户登出，用户ID: {}", userId);

        try {
            // 撤销刷新令牌
            if (refreshToken != null) {
                tokenStoreService.revokeRefreshToken(userId, refreshToken);
            } else {
                // 撤销用户所有刷新令牌
                tokenStoreService.revokeAllRefreshTokens(userId);
            }
            log.info("用户登出成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("用户登出异常，用户ID: {}", userId, e);
            // 登出失败不影响用户体验，记录日志即可
        }
    }

    private User getUserByAccount(String account) {
        // 尝试通过邮箱查找
        User user = userMapper.findByEmail(account);
        if (user != null) {
            return user;
        }
        // 尝试通过用户名查找
        return userMapper.findByUsername(account);
    }

    private boolean validateCredentials(User user, AuthDTO authDTO) {
        if (authDTO.getAuthType() == AuthType.USERNAME) {
            // 密码登录
            UserAuth userAuth = userAuthMapper.findByUserIdAndType(user.getId(), AuthType.USERNAME);
            return userAuth != null && passwordEncoder.matches(authDTO.getPassword(), userAuth.getAuthCredential());
        } else if (authDTO.getAuthType() == AuthType.EMAIL || authDTO.getAuthType() == AuthType.PHONE) {
            // 验证码登录
            return verifyCodeService.verifyCode(authDTO.getAccount(), authDTO.getCode(), "login");
        }
        return false;
    }

    private User createUser(AuthDTO authDTO) {
        User user = new User();
        String name = generateUsername(authDTO.getAccount());
        user.setUsername(name);
        user.setPassword(authDTO.getPassword());
        user.setNickname(name);
        if (authDTO.getAuthType() == AuthType.EMAIL) {
            user.setEmail(authDTO.getAccount());
            user.setEmailVerified(VerificationStatus.VERIFIED);
        }
        if (authDTO.getAuthType() == AuthType.PHONE) {
            user.setPhone(authDTO.getAccount());
            user.setPhoneVerified(VerificationStatus.VERIFIED);
        }
        user.setStatus(UserStatus.NORMAL);
        user.setRoleId(UserRoleEnum.USER.getCode());
        user.setFollowCount(0);
        user.setFollowerCount(0);
        user.setArticleCount(0);
        user.setCommentCount(0);
        user.setLikeCount(0);
        user.setRegisterTime(LocalDateTime.now());
        user.setRegisterIp(IPUtil.getClientIp());
        user.setPrivacy(VisibleStatus.PUBLIC);
        user.setDeleted(DeleteStatus.NOT_DELETED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    private void createUserAuth(User user, AuthDTO authDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(user.getId());
        userAuth.setAuthType(authDTO.getAuthType());
        userAuth.setAuthIdentifier(authDTO.getAccount());

        if (authDTO.getAuthType() == AuthType.USERNAME) {
            userAuth.setAuthCredential(authDTO.getPassword());
        }

        userAuth.setDeleted(DeleteStatus.NOT_DELETED);
        userAuth.setCreateTime(LocalDateTime.now());
        userAuth.setUpdateTime(LocalDateTime.now());

        userAuthMapper.insert(userAuth);
    }

    private String generateUsername(String account) {
        // 从邮箱提取用户名
        if (account.contains("@")) {
            String username = account.split("@")[0];
            // 检查用户名是否已存在
            int suffix = 1;
            String originalUsername = username;
            while (userMapper.findByUsername(username) != null) {
                username = originalUsername + suffix++;
            }
            return username;
        }
        return account;
    }

    private void checkLoginAttempts(String account) {
        String lockKey = LOGIN_LOCK_PREFIX + account;
        String attemptKey = LOGIN_ATTEMPT_PREFIX + account;

        // 检查是否被锁定
        if (redisUtil.hasKey(lockKey)) {
            throw new BusinessException("账号已被锁定，请15分钟后再试");
        }

        // 检查登录尝试次数
        Integer attempts = redisUtil.get(attemptKey, Integer.class);
        if (attempts != null && attempts >= MAX_LOGIN_ATTEMPTS) {
            // 锁定账号
            redisUtil.set(lockKey, 1, LOCKOUT_TIME_MINUTES, TimeUnit.MINUTES);
            redisUtil.delete(attemptKey);
            throw new BusinessException("账号已被锁定，请15分钟后再试");
        }
    }

    private void incrementLoginAttempts(String account) {
        String attemptKey = LOGIN_ATTEMPT_PREFIX + account;
        redisUtil.increment(attemptKey);
        redisUtil.expire(attemptKey, LOCKOUT_TIME_MINUTES, TimeUnit.MINUTES);
    }

    private void resetLoginAttempts(String account) {
        String attemptKey = LOGIN_ATTEMPT_PREFIX + account;
        redisUtil.delete(attemptKey);
    }
}
