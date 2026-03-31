package com.inkstage.utils;

import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.notification.NotificationTemplateVariable;
import com.inkstage.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板验证工具类
 * 用于验证通知模板的有效性
 */
@Slf4j
public class TemplateValidator {

    // 匹配 {{variable}} 或者 ${variable} 格式的变量
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}|\\$\\{(\\w+)}");

    /**
     * 验证通知模板的有效性
     *
     * @param template 通知模板
     */
    public static boolean validate(NotificationTemplate template) {
        try {
            if (template != null) {
                // 验证标题模板
                validateTemplatePart(template.getTitleTemplate(), "标题模板");

                // 验证内容模板
                validateTemplatePart(template.getContentTemplate(), "内容模板");

                // 验证链接模板(如果有)
                if (template.getActionUrlTemplate() != null && !template.getActionUrlTemplate().isEmpty()) {
                    validateTemplatePart(template.getActionUrlTemplate(), "链接模板");
                }
            }
            return true;
        } catch (BusinessException e) {
            log.error("模板验证失败{}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证模板的单个部分
     *
     * @param templatePart 模板部分
     * @param partType     部分类型
     */
    private static void validateTemplatePart(String templatePart, String partType) {
        if (templatePart != null && !templatePart.isEmpty()) {
            // 使用正则表达式验证模板语法
            validateVariablesInTemplate(templatePart, partType);
        }
    }

    /**
     * 验证模板中的变量是否合法
     *
     * @param content      模板内容
     * @param templateType 模板类型
     */
    private static void validateVariablesInTemplate(String content, String templateType) {
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            String variableName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            // 检查变量是否在枚举类中定义
            if (!NotificationTemplateVariable.isValidKey(variableName)) {
                throw new BusinessException(templateType + "中使用了未定义的变量: " + variableName);
            }
        }
    }

}
