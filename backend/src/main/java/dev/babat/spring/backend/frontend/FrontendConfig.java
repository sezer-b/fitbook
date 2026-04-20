package dev.babat.spring.backend.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class FrontendConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private static final CacheControl STATIC_CACHE = CacheControl
            .maxAge(365, TimeUnit.DAYS)
            .cachePublic();

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // image uploads
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        // frontend
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(STATIC_CACHE);
    }
}