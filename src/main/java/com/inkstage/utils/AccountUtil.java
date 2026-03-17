package com.inkstage.utils;

import com.inkstage.enums.auth.AccountType;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 账号工具类
 */
@Slf4j
public class AccountUtil {

    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    // 用户名正则表达式（2-32个字符，支持字母、数字、下划线、汉字）
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,32}$"
    );

    /**
     * 判断账号类型
     * @param account 账号
     * @return 账号类型
     */
    public static AccountType getAccountType(String account) {
        if (account == null || account.isEmpty()) {
            return AccountType.UNKNOWN;
        }

        if (EMAIL_PATTERN.matcher(account).matches()) {
            return AccountType.EMAIL;
        }

        if (PHONE_PATTERN.matcher(account).matches()) {
            return AccountType.PHONE;
        }

        if (USERNAME_PATTERN.matcher(account).matches()) {
            return AccountType.USERNAME;
        }

        return AccountType.UNKNOWN;
    }

    /**
     * 验证密码强度
     * @param password 密码
     * @return 是否符合强度要求
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // 检查密码复杂度
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        // 至少满足3个条件
        int count = 0;
        if (hasUpperCase) count++;
        if (hasLowerCase) count++;
        if (hasDigit) count++;
        if (hasSpecialChar) count++;

        return count >= 3;
    }

    /**
     * 验证账号格式
     * @param account 账号
     * @return 是否符合格式要求
     */
    public static boolean validateAccountFormat(String account) {
        AccountType type = getAccountType(account);
        return type != AccountType.UNKNOWN;
    }
}
