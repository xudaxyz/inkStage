package com.inkstage.service;

import com.inkstage.dto.OAuth2RegisterDTO;
import com.inkstage.entity.model.User;
import com.inkstage.vo.TokenResponse;

/**
 * OAuth2令牌服务接口
 */
public interface TokenService {

    /**
     * 为新注册用户生成令牌
     *
     * @param user              新注册的用户
     * @param oAuth2RegisterDTO 注册请求参数
     * @return 包含访问令牌和刷新令牌的响应
     */
    TokenResponse generateTokenForUser(User user, OAuth2RegisterDTO oAuth2RegisterDTO);
}