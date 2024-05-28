package com.nighthawk.spring_portfolio;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/volumes/uploads/**").addResourceLocations("file:volumes/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(
            "https://csa-ai-frontend.vercel.app"
        ).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("authorization", "content-type", "x-csrf-token")
        .exposedHeaders("authorization")
        .allowCredentials(true);
    }
}
