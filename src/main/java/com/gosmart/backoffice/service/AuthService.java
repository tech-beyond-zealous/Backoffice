package com.gosmart.backoffice.service;

import com.gosmart.backoffice.config.AuthProperties;
import com.gosmart.backoffice.domain.UserPasswordEntity;
import com.gosmart.backoffice.domain.UserSessionEntity;
import com.gosmart.backoffice.repo.UserPasswordRepository;
import com.gosmart.backoffice.repo.UserSessionRepository;
import com.gosmart.backoffice.util.PasswordHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    public static final String REVOKE_REASON_LOGOUT = "logout";
    public static final String REVOKE_REASON_MULTI_LOGIN = "multi_login";

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthProperties authProperties;
    private final UserPasswordRepository userPasswordRepository;
    private final UserSessionRepository userSessionRepository;
    private final ZoneOffset sessionZoneOffset = ZoneOffset.ofHours(8);

    public AuthService(
            AuthProperties authProperties,
            UserPasswordRepository userPasswordRepository,
            UserSessionRepository userSessionRepository
    ) {
        this.authProperties = authProperties;
        this.userPasswordRepository = userPasswordRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Transactional
    public Optional<UserSessionEntity> authenticateAndCreateSession(String userId, String rawPassword, HttpServletRequest request) {
        if (userId == null || rawPassword == null) {
            log.info("Login rejected: missing userId or password");
            return Optional.empty();
        }
        userId = userId.trim();
        Optional<UserPasswordEntity> pwdOpt = userPasswordRepository.findByUserId(userId);
        if (pwdOpt.isEmpty()) {
            log.info("Login failed: user not found userId={}", userId);
            return Optional.empty();
        }

        String expectedHash = pwdOpt.get().getHashedPwd();
        String actualHash = PasswordHashUtil.produceHashedPwd(userId, rawPassword);
        if (expectedHash == null || actualHash == null || !expectedHash.equals(actualHash)) {
            log.info("Login failed: invalid password userId={}", userId);
            return Optional.empty();
        }

        LocalDateTime now = LocalDateTime.now(sessionZoneOffset);
        LocalDateTime expireAt = now.plusMinutes(authProperties.getIdleTimeoutMinutes());

        int revoked = userSessionRepository.revokeActiveSessionsByUserId(userId, now, REVOKE_REASON_MULTI_LOGIN);
        if (revoked > 0) {
            log.info("Revoked {} existing session(s) due to multi-login userId={}", revoked, userId);
        }

        UserSessionEntity session = new UserSessionEntity();
        session.setCreateDt(now);
        session.setLastActivityDt(now);
        session.setExpireDt(expireAt);
        session.setRevokeDt(null);
        session.setRevokeReason(null);
        session.setUserId(userId);
        session.setSessionId(UUID.randomUUID().toString());
        session.setIpAddress(request.getRemoteAddr());
        session.setUserAgent(request.getHeader("User-Agent"));

        UserSessionEntity saved = userSessionRepository.save(session);
        log.info("Login success: session created userId={} sessionId={}", userId, saved.getSessionId());
        return Optional.of(saved);
    }

    public Optional<UserSessionEntity> findSession(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        return userSessionRepository.findBySessionId(sessionId);
    }

    public UserSessionEntity touchSession(UserSessionEntity session) {
        LocalDateTime now = LocalDateTime.now(sessionZoneOffset);
        session.setLastActivityDt(now);
        session.setExpireDt(now.plusMinutes(authProperties.getIdleTimeoutMinutes()));
        return userSessionRepository.save(session);
    }

    public void revokeSession(UserSessionEntity session) {
        revokeSession(session, REVOKE_REASON_LOGOUT);
    }

    public void revokeSession(UserSessionEntity session, String revokeReason) {
        session.setRevokeDt(LocalDateTime.now(sessionZoneOffset));
        session.setRevokeReason(revokeReason);
        UserSessionEntity saved = userSessionRepository.save(session);
        log.info("Session revoked userId={} sessionId={} reason={}", saved.getUserId(), saved.getSessionId(), revokeReason);
    }

    public ZoneOffset getSessionZoneOffset() {
        return sessionZoneOffset;
    }
}
