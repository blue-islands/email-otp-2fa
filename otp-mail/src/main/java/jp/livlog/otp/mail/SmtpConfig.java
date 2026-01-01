package jp.livlog.otp.mail;

public record SmtpConfig(
        String host,
        int port,
        String username,
        String password,
        Transport transport,
        String fromAddress,
        String fromName,
        Integer connectionTimeoutMs,
        Integer readTimeoutMs,
        Integer writeTimeoutMs
) {
    public enum Transport { PLAIN, STARTTLS, SMTPS }
}
