package com.inkstage.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 方法安全配置类
 * 启用方法级别的安全控制
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

}
