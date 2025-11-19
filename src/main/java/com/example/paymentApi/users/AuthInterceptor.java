package com.example.paymentApi.users;

import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final ExceptionThrower exceptionThrower;

    public AuthInterceptor(JwtService jwtService, ExceptionThrower exceptionThrower) {
        this.jwtService = jwtService;
        this.exceptionThrower = exceptionThrower;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exceptionThrower.throwInvalidTokenException(HttpRequestUtil.getServletPath());
        }

        try {
            String authToken = authHeader.substring(7);
            String userId = jwtService.extractSubject(authToken);
            request.setAttribute("userId", userId);
        }
        catch (Exception exception) {

            throw new RuntimeException(exception.getMessage());
        }

        return true;
    }
}