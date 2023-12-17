package com.corgan.angularbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyAppConfig implements WebMvcConfigurer {

    // This is configuration is set to affect the @RestController only, this is why it has a separated config
    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Value("${allowed.origins}")
    private String origins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        System.out.println("Rest cors config: " + basePath + "/** " + origins);
        registry.addMapping(basePath + "/**").allowedOrigins(origins);

    }
}
