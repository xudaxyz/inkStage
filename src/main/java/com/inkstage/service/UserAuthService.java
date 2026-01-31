package com.inkstage.service;

import com.inkstage.dto.AuthDTO;
import com.inkstage.vo.TokenResponse;

/**
 * 用户认证Service接口
 */
public interface UserAuthService {

    /**
     * 用户注册
     *
     * @param authDTO 认证请求DTO
     * @return 注册结果, 包含令牌信息
     */
    TokenResponse register(AuthDTO authDTO);

    /**
     * 用户登录
     *
     * @param authDTO 认证请求DTO
     * @return 登录结果, 包含令牌信息
     */
    TokenResponse login(AuthDTO authDTO);

}