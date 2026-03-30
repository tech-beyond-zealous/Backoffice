package com.gosmart.backoffice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class PasswordHashUtil {
    private PasswordHashUtil() {
    }

    public static String produceHashedPwd(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return null;
        }
        String sha1Base64 = base64(sha("SHA-1", password.getBytes(StandardCharsets.UTF_8)));
        String combined = usernameOrEmail + sha1Base64;
        return base64(sha("SHA-256", combined.getBytes(StandardCharsets.UTF_8)));
    }

    private static byte[] sha(String algorithm, byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(input);
        } catch (Exception e) {
            throw new IllegalStateException("Hash failed: " + algorithm, e);
        }
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
