package com.inkstage.enums;

import lombok.Getter;

/**
 * 评论计数类型枚举
 */
@Getter
public enum CommentCountType implements EnumCode{
    
    /**
     * 点赞数
     */
    LIKE("like", "点赞数"),
    
    /**
     * 回复数
     */
    REPLY("reply", "回复数");
    
    /**
     * 类型值
     */
    private final String value;
    
    /**
     * 类型描述
     */
    private final String desc;
    
    CommentCountType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    
    /**
     * 根据值获取枚举
     * 
     * @param value 类型值
     * @return 枚举实例
     */
    public static CommentCountType getByValue(String value) {
        for (CommentCountType type : CommentCountType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public Integer getCode() {
        return 0;
    }
}