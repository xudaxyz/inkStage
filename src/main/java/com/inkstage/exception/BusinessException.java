package com.inkstage.exception;

import com.inkstage.common.ResponseCode;
import com.inkstage.common.ResponseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 响应状态码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    /**
     * 创建业务异常
     *
     * @param responseMessage 异常信息提示
     */
    public BusinessException(ResponseMessage responseMessage) {
        super(responseMessage.getMessage());
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = responseMessage.getMessage();
    }

    /**
     * 创建业务异常
     *
     * @param responseMessage 异常信息提示
     * @param message         异常信息补充
     */
    public BusinessException(ResponseMessage responseMessage, String message) {
        super(responseMessage.format(responseMessage.getMessage(), message));
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = responseMessage.format(responseMessage.getMessage(), message);
    }

    /**
     * 创建业务异常
     *
     * @param responseCode    异常状态码
     * @param responseMessage 异常信息提示
     */
    public BusinessException(ResponseCode responseCode, ResponseMessage responseMessage) {
        super(responseMessage.getMessage());
        this.code = responseCode.getCode();
        this.message = responseMessage.getMessage();
    }

    /**
     * 创建业务异常
     *
     * @param responseCode 异常状态码
     * @param message      异常信息
     */
    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
        this.message = message;
    }

    /**
     * 创建业务异常
     *
     * @param code    异常状态码
     * @param message 异常信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 创建业务异常
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 创建业务异常
     *
     * @param responseMessage 异常信息提示
     * @param cause           异常原因
     */
    public BusinessException(ResponseMessage responseMessage, Throwable cause) {
        super(responseMessage.getMessage(), cause);
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = responseMessage.getMessage();
    }


    /**
     * 创建业务异常
     *
     * @param message 异常信息
     * @param cause   异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 创建业务异常
     *
     * @param code    异常状态码
     * @param message 异常信息
     * @param cause   异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

}