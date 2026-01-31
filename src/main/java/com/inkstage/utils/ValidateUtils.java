package com.inkstage.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidateUtils {

    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 手机号正则表达式(中国大陆)
     */
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 身份证号正则表达式(18位)
     */
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    /**
     * 用户名正则表达式(字母、数字、下划线, 4-20位)
     */
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";

    /**
     * 密码正则表达式(至少8位, 包含字母和数字)
     */
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";

    /**
     * URL正则表达式
     */
    private static final String URL_REGEX = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$";

    /**
     * IP地址正则表达式
     */
    private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否合法
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * 验证手机号格式(中国大陆)
     *
     * @param phone 手机号
     * @return 是否合法
     */
    public static boolean isPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 验证身份证号格式(18位)
     *
     * @param idCard 身份证号
     * @return 是否合法
     */
    public static boolean isIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return false;
        }
        return Pattern.matches(ID_CARD_REGEX, idCard);
    }

    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @return 是否合法
     */
    public static boolean isUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return Pattern.matches(USERNAME_REGEX, username);
    }

    /**
     * 验证密码格式
     *
     * @param password 密码
     * @return 是否合法
     */
    public static boolean isPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    /**
     * 验证URL格式
     *
     * @param url URL
     * @return 是否合法
     */
    public static boolean isUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return Pattern.matches(URL_REGEX, url);
    }

    /**
     * 验证IP地址格式
     *
     * @param ip IP地址
     * @return 是否合法
     */
    public static boolean isIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return Pattern.matches(IP_REGEX, ip);
    }

    /**
     * 验证字符串是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 验证字符串是否非空
     *
     * @param str 字符串
     * @return 是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 验证对象是否为空
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 验证对象是否非空
     *
     * @param obj 对象
     * @return 是否非空
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }
}