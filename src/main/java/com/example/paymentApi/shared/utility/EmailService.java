package com.example.paymentApi.shared.utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService{
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final GeneralLogger logger;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, GeneralLogger logger) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.logger = logger;
    }

    @Async
    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendOtpEmail(String toEmail, String otpCode, String expiryTime) {
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
            logger.log(e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
