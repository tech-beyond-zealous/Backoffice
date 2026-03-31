package com.gosmart.backoffice.web.interceptor;

import com.gosmart.backoffice.domain.UserSessionEntity;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.repo.UserFunctionRepository;
import com.gosmart.backoffice.security.AuthCookie;
import com.gosmart.backoffice.security.AuthTokenClaims;
import com.gosmart.backoffice.security.JwtProvider;
import com.gosmart.backoffice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String REQ_ATTR_USER_ID = "authUserId";
    public static final String REQ_ATTR_PERMISSION = "authPermission";
    public static final String REQ_ATTR_FUNCTION_CODE = "authFunctionCode";

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private final AuthCookie authCookie;
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final UserFunctionRepository userFunctionRepository;

    public AuthInterceptor(
            AuthCookie authCookie,
            JwtProvider jwtProvider,
            AuthService authService,
            UserFunctionRepository userFunctionRepository
    ) {
        this.authCookie = authCookie;
        this.jwtProvider = jwtProvider;
        this.authService = authService;
        this.userFunctionRepository = userFunctionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = authCookie.readToken(request);
        if (token == null || token.isBlank()) {
            log.debug("Auth blocked: missing token path={}", request.getRequestURI());
            response.sendRedirect("/login?reason=idle_timeout");
            return false;
        }

        AuthTokenClaims claims;
        try {
            claims = jwtProvider.parseAndValidate(token);
        } catch (Exception e) {
            log.info("Auth blocked: invalid token path={}", request.getRequestURI());
            response.sendRedirect("/login?reason=invalid");
            return false;
        }

        Instant now = Instant.now();
        if (claims.expiresAt().isBefore(now)) {
            log.info("Auth blocked: token expired userId={} path={}", claims.userId(), request.getRequestURI());
            response.sendRedirect("/login?reason=expired");
            return false;
        }

        Optional<UserSessionEntity> sessionOpt = authService.findSession(claims.sessionId());
        if (sessionOpt.isEmpty()) {
            log.info("Auth blocked: session not found userId={} path={}", claims.userId(), request.getRequestURI());
            response.sendRedirect("/login?reason=invalid");
            return false;
        }

        UserSessionEntity session = sessionOpt.get();
        if (session.getRevokeDt() != null) {
            if (AuthService.REVOKE_REASON_MULTI_LOGIN.equals(session.getRevokeReason())) {
                log.info("Auth blocked: session revoked (multi_login) userId={} path={}", session.getUserId(), request.getRequestURI());
                response.sendRedirect("/login?reason=multi_login");
            } else {
                log.info("Auth blocked: session revoked userId={} reason={} path={}", session.getUserId(), session.getRevokeReason(), request.getRequestURI());
                response.sendRedirect("/login?reason=logout");
            }
            return false;
        }
        ZoneOffset sessionOffset = authService.getSessionZoneOffset();
        LocalDateTime nowLocal = LocalDateTime.now(sessionOffset);
        if (session.getExpireDt() == null || session.getExpireDt().isBefore(nowLocal)) {
            log.info("Auth blocked: idle timeout userId={} path={}", session.getUserId(), request.getRequestURI());
            response.sendRedirect("/login?reason=idle_timeout");
            return false;
        }
        if (!claims.userId().equals(session.getUserId())) {
            log.info("Auth blocked: subject mismatch tokenUserId={} sessionUserId={} path={}", claims.userId(), session.getUserId(), request.getRequestURI());
            response.sendRedirect("/login?reason=invalid");
            return false;
        }

        UserSessionEntity updatedSession = authService.touchSession(session);
        Instant rotatedExpiresAt = updatedSession.getExpireDt().atOffset(sessionOffset).toInstant();
        String rotatedToken = jwtProvider.issueToken(updatedSession.getUserId(), updatedSession.getSessionId(), rotatedExpiresAt);
        authCookie.setAuthCookie(response, rotatedToken);

        request.setAttribute(REQ_ATTR_USER_ID, updatedSession.getUserId());
        if (!applyPermission(request, response, updatedSession.getUserId())) {
            return false;
        }
        log.debug("Auth ok userId={} path={}", updatedSession.getUserId(), request.getRequestURI());
        return true;
    }

    private boolean applyPermission(HttpServletRequest request, HttpServletResponse response, String userId) throws Exception {
        String functionIdStr = request.getParameter("functionId");
        Optional<UserFunctionRepository.FunctionPermissionRow> rowOpt;
        if (functionIdStr != null && !functionIdStr.isBlank()) {
            try {
                Long functionId = Long.parseLong(functionIdStr.trim());
                rowOpt = userFunctionRepository.findFunctionPermissionRowByFunctionId(userId, functionId);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        } else {
            String path = request.getRequestURI();
            rowOpt = userFunctionRepository.findFunctionPermissionRow(userId, path);
        }

        if (rowOpt.isEmpty()) {
            request.setAttribute(REQ_ATTR_PERMISSION, null);
            request.setAttribute(REQ_ATTR_FUNCTION_CODE, null);
            return true;
        }

        UserFunctionRepository.FunctionPermissionRow row = rowOpt.get();
        request.setAttribute(REQ_ATTR_FUNCTION_CODE, row.getFunctionCode());
        if (row.getCanView() == null || !isYes(row.getCanView())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        UserFunctionPermission permission = new UserFunctionPermission(
                isYes(row.getCanCreate()),
                isYes(row.getCanEdit()),
                isYes(row.getCanDelete()),
                true
        );
        request.setAttribute(REQ_ATTR_PERMISSION, permission);
        return true;
    }

    private static boolean isYes(String value) {
        return value != null && "Y".equals(value.trim().toUpperCase(Locale.ROOT));
    }
}
