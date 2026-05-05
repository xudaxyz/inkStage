package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专栏恢复通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：专栏恢复通知                                            ==
 * ==        内容：你的专栏《${columnName}》已恢复                          ==
 * ==        跳转链接：${articleUrl}                                     ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：专栏恢复通知                                           ==
 * ==        内容：你的专栏《技术干货》已恢复                                ==
 * ==        点击查看                                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ColumnRestoredParam extends NotificationParam {

    /**
     * 专栏ID
     */
    private Long columnId;
    /**
     * 专栏名称
     */
    private String columnName;
    /**
     * 操作链接
     */
    private String actionUrl;
}
