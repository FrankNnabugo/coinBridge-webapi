package com.example.paymentApi.messaging;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class OtpEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public OtpEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    @Async
    public void sendOtpEmail(String toEmail, String otpCode, long expiryTime) {
        try {

            Context context = new Context();

            context.setVariable("otp", otpCode);
            context.setVariable("expiryTime", expiryTime);

            String htmlContent = templateEngine.process("otp-email.html", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // 'true' enables HTML content

            mailSender.send(message);

        }
        catch (MessagingException e)
        {
            //retry or publish
            log.info("Failed to send OTP email", e);
        }
        catch (Exception e) {
            log.info("Error sending Otp {}" , e.getMessage());
        }
    }
}
