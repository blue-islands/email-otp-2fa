package jp.livlog.otp.web.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "otp.web")
public class OtpWebProperties {

    /** MFA画面のパス（アプリ側で用意：GET） */
    private String mfaPagePath = "/mfa";

    /** OTP送信（POST） */
    private String startPath = "/mfa/start";

    /** OTP検証（POST） */
    private String verifyPath = "/mfa/verify";

    /** 成功時リダイレクト */
    private String successRedirect = "/app";

    /** 失敗時リダイレクト */
    private String failureRedirect = "/mfa?error=1";

    /** 保護対象URL prefix */
    private List<String> protectedPathPrefixes = List.of("/app");

    /** アプリ名（メール件名など） */
    private String appName = "YourApp";

    // getters/setters
    public String getMfaPagePath() { return mfaPagePath; }
    public void setMfaPagePath(String mfaPagePath) { this.mfaPagePath = mfaPagePath; }
    public String getStartPath() { return startPath; }
    public void setStartPath(String startPath) { this.startPath = startPath; }
    public String getVerifyPath() { return verifyPath; }
    public void setVerifyPath(String verifyPath) { this.verifyPath = verifyPath; }
    public String getSuccessRedirect() { return successRedirect; }
    public void setSuccessRedirect(String successRedirect) { this.successRedirect = successRedirect; }
    public String getFailureRedirect() { return failureRedirect; }
    public void setFailureRedirect(String failureRedirect) { this.failureRedirect = failureRedirect; }
    public List<String> getProtectedPathPrefixes() { return protectedPathPrefixes; }
    public void setProtectedPathPrefixes(List<String> protectedPathPrefixes) { this.protectedPathPrefixes = protectedPathPrefixes; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
}
