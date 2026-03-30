package com.gosmart.backoffice.security;

import com.gosmart.backoffice.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final SecretKey key;

    public JwtProvider(AuthProperties authProperties) {
        byte[] secret = authProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String issueToken(String userId, String sessionId, Instant expiresAt) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(userId)
                .id(sessionId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
        log.debug(
                "JWT issued userId={} sessionId={} exp={} tokenLen={} tokenFp={}",
                userId,
                sessionId,
                expiresAt,
                token.length(),
                tokenFingerprint(token)
        );
        return token;
    }

    public AuthTokenClaims parseAndValidate(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = jws.getPayload();

            String userId = claims.getSubject();
            String sessionId = claims.getId();
            Date exp = claims.getExpiration();

            if (userId == null || sessionId == null || exp == null) {
                throw new JwtException("Missing required claims");
            }

            Instant expiresAt = exp.toInstant();
            log.debug(
                    "JWT accepted userId={} sessionId={} exp={} tokenLen={} tokenFp={}",
                    userId,
                    sessionId,
                    expiresAt,
                    token.length(),
                    tokenFingerprint(token)
            );
            return new AuthTokenClaims(userId, sessionId, expiresAt);
        } catch (JwtException e) {
            int tokenLen = token == null ? 0 : token.length();
            log.debug(
                    "JWT rejected type={} msg={} tokenLen={} tokenFp={}",
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    tokenLen,
                    tokenFingerprint(token)
            );
            throw e;
        } catch (Exception e) {
            int tokenLen = token == null ? 0 : token.length();
            log.debug("JWT rejected: invalid token tokenLen={} tokenFp={}", tokenLen, tokenFingerprint(token), e);
            throw new JwtException("Invalid token", e);
        }
    }

    private static String tokenFingerprint(String token) {
        if (token == null || token.isBlank()) {
            return "none";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return "sha256_unavailable";
        }
    }
}
