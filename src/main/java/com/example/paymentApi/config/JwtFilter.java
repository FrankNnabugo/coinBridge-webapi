package com.example.paymentApi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFilter {

    @Bean
    public FilterRegistrationBean<com.example.paymentApi.filter.JwtFilter> jwtFilterRegistration(com.example.paymentApi.filter.JwtFilter jwtFilter) {
        FilterRegistrationBean<com.example.paymentApi.filter.JwtFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(jwtFilter);

        // Apply this filter to all routes
        registration.addUrlPatterns("/api/*");

        // Ensure it runs before others (except CORS)
        registration.setOrder(2);

        return registration;
    }
}
