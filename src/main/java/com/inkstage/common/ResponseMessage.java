package com.inkstage.common;

import lombok.Getter;

/**
 * 用于统一管理API的响应消息
 */
@Getter
public enum ResponseMessage {

    SUCCESS("操作成功"),
    BAD_REQUEST("请求参数错误"),
    UNAUTHORIZED("未授权访问"),
    FORBIDDEN("权限不足, 禁止访问"),
    NOT_FOUND("资源不存在"),
    INTERNAL_SERVER_ERROR("服务器内部错误"),
    BUSINESS_ERROR("业务异常"),
    ERROR("操作失败"),
    PARAM_ERROR("请求参数错误"),
    USERNAME_EXISTS("用户名已存在"),
    EMAIL_EXISTS("邮箱已存在"),
    PHONE_EXISTS("手机号已存在"),
    CAPTCHA_ERROR("验证码错误"),
    VERIFY_CODE_SEND_TOO_FREQUENTLY("验证码发送过于频繁, 请稍后再试"),
    VERIFY_CODE_SEND_FAILED("验证码发送失败, 请稍后重试"),
    VERIFY_CODE_TYPE_ERROR("验证码类型错误"),
    VERIFY_CODE_EXPIRED("验证码已过期"),
    REGISTER_TOO_FREQUENTLY("注册过于频繁, 请稍后再试"),
    USER_NOT_FOUND("用户不存在"),
    USER_STATUS_ERROR("用户状态异常"),
    PASSWORD_ERROR("密码错误"),
    OLD_PASSWORD_ERROR("原密码错误"),
    ROLE_NOT_FOUND("角色不存在"),
    ROLE_CODE_EXISTS("角色代码已存在"),
    ROLE_NAME_EXISTS("角色名称已存在"),
    SYSTEM_ROLE_CANNOT_BE_DISABLED("系统角色不能被禁用"),
    SYSTEM_ROLE_CANNOT_BE_DELETED("系统角色不能被删除"),
    AUTH_IDENTIFIER_EXISTS("认证标识已存在"),
    AUTH_NOT_FOUND("认证信息不存在"),
    USERNAME_NOT_FOUND("用户名不存在"),
    USER_ROLE_EXISTS("用户角色关系已存在"),
    USER_ROLE_NOT_FOUND("用户角色关系不存在"),
    CANNOT_FOLLOW_SELF("不能关注自己"),
    ARTICLE_NOT_FOUND("文章不存在"),
    ARTICLE_TITLE_EMPTY("文章标题不能为空"),
    ARTICLE_CONTENT_EMPTY("文章内容不能为空"),
    ARTICLE_PUBLISH_FAILED("文章发布失败{}"),
    ARTICLE_DRAFT_FAILED("文章草稿保存失败{}"),
    ARTICLE_UPDATE_FAILED("文章更新失败"),
    ARTICLE_LIKE_FAILED("文章点赞失败"),
    ARTICLE_UNLIKE_FAILED("文章取消点赞失败"),
    ARTICLE_COLLECT_FAILED("文章收藏失败"),
    ARTICLE_UN_COLLECT_FAILED("文章取消收藏失败"),
    CATEGORY_NAME_EMPTY("分类名称不能为空"),
    CATEGORY_SLUG_EMPTY("分类别名不能为空"),
    CATEGORY_CREATE_FAILED("分类创建失败"),
    CATEGORY_UPDATE_FAILED("分类更新失败"),
    CATEGORY_NOT_FOUND("分类不存在"),
    CATEGORY_HAS_ARTICLES("分类下存在文章, 不能删除"),
    TAG_NAME_EMPTY("标签名称不能为空"),
    TAG_SLUG_EMPTY("标签别名不能为空"),
    TAG_CREATE_FAILED("标签创建失败"),
    TAG_UPDATE_FAILED("标签更新失败"),
    TAG_NOT_FOUND("标签不存在"),
    TAG_HAS_ARTICLES("标签下存在文章, 不能删除"),
    REGISTER_FAILED("用户注册失败！"),
    ACCOUNT_NOT_FOUND("账号不存在"),
    SEND_CODE_FAILED("验证码发送失败"),
    SEND_CODE_SUCCESS("验证码发送成功"),
    CAPTCHA_REQUIRED("验证码不能为空"),
    PASSWORD_REQUIRED("密码不能为空"),
    AUTH_TYPE_ERROR("认证类型错误"),
    AGREE_TERMS_REQUIRED("请同意服务条款"),
    LOGIN_FAILED("登录失败"),
    LOGIN_TOO_FREQUENTLY("登录过于频繁, 请稍后再试"),
    USER_DISABLED("用户已被禁用"),
    REGISTER_SUCCESS("注册成功"),
    LOGIN_SUCCESS("登录成功"),
    UPDATE_SUCCESS("更新成功"),
    UPDATE_FAILED("更新失败"),
    UPLOAD_SUCCESS("上传成功"),
    UPLOAD_FAILED("上传失败"),
    FILE_URL_FAILED("获取文件URL失败"),
    CATEGORIES_ARE_EMPTY("分类列表为空"),
    TAGS_ARE_EMPTY("标签列表为空"),
    TAG_COUNT_EXCEEDED("标签数量不能超过{}个"),
    TITLE_IS_EMPTY("文章标题不能为空"),
    CONTENT_IS_EMPTY("文章内容不能为空"),
    ARTICLE_DELETE_FAILED("创建文章失败{}"),
    ARTICLE_LIST_NOT_FOUND("文章列表不存在"),
    NO_COMMENTS("暂无评论"),
    COMMENT_CREATE_FAILED("创建评论失败"),
    COMMENT_UPDATE_FAILED("更新评论失败"),
    COMMENT_DELETE_FAILED("删除评论失败"),
    COMMENT_CONTENT_EMPTY("评论内容不能为空"),
    COMMENT_NOT_FOUND("评论不存在"),
    UPDATED_FORBIDDEN("无更新权限"),
    DELETED_FORBIDDEN("无删除权限"),
    USER_NOT_LOGGED_IN("用户未登录"),
    NOT_LOGIN("用户未登录"),
    NO_PERMISSION("权限不足"),
    RESOURCE_NOT_FOUND("资源不存在"),
    RESOURCE_EXISTS("资源已存在"),
    COLLECTION_DEFAULT_FOLDER_EXIST("默认收藏夹已存在"),
    COLLECTION_FOLDER_EXIST("收藏夹{}已存在"),
    
