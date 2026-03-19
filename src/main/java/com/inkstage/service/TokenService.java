package com.inkstage.service;

import com.inkstage.dto.AuthDTO;
import com.inkstage.entity.model.User;
import com.inkstage.vo.TokenResponse;

/**
 * OAuth2令牌服务接口
 */
public interface TokenService {

    /**
     * 为用户生成令牌(支持登录和注册)
     *
     * @param user     用户
     * @param authDTO  认证请求DTO
     * @return 包含访问令牌和刷新令牌的响应
     */
    TokenResponse generateTokenForUser(User user, AuthDTO authDTO);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenResponse refreshToken(String refreshToken);
}