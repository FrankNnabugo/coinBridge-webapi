package com.example.paymentApi.shared.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenResponseUtil {

    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private TokenResponseUtil() {}

    /**
     * Sets the access token into the response headers as:
     * Authorization: Bearer <token>
     */
    public static void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN_HEADER, "Bearer " + accessToken);
    }

    /**
     * Places the refresh token into an HttpOnly, Secure cookie.
     */
    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // only over HTTPS
        cookie.setPath("/auth/refresh"); // only sent when refreshing
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
                return cookie.getValue();
            }
        }

        return null;
    }

}
