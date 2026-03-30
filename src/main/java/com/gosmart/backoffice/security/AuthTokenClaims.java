package com.gosmart.backoffice.security;

import java.time.Instant;

public record AuthTokenClaims(
        String userId,
        String sessionId,
        Instant expiresAt
) {
}
