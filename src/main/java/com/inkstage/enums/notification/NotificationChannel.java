package com.inkstage.enums.notification;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

/**
 * 通知渠道枚举
 */
@Getter
public enum NotificationChannel implements EnumCode {

    SITE(1, "系统", "通过站内消息系统发送"),
    EMAIL(2, "邮件", "通过电子邮件发送"),
    SMS(3, "短信", "通过短信发送");

    private final Integer code;
    private final String desc;
    private final String remark;

    NotificationChannel(Integer code, String desc, String remark) {
        this.code = code;
        this.desc = desc;
        this.remark = remark;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 渠道代码
     * @return 通知渠道枚举，未找到返回null
     */
    public static NotificationChannel fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return null;
    }

    /**
     * 根据名称获取枚举
     *
     * @param name 渠道名称
     * @return 通知渠道枚举，未找到返回null
     */
    public static NotificationChannel fromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
