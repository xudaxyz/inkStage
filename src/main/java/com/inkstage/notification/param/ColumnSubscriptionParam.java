package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专栏订阅通知参数
 * <br/>
 * <p>
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：专栏订阅通知                                            ==
 * ==        内容：${subscriberName}订阅了您的专栏《${columnName}》         ==
 * ==        跳转链接：${articleUrl}                                      ==
 * ======================================================================
 * <br/>
 * <p>
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：专栏订阅通知                                            ==
 * ==        内容：张三 订阅了您的专栏《架构师成长之路》                       ==
 * ==        点击查看                                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ColumnSubscriptionParam extends NotificationParam {

    /**
     * 专栏ID
     */
    private Long columnId;

    /**
     * 专栏名称
     */
    private String columnName;

    /**
     * 订阅者ID
     */
    private Long subscriberId;

    /**
     * 订阅者昵称
     */
    private String subscriberName;

    /**
     * 专栏跳转链接
     */
    private String actionUrl;
}
