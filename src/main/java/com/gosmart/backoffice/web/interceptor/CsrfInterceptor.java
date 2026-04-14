package com.gosmart.backoffice.web.interceptor;

import com.gosmart.backoffice.config.CsrfProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CsrfInterceptor implements HandlerInterceptor {
    private final CsrfProperties csrfProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public CsrfInterceptor(CsrfProperties csrfProperties) {
        this.csrfProperties = csrfProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (isSafeMethod(method)) {
            ensureCookiePresent(request, response);
            return true;
        }
        ensureCookiePresent(request, response);
        String cookieToken = readCookieToken(request);
        if (cookieToken == null || cookieToken.isEmpty()) {
            sendForbidden(response);
            return false;
        }
        String headerToken = request.getHeader(csrfProperties.getHeaderName());
        String paramToken = request.getParameter("_csrf");
        String requestToken = headerToken != null && !headerToken.isEmpty() ? headerToken : paramToken;
        if (requestToken == null || requestToken.isEmpty()) {
            sendForbidden(response);
            return false;
        }
        if (!cookieToken.equals(requestToken)) {
            sendForbidden(response);
            return false;
        }
        return true;
    }

    private boolean isSafeMethod(String method) {
        if (method == null) {
            return true;
        }
        String m = method.toUpperCase();
        return "GET".equals(m) || "HEAD".equals(m) || "OPTIONS".equals(m);
    }

    private void ensureCookiePresent(HttpServletRequest request, HttpServletResponse response) {
        String existing = readCookieToken(request);
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        String token = generateToken();
        ResponseCookie cookie = ResponseCookie.from(csrfProperties.getCookieName(), token)
                .httpOnly(false)
                .secure(csrfProperties.isCookieSecure())
                .path(csrfProperties.getCookiePath())
                .sameSite(csrfProperties.getCookieSameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String readCookieToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (csrfProperties.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
