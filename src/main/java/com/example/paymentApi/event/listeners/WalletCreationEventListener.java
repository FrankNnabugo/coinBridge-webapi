package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.ledgers.AccountService;
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
    private final AccountService accountService;

    @Async
    @EventListener
    public void handleWalletCreatedEvent(WalletCreationEvent event) {

        accountService.createAccount();
        walletService.createWallet(event.getCircleWalletResponse(), event.getUserId());
        log.info("Wallet successfully created for user {}", event.getUserId());


    }
}
