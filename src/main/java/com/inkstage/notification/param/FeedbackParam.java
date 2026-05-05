package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 反馈结果通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：反馈结果通知                                            ==
 * ==        内容：您的反馈已有处理结果：${handleResult}                     ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：反馈结果通知                                            ==
 * ==        内容：您的反馈已有处理结果：问题已修复，感谢您的反馈                 ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FeedbackParam extends NotificationParam {

    /**
     * 处理结果
     */
    private String handleResult;
}
