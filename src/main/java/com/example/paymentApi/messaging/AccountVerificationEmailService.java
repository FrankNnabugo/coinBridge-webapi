package com.example.paymentApi.messaging;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class AccountVerificationEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public AccountVerificationEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    @Retryable(maxAttempts = 3, retryFor = Exception.class,
            backoff = @Backoff(delay = 2000,
                    multiplier = 2.0))
    @Async
    public void sendOtpEmail(String toEmail, String otpCode, long expiryTime) {
        try {

            Context context = new Context();

            context.setVariable("otp", otpCode);
            context.setVariable("expiryTime", expiryTime);

            String htmlContent = templateEngine.process("account-verification-email.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Account verification Code");
            helper.setText(htmlContent, true); // 'true' enables HTML content

            mailSender.send(message);

        }
        catch (MessagingException e)
        {

            log.info("Failed to send OTP email", e);
        }
        catch (Exception e) {
            log.info("Error sending Otp {}" , e.getMessage());
        }
    }
}
