package com.inkstage.service;

import com.inkstage.dto.OAuth2RegisterDTO;
import com.inkstage.vo.TokenResponse;

/**
 * 用户认证Service接口
 */
public interface UserAuthService {

    /**
     * 用户注册
     *
     * @param oAuth2RegisterDTO OAuth2注册参数
     * @return 注册结果，包含令牌信息
     */
    TokenResponse register(OAuth2RegisterDTO oAuth2RegisterDTO);

}