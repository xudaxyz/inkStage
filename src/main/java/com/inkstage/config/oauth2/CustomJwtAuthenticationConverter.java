package com.inkstage.config.oauth2;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * 自定义JWT认证转换器
 * 完善JWT认证转换，确保正确处理用户信息
 */
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public CustomJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 配置权限前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        // 配置权限声明名称
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    }
}

