package com.inkstage.controller.front;

import com.inkstage.common.model.Result;
import com.inkstage.dto.OAuth2RegisterDTO;
import com.inkstage.service.UserAuthService;
import com.inkstage.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台用户认证Controller
 */
@RestController
@RequestMapping("/api/v1/front/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    /**
     * 用户注册
     *
     * @param oAuth2RegisterDTO OAuth2注册参数
     * @return 注册结果，包含令牌信息
     */
    @PostMapping("/register")
    public Result<TokenResponse> register(@RequestBody OAuth2RegisterDTO oAuth2RegisterDTO) {
        TokenResponse tokenResponse = userAuthService.register(oAuth2RegisterDTO);
        return Result.success(tokenResponse);
    }
}
