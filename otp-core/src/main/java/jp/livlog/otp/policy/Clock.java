package jp.livlog.otp.policy;

import java.time.Instant;

@FunctionalInterface
public interface Clock {
    Instant now();

    static Clock systemUTC() {
        return Instant::now;
    }
}
