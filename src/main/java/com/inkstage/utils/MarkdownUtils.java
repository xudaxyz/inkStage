package com.inkstage.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.slf4j.Slf4j;

/**
 * Markdown工具类，用于Markdown转换为HTML
 */
@Slf4j
public class MarkdownUtils {

    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    /**
     * 将Markdown内容转换为HTML
     *
     * @param markdown Markdown内容
     * @return HTML内容
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        try {
            Node document = PARSER.parse(markdown);
            return RENDERER.render(document);
        } catch (Exception e) {
            log.error("Markdown转换为HTML失败", e);
            // 转换失败时返回原始内容
            return markdown;
        }
    }
}
