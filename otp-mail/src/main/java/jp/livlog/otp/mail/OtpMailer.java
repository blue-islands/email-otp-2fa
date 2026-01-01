package jp.livlog.otp.mail;

public interface OtpMailer {
    void send(OtpMailRequest request);
}
