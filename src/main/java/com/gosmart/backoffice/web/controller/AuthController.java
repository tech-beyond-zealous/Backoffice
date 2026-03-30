package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.domain.UserSessionEntity;
import com.gosmart.backoffice.security.AuthCookie;
import com.gosmart.backoffice.security.AuthTokenClaims;
import com.gosmart.backoffice.security.JwtProvider;
import com.gosmart.backoffice.service.AuthService;
import com.gosmart.backoffice.dto.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final AuthCookie authCookie;

    public AuthController(AuthService authService, JwtProvider jwtProvider, AuthCookie authCookie) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
        this.authCookie = authCookie;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(
            @RequestParam(value = "reason", required = false) String reason,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.debug("Login page requested reason={} path={} ip={}", reason, request.getRequestURI(), request.getRemoteAddr());
        String token = authCookie.readToken(request);
        if (token != null && !token.isBlank()) {
            try {
                AuthTokenClaims claims = jwtProvider.parseAndValidate(token);
                if (claims.expiresAt().isAfter(Instant.now())) {
                    Optional<UserSessionEntity> sessionOpt = authService.findSession(claims.sessionId());
                    if (sessionOpt.isPresent()) {
                        UserSessionEntity session = sessionOpt.get();
                        if (session.getRevokeDt() == null && claims.userId().equals(session.getUserId())) {
                            log.debug("Login page: existing valid session detected, redirecting userId={}", claims.userId());
                            return "redirect:/dashboard";
                        }
                    }
                }
            } catch (Exception e) {
            }
            authCookie.clearAuthCookie(response);
        }

        applyLoginAlert(reason, model);
        model.addAttribute("loginForm", new LoginForm("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @ModelAttribute("loginForm") LoginForm loginForm,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        String userId = loginForm.userId() == null ? "" : loginForm.userId();
        String password = loginForm.password() == null ? "" : loginForm.password();

        boolean hasError = false;
        if (userId.isBlank()) {
            model.addAttribute("userIdError", "User Id is required");
            hasError = true;
        }
        if (password.isBlank()) {
            model.addAttribute("passwordError", "Password is required");
            hasError = true;
        }
        if (hasError) {
            log.info("Login rejected by validation userId={}", userId);
            model.addAttribute("reason", null);
            model.addAttribute("loginForm", new LoginForm(userId, ""));
            return "login";
        }

        log.info("Login attempt userId={}", userId);
        Optional<UserSessionEntity> sessionOpt = authService.authenticateAndCreateSession(
                userId,
                password,
                request
        );
        if (sessionOpt.isEmpty()) {
            log.info("Login failed userId={}", userId);
            model.addAttribute("alertType", "danger");
            model.addAttribute("alertMessage", "Invalid User Id or Password.");
            model.addAttribute("loginForm", new LoginForm(userId, ""));
            return "login";
        }

        UserSessionEntity session = sessionOpt.get();
        ZoneOffset sessionOffset = authService.getSessionZoneOffset();
        Instant expiresAt = session.getExpireDt().atOffset(sessionOffset).toInstant();
        String token = jwtProvider.issueToken(session.getUserId(), session.getSessionId(), expiresAt);
        authCookie.setAuthCookie(response, token);
        log.info("Login success: redirecting to dashboard userId={}", session.getUserId());
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(
            @RequestParam(value = "reason", required = false) String reason,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (reason == null || reason.isBlank()) {
            reason = "logout";
        }
        log.info("Logout request reason={}", reason);
        revokeIfPresent(request, reason);
        authCookie.clearAuthCookie(response);
        return "redirect:/login?reason=" + reason;
    }

    @GetMapping("/logout")
    public String logoutGet(
            @RequestParam(value = "reason", required = false) String reason,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return logout(reason, request, response);
    }

    private void revokeIfPresent(HttpServletRequest request, String revokeReason) {
        String token = authCookie.readToken(request);
        if (token == null || token.isBlank()) {
            return;
        }
        AuthTokenClaims claims;
        try {
            claims = jwtProvider.parseAndValidate(token);
        } catch (Exception e) {
            log.debug("Logout: failed to parse token for revoke reason={}", revokeReason);
            return;
        }
        authService.findSession(claims.sessionId()).ifPresent(s -> authService.revokeSession(s, revokeReason));
    }

    private static void applyLoginAlert(String reason, Model model) {
        if (reason == null || reason.isBlank()) {
            return;
        }
        String alertType = "warning";
        String message = "Please login.";
        switch (reason) {
            case "idle_timeout" -> message = "Session timed out! Please login to continue.";
            case "expired" -> message = "Session expired. Please login again.";
            case "invalid" -> message = "Your session is invalid. Please login again.";
            case "multi_login" -> message = "Multiple login detected. Your session is invalidated.";
            case "auth_required" -> message = "Please login to continue.";
            case "logout" -> message = "You have been logged out.";
        }
        model.addAttribute("alertType", alertType);
        model.addAttribute("alertMessage", message);
    }
}
