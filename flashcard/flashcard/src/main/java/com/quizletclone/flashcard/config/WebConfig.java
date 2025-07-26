package com.quizletclone.flashcard.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.quizletclone.flashcard.interceptor.UserStatusInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserStatusInterceptor userStatusInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/**") // Apply to all URLs
                .excludePathPatterns("/login", "/signup", "/css/**", "/js/**", "/images/**", "/account-locked"); // Exclude
                                                                                                                 // public
                                                                                                                 // pages
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục uploads/upload
        String uploadPath = Paths.get("flashcard/flashcard/uploads/upload").toAbsolutePath().toString();

        registry.addResourceHandler("/images/**") // URL sẽ bắt đầu bằng /images/
                .addResourceLocations("file:" + uploadPath + "/"); // ánh xạ tới thư mục thực   
    }
}
