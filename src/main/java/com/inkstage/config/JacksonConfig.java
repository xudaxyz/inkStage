package com.inkstage.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson配置类
 * 统一配置Jackson 3.0的序列化特性
 * 将Long类型序列化为String
 */
@Configuration
public class JacksonConfig {

    /**
     * 注册Long转String的自定义序列化器
     * 通过JsonMapperBuilderCustomizer在自动配置的JsonMapper上追加模块，
     * 确保Spring MVC使用的ObjectMapper生效，同时保留spring.jackson.*配置
     *
     * @return JsonMapperBuilderCustomizer定制器
     */
    @Bean
    public JsonMapperBuilderCustomizer longToStringCustomizer() {
        SimpleModule longToStringModule = new SimpleModule();
        longToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
        longToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        return builder -> builder.addModule(longToStringModule);
    }
}
