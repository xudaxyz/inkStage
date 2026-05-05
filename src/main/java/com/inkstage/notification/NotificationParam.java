package com.inkstage.notification;

import com.inkstage.enums.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NotificationParam {

    /**
     * 通知接收者ID
     */
    protected Long userId;
    /**
     * 通知发送者ID
     */
    protected Long senderId;
    /**
     * 通知类型
     */
    protected NotificationType notificationType;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = this.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    if (value != null) {
                        map.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    log.warn(e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }
}
