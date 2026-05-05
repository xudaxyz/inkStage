package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章删除通知参数
 * <br/>
 * <p>
 * --- 通知展示格式 ---
 * ==================================================================================
 * ==        标题：文章删除通知                                                       ==
 * ==        内容：您发布的文章《{{articleTitle}}》因{{reason}}，已被管理员删除。          ==
 * ==        请遵守平台规则.如有疑问可联系客服申诉。                                      ==
 * ==================================================================================
 * <br/>
 * <p>
 * --- 参考示例 ---
 * ==================================================================================
 * ==        标题：文章重新审核通知                                                    ==
 * ==        内容：您的文章《网络协议分析》因不符合平台内容规范，已被管理员删除。               ==
 * ==        请遵守平台规则.如有疑问可联系客服申诉。                                      ==
 * ==================================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleDeleteParam extends NotificationParam {

    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 原因
     */
    private String reason;
}
