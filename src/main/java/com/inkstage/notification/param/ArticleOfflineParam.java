package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章下线通知参数
 * <br/>
 *
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：文章下线通知                                            ==
 * ==        内容：你的文章《${articleTitle}》已下线                         ==
 * ==        原因：${reason}                                            ==
 * ======================================================================
 * <br/>
 *
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：文章下线通知                                            ==
 * ==        内容：你的文章《技术分享》已下线                                ==
 * ==        原因：包含敏感内容，请修改后重新提交                            ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleOfflineParam extends NotificationParam {

    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 下线原因
     */
    private String reason;
    /**
     * 文章ID
     */
    private Long articleId;
}
