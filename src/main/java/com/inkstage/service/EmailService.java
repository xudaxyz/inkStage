package com.inkstage.service;

/**
 * 邮件服务
 */
public interface EmailService {

    /**
     * 发送通知邮件
     */
    boolean sendNotificationEmail(String to, String subject, String content);

    /**
     * 发送重要通知邮件
     */
    boolean sendImportantNotificationEmail(String to, String subject, String content);

    /**
     * 批量发送通知邮件
     */
    boolean sendBatchNotificationEmails(java.util.List<java.util.Map<String, Object>> emails);
}
