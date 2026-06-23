package com.sbms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebSecurityConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/verify-login-otp",
                        "/api/auth/resend-login-otp/**",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/api/auth/send-otp",
                        "/api/auth/verify-otp",
                        "/api/files/images/**",
                        "/api/files/documents/**",
                        "/actuator/**",
                        "/api/customers/**",
                        "/api/users/**");
    }
}
