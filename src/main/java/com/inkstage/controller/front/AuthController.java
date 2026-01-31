package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.AuthDTO;
import com.inkstage.dto.SendCodeDTO;
import com.inkstage.service.UserAuthService;
import com.inkstage.service.VerifyCodeService;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台用户认证Controller
 */
@Slf4j
@RestController
@RequestMapping("/front/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;
    private final VerifyCodeService verifyCodeService;

    /**
     * 用户注册
     *
     * @param authDTO 认证请求DTO
     * @return 注册结果, 包含令牌信息
     */
    @PostMapping("/register")
    public Result<TokenResponse> register(@RequestBody AuthDTO authDTO) {
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
    public Result<TokenResponse> login(@RequestBody AuthDTO authDTO) {
        TokenResponse tokenResponse = userAuthService.login(authDTO);
        if (tokenResponse != null) {
            return Result.success(tokenResponse, ResponseMessage.LOGIN_SUCCESS);
        } else {
            return Result.error(ResponseMessage.LOGIN_FAILED);
        }
    }

    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码请求DTO
     * @return 是否发送成功
     */
    @PostMapping("/send-code")
    public Result<?> sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        log.info("sendCode to: {}", sendCodeDTO.getAccount());
        boolean success = verifyCodeService.sendCode(sendCodeDTO);
        if (success) {
            return Result.success(ResponseMessage.SEND_CODE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.SEND_CODE_FAILED);
        }
    }

}
