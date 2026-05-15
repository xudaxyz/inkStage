package com.inkstage.utils;

/**
 * 文章摘要生成工具类
 * 使用截断方式生成摘要，去除Markdown格式后取前200字符内的首句
 */
public class SummaryGenerator {

    private static final int MAX_LENGTH = 180;

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

        if (plainText.length() <= MAX_LENGTH) {
            return plainText;
        }

        String sub = plainText.substring(0, MAX_LENGTH);
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[。！？.!?\n]").matcher(sub);
        if (matcher.find()) {
            return sub.substring(0, matcher.start() + 1);
        }

        return sub + "...";
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
