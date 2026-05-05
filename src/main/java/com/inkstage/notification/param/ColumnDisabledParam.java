package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专栏禁用通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：专栏禁用通知                                            ==
 * ==        内容：你的专栏《${columnName}》已被禁用                         ==
 * ==        原因：${reason}                                             ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：专栏禁用通知                                            ==
 * ==        内容：你的专栏《技术干货》已被禁用                               ==
 * ==        原因：内容存在违规信息，请整改后申请恢复                          ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ColumnDisabledParam extends NotificationParam {

    /**
     * 专栏ID
     */
    private Long columnId;
    /**
     * 专栏名称
     */
    private String columnName;
    /**
     * 下线原因
     */
    private String reason;
}
