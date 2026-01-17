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
public class OnboardingEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public OnboardingEmailService(JavaMailSender mailSender, TemplateEngine templateEngine){
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Retryable(maxAttempts = 3, retryFor = Exception.class,
            backoff = @Backoff(delay = 2000,
                    multiplier = 2.0))
    @Async
    public void sendWalletInfo(String email, String walletAddress){
        try {
            Context context = new Context();
            context.setVariable("walletAddress", walletAddress);

            String htmlContent = templateEngine.process("wallet-email.html", context);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your CoinBridge Wallet address");
            helper.setText(htmlContent, true); // 'true' enables HTML content

            mailSender.send(message);

        } catch (MessagingException e) {

            log.info("Failed to send email", e);
        }
        catch (Exception e) {

            log.info("Error sending mail {}" , e.getMessage());
        }


    }
}
