package com.inkstage.service;

import com.inkstage.dto.AuthDTO;
import com.inkstage.dto.ChangePasswordDTO;
import com.inkstage.dto.ResetPasswordDTO;
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

    /**
     * 管理员登录
     * @param authDTO 认证请求DTO
     * @return 登录结果, 包含令牌信息
     */
    TokenResponse adminLogin(AuthDTO authDTO);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     * @param refreshToken 刷新令牌
     */
    void logout(Long userId, String refreshToken);

    /**
     * 修改密码（已登录用户）
     * @param userId 用户ID
     * @param dto 修改密码请求DTO
     * @return 修改结果
     */
    boolean changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 重置密码（忘记密码，通过验证码验证身份）
     *
     * @param dto 重置密码请求DTO
     */
    void resetPassword(ResetPasswordDTO dto);

}