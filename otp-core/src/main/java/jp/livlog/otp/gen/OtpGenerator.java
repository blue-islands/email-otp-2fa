package jp.livlog.otp.gen;

import java.security.SecureRandom;

public class OtpGenerator {
    private static final SecureRandom RND = new SecureRandom();

    public String generate(int digits) {
        if (digits < 4 || digits > 10) {
            throw new IllegalArgumentException("digits must be between 4 and 10");
        }
        int bound = (int) Math.pow(10, digits);
        int code = RND.nextInt(bound);
        return String.format("%0" + digits + "d", code);
    }
}
