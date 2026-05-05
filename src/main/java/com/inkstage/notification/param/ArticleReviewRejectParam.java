package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章审核拒绝通知参数
 * <br/>
 *
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：审核未通过通知                                          ==
 * ==        内容：你的文章《${articleTitle}》审核未通过                    ==
 * ==        原因：${reason}                                            ==
 * ======================================================================
 * <br/>
 *
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：审核未通过通知                                          ==
 * ==        内容：你的文章《网络协议分析》审核未通过                          ==
 * ==        原因：内容包含广告信息，请修改后重新提交                          ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleReviewRejectParam extends NotificationParam {

    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 拒绝原因
     */
    private String reason;
    /**
     * 文章ID
     */
    private Long articleId;
}
