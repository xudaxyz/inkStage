package com.inkstage.utils;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板渲染工具类
 */
@Slf4j
public class TemplateRenderUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 支持两种变量格式：{{variable}} 和 ${variable}
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");
    private static final Pattern DOLLAR_VARIABLE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    // 模板缓存，提高性能
    private static final Map<String, String> TEMPLATE_CACHE = new ConcurrentHashMap<>();

    /**
     * 渲染字符串模板
     *
     * @param template  模板字符串
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    public static String renderString(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        if (variables == null) {
            variables = Map.of();
        }

        try {
            // 尝试从缓存获取渲染结果
            String cacheKey = generateCacheKey(template, variables);
            String cachedResult = TEMPLATE_CACHE.get(cacheKey);
            if (cachedResult != null) {
                return cachedResult;
            }

            // 替换 {{variable}} 格式
            String result = renderWithPattern(template, VARIABLE_PATTERN, variables);
            // 替换 ${variable} 格式
            result = renderWithPattern(result, DOLLAR_VARIABLE_PATTERN, variables);

            // 缓存渲染结果
            TEMPLATE_CACHE.put(cacheKey, result);

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
        StringBuilder stringBuilder = new StringBuilder(template.length());

        int lastEnd = 0;
        while (matcher.find()) {
            try {
                // 追加匹配前的内容
                stringBuilder.append(template, lastEnd, matcher.start());

                String variableName = matcher.group(1);
                Object value = variables.getOrDefault(variableName, "");
                stringBuilder.append(value != null ? value.toString() : "");

                lastEnd = matcher.end();
            } catch (Exception e) {
                log.warn("替换变量失败: {}", e.getMessage());
                // 替换失败时保持原样
                stringBuilder.append(template, lastEnd, matcher.end());
                lastEnd = matcher.end();
            }
        }
        // 追加剩余内容
        stringBuilder.append(template, lastEnd, template.length());

        return stringBuilder.toString();
    }

    /**
     * 生成缓存键
     */
    private static String generateCacheKey(String template, Map<String, Object> variables) {
        StringBuilder keyBuilder = new StringBuilder(template);
        keyBuilder.append("|");

        // 对变量进行排序，确保相同变量不同顺序时生成相同的缓存键
        variables.keySet().stream()
                .sorted()
                .forEach(key -> keyBuilder.append(key).append("=").append(variables.get(key)).append(","));

        return keyBuilder.toString();
    }

    /**
     * 构建变量映射
     *
     * @param variablesJson 变量定义JSON
     * @param params        参数数组
     * @return 变量映射
     */
    public static Map<String, Object> buildVariables(String variablesJson, Object... params) {
        Map<String, Object> variables = new HashMap<>();
        if (variablesJson == null || variablesJson.isEmpty()) {
            return variables;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(variablesJson);
            if (rootNode.isArray()) {
                // 处理数组格式：["var1", "var2"] 或 [{"name": "var1", "value": "default"}]
                processArrayNode(rootNode, variables, params);
            }
        } catch (Exception e) {
            log.warn("构建变量映射失败: {}", e.getMessage());
        }
        return variables;
    }

    /**
     * 处理数组格式/对象格式的变量
     *
     * @param arrayNode 数组格式的变量定义
     * @param variables 变量
     * @param params    参数数组
     */
    private static void processArrayNode(JsonNode arrayNode, Map<String, Object> variables, Object[] params) {
        Iterator<JsonNode> elements = arrayNode.iterator();
        int paramIndex = 0;

        while (elements.hasNext()) {
            JsonNode element = elements.next();
            if (element.isString()) {
                // 字符串数组格式: ["var1", "var2"]
                String varName = element.asString();
                if (paramIndex < params.length && params[paramIndex] != null) {
                    variables.put(varName, params[paramIndex]);
                    paramIndex++;
                }
            } else if (element.isObject()) {
                // 对象数组格式: [{"name": "var1", "value": "default"}]
                String varName = element.path("name").asString();
                if (!varName.isEmpty()) {
                    // 优先使用传入的参数，否则使用默认值
                    Object value = (paramIndex < params.length && params[paramIndex] != null)
                            ? params[paramIndex]
                            : element.path("value").asString();
                    variables.put(varName, value);
                    paramIndex++;
                }
            }
        }
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
            // 这里只添加通用的ID变量，具体使用哪些变量由模板定义决定
            variables.put("relatedId", relatedId);
        }
        return variables;
    }

    /**
     * 清理模板缓存
     */
    public static void clearTemplateCache() {
        TEMPLATE_CACHE.clear();
        log.info("模板缓存已清理");
    }

    /**
     * 获取模板缓存大小
     *
     * @return 缓存大小
     */
    public static int getTemplateCacheSize() {
        return TEMPLATE_CACHE.size();
    }

    /**
     * 检查模板是否包含变量
     *
     * @param template 模板字符串
     * @return 是否包含变量
     */
    public static boolean containsVariables(String template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        return VARIABLE_PATTERN.matcher(template).find() || DOLLAR_VARIABLE_PATTERN.matcher(template).find();
    }

    /**
     * 提取模板中的变量名
     *
     * @param template 模板字符串
     * @return 变量名列表
     */
    public static Map<String, String> extractVariables(String template) {
        Map<String, String> variables = new HashMap<>();
        if (template == null || template.isEmpty()) {
            return variables;
        }

        // 提取 {{variable}} 格式的变量
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            variables.put(variableName, variableName);
        }

        // 提取 ${variable} 格式的变量
        matcher = DOLLAR_VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            variables.put(variableName, variableName);
        }

        return variables;
    }
}