package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 举报结果通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：举报结果通知                                            ==
 * ==        内容：您举报的内容已有处理结果：${handleResult}                 ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：举报结果通知                                            ==
 * ==        内容：您举报的内容已有处理结果：经核实，被举报内容已删除             ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReportResultParam extends NotificationParam {

    /**
     * 处理结果
     */
    private String handleResult;
    /**
     * 关联ID
     */
    private Long relatedId;
}
