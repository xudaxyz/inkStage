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
        };
    }

    @Override
    public String generateActionUrl(NotificationType type, Long relatedId) {
        if (relatedId == null) {
            return "/";
        }
        
        return switch (type) {
            case ARTICLE_PUBLISH, ARTICLE_LIKE, ARTICLE_COLLECTION, ARTICLE_COMMENT -> "/article/" + relatedId;
            case COMMENT_REPLY, COMMENT_LIKE -> "/article/" + relatedId; // 评论链接需要包含文章ID
            case FOLLOW -> "/user/" + relatedId;
            case MESSAGE -> "/profile/messages";
            case REPORT, FEEDBACK, SYSTEM -> "/";
        };
    }
}
