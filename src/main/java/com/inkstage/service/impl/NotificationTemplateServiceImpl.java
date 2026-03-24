package com.inkstage.service.impl;

import com.inkstage.enums.NotificationType;
import com.inkstage.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 通知模板服务实现类
 */
@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    @Override
    public String generateTitle(NotificationType type, Object... params) {
        return switch (type) {
            case ARTICLE_PUBLISH -> "文章发布成功";  
            case ARTICLE_LIKE -> "文章获得点赞";  
            case ARTICLE_COLLECTION -> "文章被收藏";  
            case ARTICLE_COMMENT -> "文章收到评论";  
            case COMMENT_REPLY -> "评论收到回复";  
            case COMMENT_LIKE -> "评论获得点赞";  
            case FOLLOW -> "有人关注了你";  
            case MESSAGE -> "收到新私信";  
            case REPORT -> "举报处理结果";  
            case FEEDBACK -> "反馈处理结果";  
            case SYSTEM -> "系统通知";  
            case USER_STATUS_CHANGE -> "账号状态变更";  
            case ARTICLE_REVIEW_REJECT -> "文章审核拒绝";  
            case ARTICLE_REVIEW_REPROCESS -> "文章审核结果";  
            case ARTICLE_OFFLINE -> "文章已下架";  
            case ARTICLE_ONLINE -> "文章已上架";  
            case ARTICLE_TOP -> "文章已置顶";  
            case ARTICLE_RECOMMEND -> "文章已推荐";  
            case ARTICLE_DELETE -> "文章已删除";  
            case TAG_DELETE -> "标签已删除";  
            case COMMENT_REVIEW_REJECT -> "评论审核拒绝";  
            case COMMENT_TOP -> "评论已置顶";  
        };
    }

    @Override
    public String generateContent(NotificationType type, Object... params) {
        return switch (type) {
            case ARTICLE_PUBLISH -> String.format("您的文章《%s》已成功发布", params.length > 0 ? params[0] : "");
            case ARTICLE_LIKE -> String.format("用户 %s 点赞了您的文章《%s》", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case ARTICLE_COLLECTION -> String.format("用户 %s 收藏了您的文章《%s》", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case ARTICLE_COMMENT -> String.format("用户 %s 评论了您的文章《%s》: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "", 
                    params.length > 2 ? params[2] : "");
            case COMMENT_REPLY -> String.format("用户 %s 回复了您的评论: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case COMMENT_LIKE -> String.format("用户 %s 点赞了您的评论", 
                    params.length > 0 ? params[0] : "");
            case FOLLOW -> String.format("用户 %s 关注了您", 
                    params.length > 0 ? params[0] : "");
            case MESSAGE -> String.format("用户 %s 给您发送了一条私信", 
                    params.length > 0 ? params[0] : "");
            case REPORT -> String.format("您的举报已处理，结果: %s", 
                    params.length > 0 ? params[0] : "");
            case FEEDBACK -> String.format("您的反馈已处理，结果: %s", 
                    params.length > 0 ? params[0] : "");
            case SYSTEM -> params.length > 0 ? params[0].toString() : "系统通知";
            case USER_STATUS_CHANGE -> String.format("您的账号状态已变更为: %s，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case ARTICLE_REVIEW_REJECT -> String.format("您的文章《%s》审核未通过，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case ARTICLE_REVIEW_REPROCESS -> String.format("您的文章《%s》审核结果: %s，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "", params.length > 2 ? params[2] : "");
            case ARTICLE_OFFLINE -> String.format("您的文章《%s》已被下架，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case ARTICLE_ONLINE -> String.format("您的文章《%s》已重新上架", 
                    params.length > 0 ? params[0] : "");
            case ARTICLE_TOP -> String.format("您的文章《%s》已被置顶", 
                    params.length > 0 ? params[0] : "");
            case ARTICLE_RECOMMEND -> String.format("您的文章《%s》已被推荐", 
                    params.length > 0 ? params[0] : "");
            case ARTICLE_DELETE -> String.format("您的文章《%s》已被删除，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case TAG_DELETE -> String.format("%s，原因: %s", 
                    params.length > 0 ? params[0] : "", params.length > 1 ? params[1] : "");
            case COMMENT_REVIEW_REJECT -> String.format("您的评论已被拒绝，原因: %s", 
                    params.length > 0 ? params[0] : "");
            case COMMENT_TOP -> String.format("您的评论已被置顶", 
                    params.length > 0 ? params[0] : "");
        };
    }

    @Override
    public String generateActionUrl(NotificationType type, Long relatedId) {
        if (relatedId == null) {
            return "/";
        }
        
        return switch (type) {
            case ARTICLE_PUBLISH, ARTICLE_LIKE, ARTICLE_COLLECTION, ARTICLE_COMMENT, 
                 ARTICLE_REVIEW_REJECT, ARTICLE_REVIEW_REPROCESS, ARTICLE_OFFLINE, 
                 ARTICLE_ONLINE, ARTICLE_TOP, ARTICLE_RECOMMEND, ARTICLE_DELETE -> "/article/" + relatedId;
            case COMMENT_REPLY, COMMENT_LIKE, COMMENT_REVIEW_REJECT, COMMENT_TOP -> "/article/" + relatedId; // 评论链接需要包含文章ID
            case FOLLOW -> "/user/" + relatedId;
            case MESSAGE -> "/profile/messages";
            case USER_STATUS_CHANGE -> "/profile/settings";
            case TAG_DELETE -> "/tags";
            case REPORT, FEEDBACK, SYSTEM -> "/";
        };
    }
}
