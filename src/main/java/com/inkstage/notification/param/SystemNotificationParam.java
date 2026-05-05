package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：系统通知                                               ==
 * ==        内容：${messageContent}                                     ==
 * ==        时间：${systemTime}                                         ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：系统通知                                               ==
 * ==        内容：平台将于今晚22:00-24:00进行系统升级，请提前保存草稿          ==
 * ==        时间：2026-05-04 10:00:00                                  ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SystemNotificationParam extends NotificationParam {

    /**
     * 消息内容
     */
    private String messageContent;
    /**
     * 系统时间
     */
    private String systemTime;
}
