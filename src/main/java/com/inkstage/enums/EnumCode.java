package com.inkstage.enums;

/**
 * 带code字段的枚举接口
 * 所有带code字段的枚举类都应实现此接口
 */
public interface EnumCode {
    
    /**
     * 获取枚举的code值
     * @return code值
     */
    Integer getCode();

    String getDesc();
}