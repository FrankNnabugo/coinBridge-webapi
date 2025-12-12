package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.wallets.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WalletCreationEventListener {
    private final WalletService walletService;

    public WalletCreationEventListener(WalletService walletService){
        this.walletService = walletService;
    }


    @Async
    @EventListener
    public void handleWalletCreatedEvent(WalletCreationEvent event){
        walletService.createWallet(event.getCircleWalletResponse(),
                event.getUserId());

    }
}
