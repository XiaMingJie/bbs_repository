package com.example.bbs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class ApplicationConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/bbsfile/**").addResourceLocations("file:C:/bbsfile/");
        registry.addResourceHandler("/*").addResourceLocations("classpath:static/");
        super.addResourceHandlers(registry);
    }
}
