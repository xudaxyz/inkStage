package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章重新审核通知参数
 * <br/>
 * <p>
 * --- 通知展示格式 ---
 * ==================================================================================
 * ==        标题：文章重新审核通知                                                    ==
 * ==        内容：您的文章《${articleTitle}》正在重新审核中，请耐心等待审核结果             ==
 * ==================================================================================
 * <br/>
 * <p>
 * --- 参考示例 ---
 * ==================================================================================
 * ==        标题：文章重新审核通知                                                    ==
 * ==        内容：您的文章《网络协议分析》正在重新审核中，请耐心等待审核结果                  ==
 * ==================================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleReviewReprocessParam extends NotificationParam {

    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章ID
     */
    private Long articleId;
}
