package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.messaging.OnboardingEmailService;
import com.example.paymentApi.wallets.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WalletCreationEventListener {
    private final WalletService walletService;
    private final OnboardingEmailService onboardingEmailService;

    @Async
    @EventListener
    public void handleWalletCreatedEvent(WalletCreationEvent event) {

        walletService.createWallet(event.getCircleWalletResponse(), event.getUserId());
        log.info("Wallet successfully created for user {}", event.getUserId());

        onboardingEmailService.sendWalletInfo(event.getEmail(), event.getCircleWalletResponse().getAddress());
        log.info("onboarding mail sent to user with wallet address {} ", event.getCircleWalletResponse().getAddress());

    }
}
