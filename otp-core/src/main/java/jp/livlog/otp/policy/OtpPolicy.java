package jp.livlog.otp.policy;

public record OtpPolicy(
        int digits,          // 6
        int ttlSeconds,      // 300
        int maxAttempts,     // 5
        int maxResends,      // 3
        int minResendIntervalSeconds // 30 (連打防止)
) {
    public static OtpPolicy defaultPolicy() {
        return new OtpPolicy(6, 300, 5, 3, 30);
    }
}
