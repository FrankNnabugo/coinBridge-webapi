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

        walletService.createWallet(event.getCircleWalletResponse(), event.getId());
        log.info("Wallet successfully created for user {}", event.getId());

        /**
         * This would create a default account balance record per account.
         * used for computing ledger balance
         * Each wallet in the system gets a corresponding reconciliation account
         */
        accountService.createAccountForWallet(event.getId());
        log.info("Wallet_account and account_balance successfully created for wallet {}",
                event.getCircleWalletResponse().getId());


    }
}
