package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评论审核拒绝通知参数
 * <br/>
 *
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：评论审核未通过                                          ==
 * ==        内容：你的评论审核未通过                                       ==
 * ==        原因：${reason}                                             ==
 * ======================================================================
 * <br/>
 *
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：评论审核未通过                                          ==
 * ==        内容：你的评论审核未通过                                       ==
 * ==        原因：评论内容包含敏感词，请修改后重新提交                        ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentReviewRejectParam extends NotificationParam {

    /**
     * 拒绝原因
     */
    private String reason;
    /**
     * 文章ID
     */
    private Long articleId;
}
