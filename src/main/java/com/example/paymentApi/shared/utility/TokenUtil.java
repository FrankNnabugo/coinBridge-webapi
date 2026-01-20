package com.example.paymentApi.shared.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_COOKIE = "EmailAddress";

    /**
     * Sets the access token into the response headers as:
     * Authorization: Bearer <token>
     */
    public static void setAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN_HEADER, "Bearer " + accessToken);
    }


    public static void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // only over HTTPS
        cookie.setPath("/api/v1/users/{userId}/refresh-token"); // only sent when accessed through path
        cookie.setMaxAge(604800000); // 7 days
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

    public static String extractRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getName();
            }
        }
        return null;
    }


}
