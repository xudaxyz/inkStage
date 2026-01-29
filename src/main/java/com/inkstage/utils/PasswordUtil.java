package com.inkstage.utils;

import java.util.Random;

/**
 * 密码工具类
 */
public class PasswordUtil {

    private static final String CHARS = "AaBbCcDdEeFfGgHhJjKkMmNnPpQqRrSsTtUuVvWwXxYyZz23456789";

    /**
     * 生成简单的随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        length = length <= 0 ? 12 : length;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
