package com.inkstage.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板渲染工具类
 */
@Slf4j
public class TemplateRenderUtils {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    /**
     * 渲染字符串模板
     *
     * @param template 模板字符串
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    public static String renderString(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer stringBuilder = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.getOrDefault(variableName, "");
            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(value != null ? value.toString() : ""));
        }
        matcher.appendTail(stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * 构建变量映射
     *
     * @param variablesJson 变量定义JSON
     * @param params 参数数组
     * @return 变量映射
     */
    public static Map<String, Object> buildVariables(String variablesJson, Object... params) {
        Map<String, Object> variables = new HashMap<>();

        // 默认变量名
        String[] defaultVarNames = {"username", "articleTitle", "articleId", "commentContent",
                "replyContent", "reason", "result", "status", "message", "content"};

        for (int i = 0; i < params.length && i < defaultVarNames.length; i++) {
            variables.put(defaultVarNames[i], params[i]);
        }

        return variables;
    }

    /**
     * 构建相关ID变量映射
     *
     * @param relatedId 相关ID
     * @return 变量映射
     */
    public static Map<String, Object> buildRelatedIdVariables(Long relatedId) {
        Map<String, Object> variables = new HashMap<>();
        if (relatedId != null) {
            variables.put("articleId", relatedId);
            variables.put("userId", relatedId);
        }
        return variables;
    }
}
