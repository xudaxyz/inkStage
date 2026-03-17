package com.inkstage.exception;

import com.inkstage.common.ResponseCode;
import com.inkstage.common.Result;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     *
     * @param e       认证异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(AuthException.class)
    public Result<?> handleAuthException(AuthException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = "认证失败: " + e.getMessage();
        log.error("[{}] AuthException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.UNAUTHORIZED, errorMessage);
    }

    /**
     * 处理自定义业务异常
     *
     * @param e       业务异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] BusinessException: {}, URI: {}", requestId, e.getMessage(), request.getRequestURI(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理缺少请求参数异常
     *
     * @param e       缺少请求参数异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String errorMessage = "缺少必要的请求参数: " + e.getParameterName();
        String requestId = generateRequestId();
        log.error("[{}] MissingServletRequestParameterException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理方法参数类型转换异常
     *
     * @param e       方法参数类型转换异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = "参数类型转换错误: " + e.getName() + " 应为 " + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型");
        log.error("[{}] MethodArgumentTypeMismatchException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理HTTP消息不可读异常(如JSON格式错误)
     *
     * @param e       HTTP消息不可读异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = "请求体格式错误, 无法解析: " + e.getMessage();
        log.error("[{}] HttpMessageNotReadableException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.BAD_REQUEST, errorMessage);
    }

    /**
     * 处理HTTP请求方法不支持异常
     *
     * @param e       HTTP请求方法不支持异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = "请求方法不支持: " + e.getMethod();
        log.error("[{}] HttpRequestMethodNotSupportedException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.METHOD_NOT_ALLOWED, errorMessage);
    }

    /**
     * 处理资源未找到异常
     *
     * @param e       资源未找到异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = "资源未找到: " + e.getMessage();
        log.error("[{}] NoSuchElementException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.NOT_FOUND, errorMessage);
    }

    /**
     * 处理请求参数验证失败异常
     *
     * @param e       请求参数验证失败异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        // 获取所有验证失败的字段和错误信息
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            // 将字段名转换为更友好的名称
            String friendlyFieldName = getFriendlyFieldName(fieldName);
            errorMessage.append(friendlyFieldName).append(": ").append(fieldError.getDefaultMessage()).append("；");
        });
        if (!errorMessage.isEmpty()) {
            errorMessage.setLength(errorMessage.length() - 1); // 移除最后的分号
        } else {
            errorMessage.append("请求参数验证失败");
        }
        log.error("[{}] MethodArgumentNotValidException: {}, URI: {}", requestId, errorMessage, request.getRequestURI(), e);
        return Result.error(ResponseCode.BAD_REQUEST, errorMessage.toString());
    }

    /**
     * 将字段名转换为更友好的名称
     *
     * @param fieldName 字段名
     * @return 友好的字段名称
     */
    private String getFriendlyFieldName(String fieldName) {
        // 字段名映射，将驼峰命名转换为中文名称
        return switch (fieldName) {
            case "title" -> "文章标题";
            case "content" -> "文章内容";
            case "summary" -> "文章摘要";
            case "categoryId" -> "文章分类";
            case "tagIds" -> "文章标签";
            case "coverImage" -> "封面图片";
            case "status" -> "文章状态";
            case "visible" -> "可见性";
            case "allowComment" -> "评论设置";
            case "allowForward" -> "转发设置";
            case "original" -> "文章类型";
            case "originalUrl" -> "转载链接";
            case "metaTitle" -> "SEO标题";
            case "metaDescription" -> "SEO描述";
            case "metaKeywords" -> "SEO关键词";
            case "scheduledPublishTime" -> "定时发布时间";
            default -> fieldName;
        };
    }

    /**
     * 处理运行时异常
     *
     * @param e       运行时异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = isProduction() ? "服务器内部错误" : "运行时异常: " + e.getMessage();
        log.error("[{}] RuntimeException: {}, URI: {}", requestId, e.getMessage(), request.getRequestURI(), e);
        return Result.error(ResponseCode.INTERNAL_SERVER_ERROR, errorMessage);
    }

    /**
     * 处理所有其他异常
     *
     * @param e       异常
     * @param request HTTP请求
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorMessage = isProduction() ? "服务器内部错误" : "系统异常: " + e.getMessage();
        log.error("[{}] Exception: {}, URI: {}", requestId, e.getMessage(), request.getRequestURI(), e);
        return Result.error(ResponseCode.INTERNAL_SERVER_ERROR, errorMessage);
    }

    /**
     * 生成请求ID, 后续用于追踪请求
     *
     * @return 请求ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 判断是否为生产环境
     *
     * @return 是否为生产环境
     */
    private boolean isProduction() {
        String env = System.getProperty("spring.profiles.active", "");
        return env.contains("prod") || env.contains("production");
    }
}