package jp.livlog.otp.sample.web;

import jakarta.servlet.http.HttpSession;
import jp.livlog.otp.web.spring.OtpWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MfaController {

    @GetMapping("/mfa")
    public String mfa(HttpSession session) {
        if (!OtpWebSupport.isPasswordOk(session)) {
            return "redirect:/login";
        }
        return "mfa";
    }
}
