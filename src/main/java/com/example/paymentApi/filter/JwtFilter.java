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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


            String token = tokenUtil.extractTokenFromHeader(request);
            if(!jwtService.isTokenValid(token)) {
                exceptionThrower.throwMissingTokenException(HttpRequestUtil.getServletPath());


                String userId = jwtService.extractSubject(token);
                request.setAttribute("userId", userId);

                filterChain.doFilter(request, response);

            }
    }

    private void sendErrorResponse(HttpServletResponse response, GeneralAppException ex)
            throws IOException {
        ApiError apiError = new ApiError(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getPath()
        );

        response.setStatus(ex.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(
                String.format(
                        "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                        apiError.getTimestamp(), apiError.getStatus(), apiError.getError(),
                        apiError.getMessage(), apiError.getPath()
                )
        );
        response.getWriter().flush();
    }
    }


