package com.inkstage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 允许的来源
     */
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * 允许的方法
     */
    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    /**
     * 允许的头
     */
    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    /**
     * 是否允许凭证
     */
    @Value("${cors.allow-credentials}")
    private Boolean allowCredentials;

    /**
     * 缓存时间
     */
    @Value("${cors.max-age}")
    private Long maxAge;

    /**
     * 配置跨域
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}