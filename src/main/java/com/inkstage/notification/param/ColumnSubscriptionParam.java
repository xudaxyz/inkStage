package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专栏订阅通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：专栏更新通知                                             ==
 * ==        内容：你订阅的专栏《${columnName}》发布了新文章《${articleTitle}》 ==
 * ==        跳转链接：${articleUrl}                                      ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：专栏更新通知                                            ==
 * ==        内容：你订阅的专栏《架构师成长之路》发布了新文章《分布式系统设计》     ==
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
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文章链接
     */
    private String articleUrl;
}
