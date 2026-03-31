package com.inkstage.utils;

import com.inkstage.enums.notification.NotificationTemplateVariable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用 Spring Expression Language (SpEL) 的模板渲染工具类
 */
@Slf4j
public class SpELTemplateRender {

    private static final ExpressionParser parser = new SpelExpressionParser();
    // 匹配 {{variable}} 和 ${variable} 格式的变量
    private static final Pattern DOUBLE_BRACE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");
    private static final Pattern DOLLAR_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    /**
     * 渲染字符串模板
     *
     * @param template  模板字符串
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    public static String render(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        if (variables == null) {
            return template;
        }

        try {
            // 替换 {{variable}} 格式的变量
            String result = renderWithPattern(template, DOUBLE_BRACE_PATTERN, variables);
            // 替换 ${variable} 格式的变量
            result = renderWithPattern(result, DOLLAR_PATTERN, variables);

            return result;
        } catch (Exception e) {
            log.error("渲染模板失败: {}", e.getMessage());
            return template; // 渲染失败时返回原始模板
        }
    }

    /**
     * 使用指定模式渲染模板
     */
    private static String renderWithPattern(String template, Pattern pattern, Map<String, Object> variables) {
        Matcher matcher = pattern.matcher(template);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // 追加匹配前的内容
            sb.append(template, lastEnd, matcher.start());

            String variableName = matcher.group(1);
            // 验证变量是否在枚举类中定义
            if (!NotificationTemplateVariable.isValidKey(variableName)) {
                log.warn("模板中使用了未定义的变量: {}", variableName);
                // 未定义的变量保持原样
                sb.append(matcher.group());
            } else {
                // 使用 SpEL 解析变量
                String expression = "#" + variableName;
                StandardEvaluationContext context = new StandardEvaluationContext();
                // 将所有变量放入上下文
                variables.forEach(context::setVariable);

                try {
                    Object value = parser.parseExpression(expression).getValue(context);
                    sb.append(value != null ? value.toString() : "");
                } catch (Exception e) {
                    log.warn("解析变量失败: {}", e.getMessage());
                    // 解析失败时保持原样
                    sb.append(matcher.group());
                }
            }

            lastEnd = matcher.end();
        }

        // 追加剩余内容
        sb.append(template, lastEnd, template.length());

        return sb.toString();
    }

}
