package jp.livlog.otp.model;

import java.time.Instant;

public record OtpChallenge(
        String challengeId,
        String userId,
        String otpHash,
        Instant expiresAt,
        int attempts,
        int resends,
        Instant lastSentAt,
        Status status
) {
    public enum Status { PENDING, VERIFIED, EXPIRED, LOCKED }
}
