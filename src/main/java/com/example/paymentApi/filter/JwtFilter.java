package com.example.paymentApi.filter;

import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.HttpRequestUtil;
import com.example.paymentApi.shared.exception.ApiError;
import com.example.paymentApi.shared.exception.GeneralAppException;
import com.example.paymentApi.shared.utility.TokenUtil;
import com.example.paymentApi.users.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ExceptionThrower exceptionThrower;
    private final TokenUtil tokenUtil;

    public JwtFilter(JwtService jwtService, ExceptionThrower exceptionThrower, TokenUtil tokenUtil){
        this.jwtService = jwtService;
        this.exceptionThrower = exceptionThrower;
        this.tokenUtil = tokenUtil;

    }

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/v1/users",
            "/api/v1/users/otp/mail",
            "/api/v1/users/otp/verify",
            "/api/v1/users/authenticate",
            "/api/v1/circle/pay-in",
            "/api/v1/circle/finalize/transfer"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterchain)
            throws ServletException, IOException {

        if (EXCLUDED_PATHS.contains(HttpRequestUtil.getServletPath())) {
            filterchain.doFilter(request, response);
            return;
        }

            String token = tokenUtil.extractTokenFromHeader(request);
            if(!jwtService.isTokenValid(token)) {
                exceptionThrower.throwMissingTokenException(HttpRequestUtil.getServletPath());


                String userId = jwtService.extractSubject(token);
                request.setAttribute("userId", userId);

                filterchain.doFilter(request, response);
            }
    }

    }


