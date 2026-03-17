package com.inkstage.service.impl;

import com.inkstage.exception.BusinessException;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.AuthTypeConstant;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.dto.AuthDTO;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserAuth;
import com.inkstage.enums.DefaultStatus;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.StatusEnum;
import com.inkstage.enums.VerificationStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.auth.AccountType;
import com.inkstage.enums.auth.AuthType;
import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.mapper.UserAuthMapper;
import com.inkstage.service.TokenService;
import com.inkstage.service.UserAuthService;
import com.inkstage.service.UserRoleService;
import com.inkstage.service.UserService;
import com.inkstage.service.VerifyCodeService;
import com.inkstage.utils.AccountUtil;
import com.inkstage.utils.IPUtil;
import com.inkstage.utils.PasswordUtil;
import com.inkstage.utils.RedisUtil;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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
    private final VerifyCodeService verifyCodeService;
    private final RedisUtil redisUtil;
    private final AuthenticationManager authenticationManager;

    // 注册频率限制(秒)
    private static final int REGISTER_RATE_LIMIT_SECONDS = 300;
    // 登录失败限制(秒)
    private static final int LOGIN_FAIL_LIMIT_SECONDS = 300;
    // 最大登录失败次数
    private static final int MAX_LOGIN_FAIL_TIMES = 10;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public TokenResponse register(AuthDTO authDTO) {
        if (authDTO == null) {
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }

        try {
            // 检查注册频率限制
            String clientIp = IPUtil.getClientIp();
            String limitKey = RedisKeyConstants.buildRegisterLimitKey(clientIp);
            if (redisUtil.hasKey(limitKey)) {
                throw new BusinessException(ResponseMessage.REGISTER_TOO_FREQUENTLY);
            }

            String account = authDTO.getAccount();
            String password = authDTO.getPassword();
            AuthType authType = authDTO.getAuthType();
            
            // 检查认证类型
            if (authType == null) {
                throw new BusinessException(ResponseMessage.AUTH_TYPE_ERROR);
            }

            // 验证账号格式
            if (!AccountUtil.validateAccountFormat(account)) {
                throw new BusinessException("账号格式错误");
            }

            // 验证码注册
            if (AuthType.CODE == authType) {
                String code = authDTO.getCode();
                if (code == null || code.isEmpty()) {
                    throw new BusinessException(ResponseMessage.CAPTCHA_REQUIRED);
                }
                // 验证验证码
                boolean verifyResult = verifyCodeService.verifyCode(account, code, "register");
                if (!verifyResult) {
                    throw new BusinessException(ResponseMessage.CAPTCHA_ERROR);
                }
                // 生成随机密码
                password = PasswordUtil.generateRandomPassword(12);
            } else if (AuthType.PASSWORD == authType) {
                // 密码注册
                if (password == null || password.isEmpty()) {
                    throw new BusinessException(ResponseMessage.PASSWORD_REQUIRED);
                }
                // 验证密码强度
                if (!AccountUtil.validatePasswordStrength(password)) {
                    throw new BusinessException("密码强度不足，至少需要包含大小写字母、数字和特殊字符中的三种");
                }
            } else {
                throw new BusinessException(ResponseMessage.AUTH_TYPE_ERROR);
            }

            // 检查是否同意条款
            if (!authDTO.isAgreeTerms()) {
                throw new BusinessException(ResponseMessage.AGREE_TERMS_REQUIRED);
            }

            // 检查账号唯一性
            AccountType accountType = AccountUtil.getAccountType(account);
            switch (accountType) {
                case EMAIL:
                    if (userService.isEmailExists(account)) {
                        throw new BusinessException(ResponseMessage.EMAIL_EXISTS);
                    }
                    break;
                case PHONE:
                    if (userService.isPhoneExists(account)) {
                        throw new BusinessException(ResponseMessage.PHONE_EXISTS);
                    }
                    break;
                case USERNAME:
                    if (userService.isUsernameExists(account)) {
                        throw new BusinessException(ResponseMessage.USERNAME_EXISTS);
                    }
                    break;
                default:
                    throw new BusinessException("账号格式错误");
            }

            String encodePassword = passwordEncoder.encode(password);
            // 创建用户
            User user = new User();
            user.setUsername(account);
            user.setNickname(account);
            user.setPassword(encodePassword);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setRegisterTime(LocalDateTime.now());
            user.setRegisterIp(clientIp);
            user.setStatus(UserStatus.NORMAL);
            user.setPrivacy(VisibleStatus.PUBLIC);
            user.setDeleted(DeleteStatus.NOT_DELETED);
            user.setFollowCount(0);
            user.setFollowerCount(0);
            user.setArticleCount(0);
            user.setCommentCount(0);
            user.setLikeCount(0);
            user.setGender(Gender.UNKNOWN);
            user.setPhoneVerified(VerificationStatus.UNVERIFIED);
            user.setEmailVerified(VerificationStatus.UNVERIFIED);

            // 根据账号类型设置相应字段和验证状态
            switch (accountType) {
                case EMAIL:
                    user.setEmail(account);
                    user.setEmailVerified(VerificationStatus.VERIFIED);
                    break;
                case PHONE:
                    user.setPhone(account);
                    user.setPhoneVerified(VerificationStatus.VERIFIED);
                    break;
            }
            
            User newUser = userService.createUser(user);

            // 创建用户认证信息
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(newUser.getId());
            userAuth.setAuthType(AuthTypeConstant.USERNAME);
            userAuth.setAuthIdentifier(account); // 存储账号
            userAuth.setAuthCredential(encodePassword); // 存储加密后的密码
            userAuth.setEnabled(StatusEnum.ENABLED);
            userAuth.setPrimaryAuth(DefaultStatus.YES);
            userAuth.setCreateTime(LocalDateTime.now());
            userAuth.setUpdateTime(LocalDateTime.now());
            userAuth.setLastAuthTime(LocalDateTime.now());
            userAuth.setDeleted(DeleteStatus.NOT_DELETED);
            userAuthMapper.insert(userAuth);

            // 为用户分配角色
            userRoleService.createUserRole(newUser);

            // 设置注册频率限制
            redisUtil.set(limitKey, "1", REGISTER_RATE_LIMIT_SECONDS, TimeUnit.SECONDS);

            // 生成OAuth2令牌
            return tokenService.generateTokenForUser(newUser, authDTO);
        } catch (BusinessException e) {
            log.error("用户注册失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }
    }

    @Override
    public TokenResponse login(AuthDTO authDTO) {
        if (authDTO == null) {
            throw new BusinessException(ResponseMessage.LOGIN_FAILED);
        }

        try {
            // 检查登录频率限制
            String clientIp = IPUtil.getClientIp();
            String failKey = RedisKeyConstants.buildLoginFailKey(clientIp);
            Integer failCount = redisUtil.getWithType(failKey, new TypeReference<>() {});
            if (failCount != null && failCount >= MAX_LOGIN_FAIL_TIMES) {
                throw new BusinessException(ResponseMessage.LOGIN_TOO_FREQUENTLY);
            }

            String account = authDTO.getAccount();
            String password = authDTO.getPassword();
            String code = authDTO.getCode();
            AuthType authType = authDTO.getAuthType();

            User user = null;

            // 验证码登录
            if (AuthType.CODE == authType) {
                if (code == null || code.isEmpty()) {
                    throw new BusinessException(ResponseMessage.CAPTCHA_REQUIRED);
                }
                // 验证验证码
                boolean verifyResult = verifyCodeService.verifyCode(account, code, "login");
                if (!verifyResult) {
                    // 记录登录失败次数
                    recordLoginFail(clientIp, failKey, failCount);
                    throw new BusinessException(ResponseMessage.CAPTCHA_ERROR);
                }
                // 根据账号获取用户
                AccountType accountType = AccountUtil.getAccountType(account);
                user = switch (accountType) {
                    case EMAIL -> userService.getUserByEmail(account);
                    case PHONE -> userService.getUserByPhone(account);
                    case USERNAME -> userService.getUserByUsername(account);
                    default -> throw new BusinessException("账号格式错误");
                };
            } else if (AuthType.PASSWORD == authType) {
                if (password == null || password.isEmpty()) {
                    throw new BusinessException(ResponseMessage.PASSWORD_REQUIRED);
                }
                // 密码登录 - 使用 AuthenticationManager 进行认证
                try {
                    // 通过用户名和密码进行认证
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(account, password)
                    );
                    // 从认证结果中获取用户详情
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    // 根据用户名获取完整的用户信息
                    if (userDetails != null) {
                        user = userService.getUserByUsername(userDetails.getUsername());
                    }
                } catch (Exception e) {
                    // 记录登录失败次数
                    recordLoginFail(clientIp, failKey, failCount);
                    if (e instanceof UsernameNotFoundException) {
                        throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
                    } else {
                        throw new BusinessException(ResponseMessage.PASSWORD_ERROR);
                    }
                }
            } else {
                throw new BusinessException(ResponseMessage.AUTH_TYPE_ERROR);
            }

            // 检查用户是否存在
            if (user == null) {
                throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
            }

            // 检查用户状态
            if (UserStatus.NORMAL != user.getStatus()) {
                throw new BusinessException(ResponseMessage.USER_DISABLED);
            }

            // 清除登录失败记录
            redisUtil.delete(failKey);

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IPUtil.getClientIp());
            User updatedUser = userService.updateUser(user);

            // 生成OAuth2令牌
            return tokenService.generateTokenForUser(updatedUser, authDTO);
        } catch (BusinessException e) {
            log.error("用户登录失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户登录异常: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.LOGIN_FAILED);
        }
    }

    /**
     * 记录登录失败次数
     */
    private void recordLoginFail(String clientIp, String failKey, Integer failCount) {
        log.info("记录登录失败次数: clientIp={}, failKey={}, failCount={}", clientIp, failKey, failCount);
        if (failCount == null) {
            failCount = 0;
        }
        failCount++;
        redisUtil.set(failKey, failCount, LOGIN_FAIL_LIMIT_SECONDS, TimeUnit.SECONDS);
    }
}