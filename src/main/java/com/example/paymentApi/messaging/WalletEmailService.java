package com.example.paymentApi.messaging;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class WalletEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public WalletEmailService(JavaMailSender mailSender, TemplateEngine templateEngine){
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

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

        } catch (Exception e) {
            throw new RuntimeException("failed to send wallet address", e);
        }


    }
}
