package com.gosmart.backoffice.security;

import com.gosmart.backoffice.config.AuthProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthCookie {
    private final AuthProperties authProperties;

    public AuthCookie(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public void setAuthCookie(HttpHeaders headers, String token) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieName(), token)
                .httpOnly(true)
                .secure(authProperties.isCookieSecure())
                .path(authProperties.getCookiePath())
                .sameSite(authProperties.getCookieSameSite())
                .maxAge(Duration.ofMinutes(authProperties.getIdleTimeoutMinutes()))
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void setAuthCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieName(), token)
                .httpOnly(true)
                .secure(authProperties.isCookieSecure())
                .path(authProperties.getCookiePath())
                .sameSite(authProperties.getCookieSameSite())
                .maxAge(Duration.ofMinutes(authProperties.getIdleTimeoutMinutes()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearAuthCookie(HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieName(), "")
                .httpOnly(true)
                .secure(authProperties.isCookieSecure())
                .path(authProperties.getCookiePath())
                .sameSite(authProperties.getCookieSameSite())
                .maxAge(Duration.ZERO)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieName(), "")
                .httpOnly(true)
                .secure(authProperties.isCookieSecure())
                .path(authProperties.getCookiePath())
                .sameSite(authProperties.getCookieSameSite())
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String readToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (authProperties.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
