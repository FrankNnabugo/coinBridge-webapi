package com.example.paymentApi.config;

import com.example.paymentApi.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session-> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/actuator/**",
                                "/health",
                                "/api-docs",
                                "/swagger",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/users",
                                "/api/v1/users/otp/mail",
                                "/api/v1/users/otp/verify",
                                "/api/v1/users/authenticate",
                                "/api/v1/circle/pay-in",
                                "/api/v1/circle/finalize/transfer"
                        )
                .permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }
}
