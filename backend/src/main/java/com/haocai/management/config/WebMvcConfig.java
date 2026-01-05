package com.haocai.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC配置类
 * 配置自定义的类型转换器
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserStatusConverter userStatusConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(userStatusConverter);
    }

    /**
     * 配置Jackson消息转换器
     * 支持Java 8日期时间类型（LocalDateTime等）的序列化和反序列化
     * 遵循：代码规范-第3条（异常处理）
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8日期时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期序列化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
    }
}
