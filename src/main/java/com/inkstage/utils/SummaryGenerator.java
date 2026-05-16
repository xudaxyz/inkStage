package com.inkstage.utils;


/**
 * 文章摘要生成工具类
 * 使用截断方式生成摘要，去除Markdown格式后取前200字符内的首句
 */
public class SummaryGenerator {

    private static final int MAX_LENGTH = 180;

    // 终止符集合
    private static final boolean[] STOP_CHARS = new boolean[65536];

    static {
        // 初始化终止符：只需要初始化一次
        STOP_CHARS['.'] = true;     // 英文句号
        STOP_CHARS['。'] = true;    // 中文句号
        STOP_CHARS['?'] = true;     // 英文问号
        STOP_CHARS['？'] = true;    // 中文问号
        STOP_CHARS['!'] = true;     // 英文感叹号
        STOP_CHARS['！'] = true;    // 中文感叹号
        STOP_CHARS['\n'] = true;    // 换行
        STOP_CHARS['\r'] = true;    // 回车
    }

    /**
     * 生成文章摘要
     *
     * @param content 文章内容
     * @return 生成的摘要
     */
    public static String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        String plainText = removeFormatting(content);
        if (plainText.isEmpty()) {
            return null;
        }
        int length = Math.min(plainText.length(), MAX_LENGTH);

        for (int i = 0; i < length; i++) {
            char c = plainText.charAt(i);
            if (STOP_CHARS[c]) {
                return plainText.substring(0, i + 1).trim();
            }
        }

        // 没有终止符，返回全文并去空格
        return plainText.substring(0, length).trim();
    }

    /**
     * 去除Markdown和HTML格式
     *
     * @param content 带Markdown和HTML格式的内容
     * @return 纯文本内容
     */
    private static String removeFormatting(String content) {
        content = content.replaceAll("<[^>]*>", "");
        content = content.replaceAll("#+\\s+", "");
        content = content.replaceAll("\\*\\*|__", "");
        content = content.replaceAll("[*_]", "");
        content = content.replaceAll("`{1,3}[\\s\\S]*?`{1,3}", "");
        content = content.replaceAll("\\[([^]]+)]\\([^)]+\\)", "$1");
        content = content.replaceAll("!\\[([^]]*)]\\([^)]+\\)", "");
        content = content.replaceAll("\\n+", " ");
        content = content.replaceAll("\\s+", " ").trim();
        return content;
    }
}
