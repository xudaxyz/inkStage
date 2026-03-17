package com.inkstage.service.impl;

import com.inkstage.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 邮件服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public boolean sendNotificationEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            javaMailSender.send(message);
            log.info("通知邮件发送成功，收件人: {}", to);
            return true;
        } catch (Exception e) {
            log.error("通知邮件发送失败，收件人: {}, 错误消息: {}", to, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendImportantNotificationEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("【重要】" + subject);
            message.setText("这是一封重要通知邮件\n\n" + content);
            
            javaMailSender.send(message);
            log.info("重要通知邮件发送成功，收件人: {}", to);
            return true;
        } catch (Exception e) {
            log.error("重要通知邮件发送失败，收件人: {}, 错误消息: {}", to, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendBatchNotificationEmails(List<Map<String, Object>> emails) {
        try {
            int successCount = 0;
            for (Map<String, Object> email : emails) {
                String to = (String) email.get("to");
                String subject = (String) email.get("subject");
                String content = (String) email.get("content");
                boolean isImportant = (boolean) email.getOrDefault("isImportant", false);
                
                if (isImportant) {
                    if (sendImportantNotificationEmail(to, subject, content)) {
                        successCount++;
                    }
                } else {
                    if (sendNotificationEmail(to, subject, content)) {
                        successCount++;
                    }
                }
            }
            
            log.info("批量邮件发送完成，成功: {}, 总数量: {}", successCount, emails.size());
            return successCount > 0;
        } catch (Exception e) {
            log.error("批量邮件发送失败，错误消息: {}", e.getMessage());
            return false;
        }
    }
}
