package com.inkstage.controller;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.AuthDTO;
import com.inkstage.dto.SendCodeDTO;
import com.inkstage.service.TokenService;
import com.inkstage.service.UserAuthService;
import com.inkstage.service.VerifyCodeService;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 前台用户认证Controller
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;
    private final VerifyCodeService verifyCodeService;
    private final TokenService tokenService;

    /**
     * 用户注册
     *
     * @param authDTO 认证请求DTO
     * @return 注册结果, 包含令牌信息
     */
    @PostMapping("/register")
    public Result<TokenResponse> register(@RequestBody @Valid AuthDTO authDTO) {
        TokenResponse tokenResponse = userAuthService.register(authDTO);
        if (tokenResponse != null) {
            return Result.success(tokenResponse, ResponseMessage.REGISTER_SUCCESS);
        } else {
            return Result.error(ResponseMessage.REGISTER_FAILED);
        }
    }

    /**
     * 用户登录
     *
     * @param authDTO 认证请求DTO
     * @return 登录结果, 包含令牌信息
     */
    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestBody @Valid AuthDTO authDTO) {
        log.info("用户登录: {}", authDTO);
        TokenResponse tokenResponse = userAuthService.login(authDTO);
        if (tokenResponse != null) {
            return Result.success(tokenResponse, ResponseMessage.LOGIN_SUCCESS);
        } else {
            return Result.error(ResponseMessage.LOGIN_FAILED);
        }
    }


    /**
     * 用户登录
     *
     * @param authDTO 认证请求DTO
     * @return 登录结果, 包含令牌信息
     */
    @PostMapping("/admin/login")
    public Result<TokenResponse> adminLogin(@RequestBody @Valid AuthDTO authDTO) {
        log.info("管理员登录: {}", authDTO);
        TokenResponse tokenResponse = userAuthService.adminLogin(authDTO);
        if (tokenResponse != null) {
            return Result.success(tokenResponse, ResponseMessage.ADMIN_LOGIN_SUCCESS);
        } else {
            return Result.error(ResponseMessage.ADMIN_LOGIN_FAILED);
        }
    }


    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码请求DTO
     * @return 是否发送成功
     */
    @PostMapping("/send-code")
    public Result<?> sendCode(@RequestBody @Valid SendCodeDTO sendCodeDTO) {
        log.info("sendCode to: {}", sendCodeDTO.getAccount());
        boolean success = verifyCodeService.sendCode(sendCodeDTO);
        if (success) {
            return Result.success(ResponseMessage.SEND_CODE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.SEND_CODE_FAILED);
        }
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌请求
     * @return 新的令牌信息
     */
    @PostMapping("/refresh-token")
    public Result<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        log.info("刷新token: {}", refreshToken);
        try {
            TokenResponse tokenResponse = tokenService.refreshToken(refreshToken);
            if (tokenResponse != null) {
                return Result.success(tokenResponse, ResponseMessage.REFRESH_TOKEN_SUCCESS);
            } else {
                return Result.error(ResponseMessage.REFRESH_TOKEN_FAILED);
            }
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return Result.error(ResponseMessage.REFRESH_TOKEN_FAILED);
        }
    }

    /**
     * 用户登出
     *
     * @param userId       用户ID
     * @param refreshToken 刷新令牌
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<?> logout(@RequestParam Long userId, @RequestParam(required = false) String refreshToken) {
        log.info("用户登出: {}", userId);
        try {
            userAuthService.logout(userId, refreshToken);
            return Result.success(ResponseMessage.LOGOUT_SUCCESS);
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return Result.error(ResponseMessage.LOGOUT_FAILED);
        }
    }

}
