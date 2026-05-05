package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 举报通知参数（后台通知管理员）
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：内容举报                                              ==
 * ==        内容：有用户举报了内容                                        ==
 * ==               ${reportedContent}                                 ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：内容举报                                              ==
 * ==        内容：有用户举报了内容                                        ==
 * ==               这篇文章疑似抄袭，请核实处理                            ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReportParam extends NotificationParam {

    /**
     * 被举报内容
     */
    private String reportedContent;
    /**
     * 关联ID
     */
    private Long relatedId;
}
