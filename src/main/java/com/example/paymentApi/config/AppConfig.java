package com.example.paymentApi.config;

import com.example.paymentApi.interceptor.AuthInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer{
    private final AuthInterceptor authInterceptor;

    public AppConfig(AuthInterceptor authInterceptor){
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(
                        "/api/*/users",
                        "/api/*/users/login",
                        "/api/*/users/otp/mail",
                        "/api/*/users/otp/verify",
                        "/actuator/**",
                        "/api",
                        "/api-docs",
                        "/swagger",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/v1/circle/pay-in",
                        "/api/v1/circle/finalize/transfer"


                );
    }
        @Bean
        public ModelMapper modelMapper () {
            return new ModelMapper();
        }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //enable on all origins and endpoints
        registry.addMapping("/**").allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
}
