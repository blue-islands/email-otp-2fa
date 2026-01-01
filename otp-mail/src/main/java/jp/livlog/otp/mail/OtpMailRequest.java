package jp.livlog.otp.mail;

import java.util.Objects;

public record OtpMailRequest(
        String to,
        String subject,
        String textBody,
        String htmlBody
) {
    public OtpMailRequest {
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(subject, "subject");
        if ((textBody == null || textBody.isBlank()) && (htmlBody == null || htmlBody.isBlank())) {
            throw new IllegalArgumentException("Either textBody or htmlBody must be provided.");
        }
    }

    public static OtpMailRequest text(String to, String subject, String textBody) {
        return new OtpMailRequest(to, subject, textBody, null);
    }
}
