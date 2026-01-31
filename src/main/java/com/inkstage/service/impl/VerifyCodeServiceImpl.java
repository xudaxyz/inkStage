package com.inkstage.service.impl;

import com.inkstage.exception.BusinessException;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.AuthTypeConstant;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.dto.SendCodeDTO;
import com.inkstage.service.VerifyCodeService;
import com.inkstage.utils.EmailTemplateUtils;
import com.inkstage.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;
    private final EmailTemplateUtils emailTemplateUtils;

    // 从配置文件读取发件人地址
    @Value("${spring.mail.username}")
    private String fromEmail;

    // 验证码有效期(分钟)
    private static final int CODE_EXPIRY_MINUTES = 5;
    // 验证码长度
    private static final int CODE_LENGTH = 6;
    // 发送频率限制(秒)
    private static final int SEND_RATE_LIMIT_SECONDS = 60;
    // 邮件发送重试次数
    private static final int EMAIL_SEND_RETRY_COUNT = 3;
    // 邮件发送重试间隔(毫秒)
    private static final long EMAIL_SEND_RETRY_INTERVAL = 1000;
    // Redis key前缀

    @Override
    public boolean sendCode(SendCodeDTO sendCodeDTO) {
        if (sendCodeDTO == null || !StringUtils.hasText(sendCodeDTO.getAccount())) {
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }

        // 检查发送频率限制
        String limitKey = RedisKeyConstants.buildSendLimitKey(sendCodeDTO.getAccount(), sendCodeDTO.getPurpose());
        if (redisUtil.hasKey(limitKey)) {
            throw new BusinessException(ResponseMessage.VERIFY_CODE_SEND_TOO_FREQUENTLY);
        }

        // 生成验证码
        String code = generateCode(CODE_LENGTH);

        // 存储验证码到Redis
        String codeKey = RedisKeyConstants.buildVerifyCodeKey(sendCodeDTO.getAccount(), sendCodeDTO.getPurpose());
        redisUtil.set(codeKey, code, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // 设置发送频率限制
        redisUtil.set(limitKey, "1", SEND_RATE_LIMIT_SECONDS, TimeUnit.SECONDS);

        // 根据类型发送验证码
        if (AuthTypeConstant.EMAIL.equalsIgnoreCase(sendCodeDTO.getType())) {
            return sendEmailCode(sendCodeDTO.getAccount(), code, sendCodeDTO.getPurpose());
        } else if (AuthTypeConstant.PHONE.equalsIgnoreCase(sendCodeDTO.getType())) {
            return sendSmsCode(sendCodeDTO.getAccount(), code, sendCodeDTO.getPurpose());
        } else {
            throw new BusinessException(ResponseMessage.VERIFY_CODE_TYPE_ERROR);
        }
    }

    @Override
    public boolean verifyCode(String target, String code, String purpose) {
        if (!StringUtils.hasText(target) || !StringUtils.hasText(code) || !StringUtils.hasText(purpose)) {
            return false;
        }

        String codeKey = RedisKeyConstants.buildVerifyCodeKey(target, purpose);
        String storedCode = redisUtil.get(codeKey, String.class);

        if (code.equals(storedCode)) {
            // 验证成功后删除验证码, 防止重复使用
            redisUtil.delete(codeKey);
            return true;
        }

        return false;
    }

    @Override
    public String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 发送邮箱验证码(带重试机制)
     */
    private boolean sendEmailCode(String email, String code, String purpose) {
        for (int retryCount = 0; retryCount < EMAIL_SEND_RETRY_COUNT; retryCount++) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                String purposeStr = switch (purpose) {
                    case "register" -> "注册";
                    case "login" -> "登录";
                    case "forget" -> "忘记密码";
                    default -> "验证";
                };

                // 使用配置文件中的发件人地址
                helper.setFrom(fromEmail);
                helper.setTo(email);
                helper.setSubject("InkStage验证码");

                // 构建邮件模板变量
                Map<String, Object> variables = new HashMap<>();
                variables.put("purpose", purposeStr);
                variables.put("verifyCode", code);
                variables.put("expireTime", CODE_EXPIRY_MINUTES);
                variables.put("currentYear", LocalDate.now().getYear());

                // 使用邮件模板工具类渲染模板
                String htmlContent = emailTemplateUtils.renderTemplate("email/verify-code.html", variables);
                helper.setText(htmlContent, true);

                javaMailSender.send(message);
                log.info("邮箱验证码发送成功：{}", email);
                return true;
            } catch (MessagingException | IOException e) {
                log.error("邮箱验证码发送失败(第{}次)：{}, 错误信息：{}", retryCount + 1, email, e.getMessage(), e);

                // 不是最后一次重试, 进行重试
                if (retryCount < EMAIL_SEND_RETRY_COUNT - 1) {
                    // 重试间隔
                    try {
                        Thread.sleep(EMAIL_SEND_RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("邮件发送重试被中断：{}", ie.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("邮箱验证码发送失败：{}, 错误信息：{}", email, e.getMessage(), e);
                throw new BusinessException(ResponseMessage.VERIFY_CODE_SEND_FAILED);
            }
        }

        // 所有重试都失败
        log.error("邮箱验证码发送失败, 已达到最大重试次数：{}", email);
        throw new BusinessException(ResponseMessage.VERIFY_CODE_SEND_FAILED);
    }

    /**
     * 发送短信验证码
     */
    private boolean sendSmsCode(String phone, String code, String purpose) {
        try {
            // 这里应该集成短信服务提供商的API
            // 例如：阿里云短信服务、腾讯云短信服务等
            log.info("'{}'短信验证码发送成功：{}, 验证码：{}", purpose, phone, code);
            // 模拟发送成功
            return true;
        } catch (Exception e) {
            log.error("短信验证码发送失败：{}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.VERIFY_CODE_SEND_FAILED);
        }
    }
}
