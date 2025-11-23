package com.example.paymentApi.filter;

import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.HttpRequestUtil;
import com.example.paymentApi.shared.exception.authException.ExpiredTokenException;
import com.example.paymentApi.shared.exception.authException.InvalidTokenException;
import com.example.paymentApi.shared.exception.authException.MissingTokenException;
import com.example.paymentApi.users.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import static com.example.paymentApi.shared.utility.TokenUtil.extractTokenFromHeader;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ExceptionThrower exceptionThrower;

    public JwtFilter(JwtService jwtService, ExceptionThrower exceptionThrower){
        this.jwtService = jwtService;
        this.exceptionThrower = exceptionThrower;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String token = extractTokenFromHeader(request);
            if(!jwtService.isTokenValid(token)){
                exceptionThrower.throwInvalidTokenException(HttpRequestUtil.getServletPath());
            }

            String userId = jwtService.extractSubject(token);
            request.setAttribute("userId", userId);

            filterChain.doFilter(request, response);

        }
        catch (MissingTokenException | InvalidTokenException | ExpiredTokenException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        catch (Exception ex) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   HttpStatus status,
                                   String message)
            throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");

        String json = String.format("{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status.value(), status.getReasonPhrase(), message);

        response.getWriter().write(json);
    }
}

