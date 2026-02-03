package com.inkstage.vo.front;

import com.inkstage.enums.article.TopStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO
 */
@Data
public class ArticleCommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 父评论ID(用于回复功能, 顶级评论为0)
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 评论状态
     */
    private Integer status;

    /**
     * 是否置顶(0:否,1:是)
     */
    private TopStatus top;

    /**
     * 置顶顺序(数值越大, 优先级越高)
     */
    private Integer topOrder;

    /**
     * @ 提及的用户ID列表
     */
    private String mentionUserIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 点踩数
     */
    private Integer dislikeCount;

    /**
     * 是否已点赞
     */
    private Boolean isLiked;

    /**
     * 是否已点踩
     */
    private Boolean isDisliked;

    /**
     * 回复评论列表
     */
    private List<ArticleCommentVO> replies;
}
