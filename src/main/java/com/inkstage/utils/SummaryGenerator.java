package com.inkstage.utils;

import java.util.regex.Pattern;

/**
 * 文章摘要生成工具类
 * 流程：HTML预处理 → 分行过滤 → 清除格式 → 截断
 */
public class SummaryGenerator {

    private static final int MAX_LENGTH = 180;

    // 收集正文的上限，超过此长度即可停止处理后续行
    private static final int COLLECT_LIMIT = MAX_LENGTH + 100;

    private static final boolean[] STOP_CHARS = new boolean[65536];

    // 预编译换行分割Pattern
    private static final Pattern LINE_SPLIT_PATTERN = Pattern.compile("\\R");

    // HTML块级闭合标签，用于在闭合处插入换行，使内联HTML变为独立行
    private static final Pattern BLOCK_TAG_PATTERN = Pattern.compile(
            "</(h[1-6]|p|div|li|tr|blockquote|pre|section|article|header|footer|nav|aside|main|figure|figcaption|details|summary)>",
            Pattern.CASE_INSENSITIVE
    );

    // 行内格式标记：HTML标签、代码块、行内代码、图片、链接、粗体斜体删除线
    private static final Pattern INLINE_FORMAT_PATTERN = Pattern.compile(
            "<[^>]+>"
                    + "|```[\\s\\S]*?```"
                    + "|`([^`]+)`"
                    + "|!\\[[^]]*]\\([^)]+\\)"
                    + "|\\[([^]]+)]\\([^)]+\\)"
                    + "|\\*\\*|__|\\*|_|~~"
    );

    // 预编译空白压缩Pattern
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    // HTML标题标签，整块删除（含内容），用[^<]替代.*?避免回溯
    private static final Pattern HTML_HEADING_PATTERN = Pattern.compile(
            "<h[1-6][^>]*>[^<]*(?:<(?!/h[1-6])[^>]*>[^<]*)*</h[1-6]>",
            Pattern.CASE_INSENSITIVE
    );

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
        if (content == null || content.isBlank()) {
            return "";
        }

        content = preprocessHtml(content);

        String realText = filterLines(content);
        if (realText.isBlank()) {
            return "";
        }

        realText = cleanInlineFormat(realText);

        return truncate(realText);
    }

    /**
     * HTML预处理：删除标题标签内容，块级闭合标签后插入换行
     */
    private static String preprocessHtml(String content) {
        content = HTML_HEADING_PATTERN.matcher(content).replaceAll("");
        content = BLOCK_TAG_PATTERN.matcher(content).replaceAll("\n");
        return content;
    }

    /**
     * 分行过滤：跳过标题、列表、引用、代码块、表格等非正文行
     * 收集到足够正文后提前终止，避免处理全文
     */
    private static String filterLines(String content) {
        String[] lines = LINE_SPLIT_PATTERN.split(content);
        StringBuilder sb = new StringBuilder(COLLECT_LIMIT);
        boolean inCodeBlock = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                continue;
            }
            if (inCodeBlock) continue;

            if (isSkippableLine(trimmed)) continue;

            sb.append(trimmed).append(' ');

            if (sb.length() >= COLLECT_LIMIT) {
                break;
            }
        }

        return sb.toString().trim();
    }

    /**
     * 判断是否为需要跳过的非正文行
     */
    private static boolean isSkippableLine(String line) {
        // Markdown标题、引用、表格、代码块边界
        if (line.startsWith("#")) return true;
        if (line.startsWith(">")) return true;
        if (line.startsWith("|")) return true;
        if (line.startsWith("```")) return true;

        // 无序列表（- + *后跟空格）
        if (line.startsWith("-") || line.startsWith("+")) return true;
        if (line.startsWith("*")) {
            if (line.length() == 1 || !Character.isLetterOrDigit(line.charAt(1))) {
                return true;
            }
        }

        // 分割线
        if (line.startsWith("===") || line.startsWith("---") || line.startsWith("***")) return true;

        // 有序列表（数字. 或 数字））
        if (line.length() >= 2 && Character.isDigit(line.charAt(0))) {
            int i = 1;
            while (i < line.length() && Character.isDigit(line.charAt(i))) i++;
            if (i < line.length() && (line.charAt(i) == '.' || line.charAt(i) == ')')) return true;
        }

        // HTML正文标签（如<p>）不跳过
        if (line.startsWith("<") && line.length() >= 2 && Character.isLetter(line.charAt(1))) {
            return false;
        }

        // 非中文/英文/数字开头的行视为符号行，跳过
        char first = line.charAt(0);
        return isSymbol(first);
    }

    /**
     * 清除行内格式标记，保留文字内容
     */
    private static String cleanInlineFormat(String content) {
        content = INLINE_FORMAT_PATTERN.matcher(content).replaceAll("$1$2");
        content = WHITESPACE_PATTERN.matcher(content).replaceAll(" ").trim();
        return content;
    }

    /**
     * 在MAX_LENGTH范围内找到第一个终止符截断，无终止符则硬截断
     */
    private static String truncate(String text) {
        int len = Math.min(text.length(), MAX_LENGTH);
        for (int i = 0; i < len; i++) {
            if (STOP_CHARS[text.charAt(i)]) {
                return text.substring(0, i + 1).trim();
            }
        }
        return text.substring(0, len).trim();
    }

    /**
     * 非中文/英文/数字视为符号
     */
    private static boolean isSymbol(char c) {
        return !Character.isDigit(c)
                && !((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                && !(c >= '一' && c <= '鿿');
    }
}
