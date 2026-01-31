package com.inkstage.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件模板工具类
 * 用于加载和渲染邮件模板
 */
@Component
public class EmailTemplateUtils {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * 加载并渲染邮件模板
     *
     * @param templatePath 模板路径(相对于resources/templates目录)
     * @param variables    变量映射
     * @return 渲染后的HTML内容
     * @throws IOException 模板加载失败时抛出
     */
    public String renderTemplate(String templatePath, Map<String, Object> variables) throws IOException {
        // 构建完整的模板路径
        String fullPath = "templates/" + templatePath;

        // 加载模板文件
        Resource resource = new ClassPathResource(fullPath);
        if (!resource.exists()) {
            throw new IOException("Template file not found: " + fullPath);
        }

        // 读取模板内容
        StringBuilder templateContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line).append("\n");
            }
        }

        // 渲染模板
        return replaceVariables(templateContent.toString(), variables);
    }

    /**
     * 替换模板中的变量
     *
     * @param template  模板内容
     * @param variables 变量映射
     * @return 渲染后的内容
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            String replacement = (value != null) ? value.toString() : "";
            matcher.appendReplacement(result, replacement);
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 检查模板文件是否存在
     *
     * @param templatePath 模板路径
     * @return 是否存在
     */
    public boolean templateExists(String templatePath) {
        String fullPath = "templates/" + templatePath;
        Resource resource = new ClassPathResource(fullPath);
        return resource.exists();
    }
}
