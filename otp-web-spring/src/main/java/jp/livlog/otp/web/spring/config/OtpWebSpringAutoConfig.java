package jp.livlog.otp.web.spring.config;

import jp.livlog.otp.policy.Clock;
import jp.livlog.otp.policy.OtpPolicy;
import jp.livlog.otp.storage.OtpChallengeStore;
import jp.livlog.otp.mail.OtpMailer;
import jp.livlog.otp.web.spring.OtpWebProperties;
import jp.livlog.otp.web.spring.filter.MfaEnforcerFilter;
import jp.livlog.otp.web.spring.mail.DefaultOtpMailTemplate;
import jp.livlog.otp.web.spring.mail.OtpMailTemplate;
import jp.livlog.otp.web.spring.service.SpringEmailOtp2faService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OtpWebProperties.class)
public class OtpWebSpringAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock otpClock() {
        return Clock.systemUTC();
    }

    @Bean
    @ConditionalOnMissingBean
    public OtpPolicy otpPolicy() {
        return OtpPolicy.defaultPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public OtpMailTemplate otpMailTemplate() {
        return new DefaultOtpMailTemplate();
    }

    @Bean
    public SpringEmailOtp2faService springEmailOtp2faService(
            OtpWebProperties props,
            OtpPolicy policy,
            Clock clock,
            OtpChallengeStore store,
            OtpMailer mailer,
            OtpMailTemplate template
    ) {
        return new SpringEmailOtp2faService(
                props.getAppName(),
                policy,
                clock,
                store,
                mailer,
                template
        );
    }

    @Bean
    public FilterRegistrationBean<MfaEnforcerFilter> mfaEnforcerFilter(OtpWebProperties props) {
        FilterRegistrationBean<MfaEnforcerFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new MfaEnforcerFilter(props));
        bean.addUrlPatterns("/*");
        bean.setOrder(10);
        return bean;
    }
}
