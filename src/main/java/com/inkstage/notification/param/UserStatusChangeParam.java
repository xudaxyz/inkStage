package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户状态变更通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：账号状态变更                                            ==
 * ==        内容：您的账号状态已变更                                       ==
 * ==        原因：${reason}                                             ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：账号状态变更                                            ==
 * ==        内容：您的账号状态已变更                                       ==
 * ==        原因：账号已解封，请正常使用                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserStatusChangeParam extends NotificationParam {

    /**
     * 状态变更原因
     */
    private String reason;
}
