package com.myboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry reg) {
        // 예: http://<app-host>:8080/uploads/3/12/image.jpg
        reg.addResourceHandler("/uploads/**")
           .addResourceLocations("file:/mnt/nowait-uploads/")
           .setCachePeriod(3600);
    }
}
