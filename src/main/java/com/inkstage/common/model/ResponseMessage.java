package com.inkstage.common.model;

import lombok.Getter;

/**
 * 用于统一管理API的响应消息
 */
@Getter
public enum ResponseMessage {

    SUCCESS("操作成功"),
    BAD_REQUEST("请求参数错误"),
    UNAUTHORIZED("未授权访问"),
    FORBIDDEN("权限不足，禁止访问"),
    NOT_FOUND("资源不存在"),
    INTERNAL_SERVER_ERROR("服务器内部错误"),
    BUSINESS_ERROR("业务异常"),
    ERROR("操作失败"),
    PARAM_ERROR("请求参数错误"),
    USERNAME_EXISTS("用户名已存在"),
    EMAIL_EXISTS("邮箱已存在"),
    PHONE_EXISTS("手机号已存在"),
    CAPTCHA_ERROR("验证码错误"),
    VERIFY_CODE_SEND_TOO_FREQUENTLY("验证码发送过于频繁，请稍后再试"),
    VERIFY_CODE_SEND_FAILED("验证码发送失败，请稍后重试"),
    VERIFY_CODE_TYPE_ERROR("验证码类型错误"),
    VERIFY_CODE_EXPIRED("验证码已过期"),
    REGISTER_TOO_FREQUENTLY("注册过于频繁，请稍后再试"),
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
    ARTICLE_PUBLISH_FAILED("文章发布失败"),
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
    CATEGORY_HAS_ARTICLES("分类下存在文章，不能删除"),
    TAG_NAME_EMPTY("标签名称不能为空"),
    TAG_SLUG_EMPTY("标签别名不能为空"),
    TAG_CREATE_FAILED("标签创建失败"),
    TAG_UPDATE_FAILED("标签更新失败"),
    TAG_NOT_FOUND("标签不存在"),
    TAG_HAS_ARTICLES("标签下存在文章，不能删除"),
    REGISTER_FAILED("用户注册失败！"),
    ACCOUNT_NOT_FOUND("账号不存在"),
    SEND_CODE_FAILED("验证码发送失败"),
    SEND_CODE_SUCCESS("验证码发送成功"),
    CAPTCHA_REQUIRED("验证码不能为空"),
    PASSWORD_REQUIRED("密码不能为空"),
    AUTH_TYPE_ERROR("认证类型错误"),
    AGREE_TERMS_REQUIRED("请同意服务条款"),
    LOGIN_FAILED("登录失败"),
    LOGIN_TOO_FREQUENTLY("登录过于频繁，请稍后再试"),
    USER_DISABLED("用户已被禁用"),
    REGISTER_SUCCESS("注册成功"),
    LOGIN_SUCCESS("登录成功"),
    UPDATE_SUCCESS("更新成功"),
    UPDATE_FAILED("更新失败"),
    UPLOAD_SUCCESS("上传成功"),
    UPLOAD_FAILED("上传失败"),
    FILE_URL_FAILED("获取文件URL失败");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    /**
     * 格式化消息模板
     * 支持两种占位符格式：
     * 1. 顺序占位符：{}，按参数顺序替换
     * 2. 索引占位符：{0}, {1}，按指定索引替换
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
            sb.replace(position, position + 2, argValue);
            argIndex++;
        }

        return sb.toString();
    }
}