    // 文章相关成功消息
    ARTICLE_LIST_SUCCESS("获取文章列表成功"),
    ARTICLE_DETAIL_SUCCESS("获取文章详情成功"),
    ARTICLE_CREATE_SUCCESS("创建文章成功"),
    ARTICLE_UPDATE_SUCCESS("更新文章成功"),
    ARTICLE_DELETE_SUCCESS("删除文章成功"),
    ARTICLE_STATUS_UPDATE_SUCCESS("更新文章状态成功"),
    ARTICLE_DRAFT_SUCCESS("草稿保存成功"),
    ARTICLE_LIKE_SUCCESS("点赞成功"),
    ARTICLE_UNLIKE_SUCCESS("取消点赞成功"),
    ARTICLE_READ_COUNT_INCREMENT_SUCCESS("增加文章阅读数成功"),
    
    // 分类相关成功消息
    CATEGORY_LIST_SUCCESS("获取分类列表成功"),
    CATEGORY_DETAIL_SUCCESS("获取分类详情成功"),
    CATEGORY_CREATE_SUCCESS("添加分类成功"),
    CATEGORY_UPDATE_SUCCESS("更新分类成功"),
    CATEGORY_DELETE_SUCCESS("删除分类成功"),
    CATEGORY_STATUS_UPDATE_SUCCESS("更新分类状态成功"),
    
    // 标签相关成功消息
    TAG_LIST_SUCCESS("获取标签列表成功"),
    TAG_DETAIL_SUCCESS("获取标签详情成功"),
    TAG_CREATE_SUCCESS("添加标签成功"),
    TAG_UPDATE_SUCCESS("更新标签成功"),
    TAG_DELETE_SUCCESS("删除标签成功"),
    TAG_STATUS_UPDATE_SUCCESS("更新标签状态成功"),
    
    // 评论相关成功消息
    COMMENT_LIST_SUCCESS("获取评论列表成功"),
    COMMENT_CREATE_SUCCESS("创建评论成功"),
    COMMENT_UPDATE_SUCCESS("更新评论成功"),
    COMMENT_DELETE_SUCCESS("删除评论成功"),
    COMMENT_STATUS_UPDATE_SUCCESS("更新评论状态成功"),
    COMMENT_TOP_UPDATE_SUCCESS("更新评论置顶状态成功"),
    
    // 用户相关成功消息
    USER_LIST_SUCCESS("获取用户列表成功"),
    USER_DETAIL_SUCCESS("获取用户详情成功"),
    USER_DELETE_SUCCESS("删除用户成功"),
    USER_UPDATE_SUCCESS("更新用户成功"),
    USER_STATUS_UPDATE_SUCCESS("更新用户状态成功"),
    USER_ROLE_UPDATE_SUCCESS("更新用户角色成功"),
    USER_PROFILE_UPDATE_SUCCESS("更新用户个人资料成功"),
    
    // 收藏相关成功消息
    ARTICLE_COLLECT_SUCCESS("收藏成功"),
    ARTICLE_UN_COLLECT_SUCCESS("取消收藏成功"),
    COLLECTION_FOLDER_CREATE_SUCCESS("创建收藏文件夹成功"),
    COLLECTION_FOLDER_UPDATE_SUCCESS("更新收藏文件夹成功"),
    COLLECTION_FOLDER_DELETE_SUCCESS("删除收藏文件夹成功"),
    COLLECTION_ARTICLE_MOVE_SUCCESS("移动收藏文章成功"),
    
    // 文件相关成功消息
    FILE_DELETE_SUCCESS("文件删除成功"),
    ARTICLE_TAGS_MAX_TEN("文章标签不能超过10个"),
    
    // 令牌相关消息
    REFRESH_TOKEN_SUCCESS("令牌刷新成功"),
    REFRESH_TOKEN_FAILED("令牌刷新失败"),
    
    // 登出相关消息
    LOGOUT_SUCCESS("登出成功"),
    LOGOUT_FAILED("登出失败"),
    ARTICLE_DELETE_ERROR("删除文章失败"),
    ARTICLE_CREATE_FAILED("文章创建失败");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    /**
     * 格式化消息模板
     * 支持两种占位符格式：
     * 1. 顺序占位符：{}, 按参数顺序替换
     * 2. 索引占位符：{0}, {1}, 按指定索引替换
     */
    public String format(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }

        String result = message;

        // 先处理索引占位符 {0}, {1}, ...
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String argValue = args[i] != null ? args[i].toString() : "null";
            result = result.replace(placeholder, argValue);
        }

        // 再处理顺序占位符 {}
        StringBuilder sb = new StringBuilder(result);
        int argIndex = 0;
        int position;

        while (argIndex < args.length && (position = sb.indexOf("{}")) != -1) {
            String argValue = args[argIndex] != null ? args[argIndex].toString() : "null";
            argValue = " " + argValue; // 添加空格以保持格式一致
            sb.replace(position, position + 2, argValue);
            argIndex++;
        }

        return sb.toString();
    }
}
