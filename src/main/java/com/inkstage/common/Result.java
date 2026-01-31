package com.inkstage.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 统一返回结果集
 *
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 响应代码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 响应时间戳 - Unix时间戳(毫秒)
     */
    private Long timestamp;

    private Result() {
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(ResponseCode responseCode, ResponseMessage responseMessage, T data) {
        this.code = responseCode.getCode();
        this.message = responseMessage.getMessage();
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(ResponseCode responseCode, ResponseMessage responseMessage) {
        this.code = responseCode.getCode();
        this.message = responseMessage.getMessage();
        this.data = null;
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(ResponseCode responseCode, String message) {
        this.code = responseCode.getCode();
        this.message = message;
        this.data = null;
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(ResponseCode responseCode, String message, T data) {
        this.code = responseCode.getCode();
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }


    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, data);
    }

    public static <T> Result<T> success(ResponseMessage responseMessage) {
        return new Result<>(ResponseCode.SUCCESS, responseMessage, null);
    }

    public static <T> Result<T> success(T data, ResponseMessage message) {
        return new Result<>(ResponseCode.SUCCESS, message, data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResponseCode.SUCCESS, message, data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(ResponseCode.SUCCESS, message, null);
    }

    public static <T> Result<T> success(ResponseMessage responseMessage, String message) {
        String formatedMsg = responseMessage.format(responseMessage.getMessage(), message);
        return new Result<>(ResponseCode.SUCCESS, formatedMsg, null);
    }

    public static <T> Result<T> success(T data, ResponseMessage responseMessage, String message) {
        String formatedMsg = responseMessage.format(responseMessage.getMessage(), message);
        return new Result<>(ResponseCode.SUCCESS, formatedMsg, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResponseCode.BUSINESS_ERROR, message, null);
    }

    public static <T> Result<T> error(ResponseMessage responseMessage) {
        return new Result<>(ResponseCode.BUSINESS_ERROR, responseMessage, null);
    }

    public static <T> Result<T> error(ResponseCode responseCode, ResponseMessage responseMessage) {
        return new Result<>(responseCode.getCode(), responseMessage.getMessage(), null);
    }

    public static <T> Result<T> error(ResponseCode responseCode, String message) {
        return new Result<>(responseCode.getCode(), message, null);
    }

    public static <T> Result<T> error(ResponseMessage responseMessage, T data) {
        return new Result<>(ResponseCode.BUSINESS_ERROR, responseMessage, data);
    }

    public static <T> Result<T> error(ResponseMessage responseMessage, String message) {
        String formatedMsg = responseMessage.format(responseMessage.getMessage(), message);
        return new Result<>(ResponseCode.BUSINESS_ERROR, formatedMsg, null);
    }

    public static <T> Result<T> error(ResponseMessage responseMessage, String message, T data) {
        String formatedMsg = responseMessage.format(responseMessage.getMessage(), message);
        return new Result<>(ResponseCode.BUSINESS_ERROR, formatedMsg, data);
    }


    /**
     * 检查是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 检查是否为客户端错误
     *
     * @return 是否为客户端错误
     */
    public boolean isClientError() {
        return this.code >= 400 && this.code < 500;
    }

    /**
     * 检查是否为服务器错误
     *
     * @return 是否为服务器错误
     */
    public boolean isServerError() {
        return this.code >= 500;
    }


}