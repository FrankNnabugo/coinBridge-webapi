package com.example.paymentApi.event.wallet;

import com.example.paymentApi.wallets.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WalletEventListener {
    private final WalletService walletService;

    public WalletEventListener(WalletService walletService){
        this.walletService = walletService;
    }

    @Async
    @EventListener
    public void handleWalletCreatedEvent(WalletCreatedEvent event){
        walletService.createWallet(event.getCircleWalletResponse(),
                event.getUserId());

    }
}
