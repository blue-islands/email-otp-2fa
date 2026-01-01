package jp.livlog.otp.hash;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class OtpHasher {
    private static final SecureRandom RND = new SecureRandom();

    // パラメータは固定（互換性維持のため）
    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;

    /**
     * format: v1:pbkdf2-sha256:iter:saltB64:dkB64
     */
    public String hash(String otp) {
        if (otp == null || otp.isBlank()) throw new IllegalArgumentException("otp is blank");
        byte[] salt = new byte[SALT_BYTES];
        RND.nextBytes(salt);
        byte[] dk = pbkdf2(otp.toCharArray(), salt, ITERATIONS, KEY_BITS);

        return "v1:pbkdf2-sha256:" + ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(dk);
    }

    public boolean verify(String otp, String stored) {
        if (otp == null || stored == null) return false;

        String[] p = stored.split(":");
        if (p.length != 5) return false;
        if (!"v1".equals(p[0])) return false;
        if (!"pbkdf2-sha256".equals(p[1])) return false;

        int iter;
        try {
            iter = Integer.parseInt(p[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(p[3]);
            expected = Base64.getDecoder().decode(p[4]);
        } catch (IllegalArgumentException e) {
            return false;
        }

        byte[] actual = pbkdf2(otp.toCharArray(), salt, iter, expected.length * 8);
        return constantTimeEquals(expected, actual);
    }

    private byte[] pbkdf2(char[] pw, byte[] salt, int iter, int bits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pw, salt, iter, bits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 failed", e);
        }
    }

    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
        return r == 0;
    }
}
