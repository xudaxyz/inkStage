package com.inkstage.config.oauth2;

import com.inkstage.entity.model.User;
import com.inkstage.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义令牌增强器
 * 在JWT令牌中添加用户信息
 */
public class CustomTokenEnhancer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication authentication = context.getPrincipal();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            User user = userDetails.getUser();
            
            // 添加用户基本信息到JWT声明
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            claims.put("nickname", user.getNickname());
            claims.put("signature", user.getSignature());
            claims.put("gender", user.getGender());
            claims.put("avatar", user.getAvatar());
            claims.put("email", user.getEmail());
            claims.put("status", user.getStatus());
            
            // 将自定义声明添加到JWT
            context.getClaims().claims((map) -> map.putAll(claims));
        }
    }
}
