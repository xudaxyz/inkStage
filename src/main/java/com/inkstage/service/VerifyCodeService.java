package com.inkstage.service;

import com.inkstage.dto.SendCodeDTO;

/**
 * 验证码服务接口
 */
public interface VerifyCodeService {

    /**
     * 发送验证码
     *
     * @param sendCodeDTO 发送验证码请求DTO
     * @return 是否发送成功
     */
    boolean sendCode(SendCodeDTO sendCodeDTO);

    /**
     * 验证验证码
     *
     * @param account 目标邮箱或手机号
     * @param code 验证码
     * @param purpose 验证码用途
     * @return 是否验证成功
     */
    boolean verifyCode(String account, String code, String purpose);

    /**
     * 生成验证码
     *
     * @param length 验证码长度
     * @return 生成的验证码
     */
    String generateCode(int length);
}
