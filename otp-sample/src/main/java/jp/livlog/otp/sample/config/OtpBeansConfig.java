package jp.livlog.otp.sample.config;

import jp.livlog.otp.mail.OtpMailer;
import jp.livlog.otp.mail.SmtpConfig;
import jp.livlog.otp.mail.simple.SimpleJavaMailOtpMailer;
import jp.livlog.otp.storage.OtpChallengeStore;
import jp.livlog.otp.storage.jdbc.JdbcOtpChallengeStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OtpBeansConfig {

    @Bean
    public OtpChallengeStore otpChallengeStore(DataSource ds) {
        return new JdbcOtpChallengeStore(ds);
    }

    @Bean
    public OtpMailer otpMailer() {
        // ★ 実際にメールを飛ばしたい場合はここを変更
        return new SimpleJavaMailOtpMailer(
                new SmtpConfig(
                        "smtp.example.com",
                        587,
                        "user",
                        "password",
                        SmtpConfig.Transport.STARTTLS,
                        "no-reply@example.com",
                        "OTP Sample",
                        10000,
                        10000,
                        10000
                )
        );
    }
}
