package com.example.paymentApi.event.user;

import com.example.paymentApi.event.wallet.WalletCreatedEvent;
import com.example.paymentApi.event.wallet.WalletEventPublisher;
import com.example.paymentApi.integration.CircleWalletResponse;
import com.example.paymentApi.integration.CircleWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCreatedEventListener {

    private final CircleWalletService circleWalletService;
    private final WalletEventPublisher walletEventPublisher;

    public UserCreatedEventListener(CircleWalletService circleWalletService,
                                    WalletEventPublisher walletEventPublisher){
        this.circleWalletService = circleWalletService;
        this.walletEventPublisher = walletEventPublisher;

    }
    @Async
    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event, CircleWalletResponse circleWalletResponse){
        circleWalletService.createCircleWallet(event.getUserId())
                .doOnSuccess(response-> walletEventPublisher.publishWalletCreatedEvent(circleWalletResponse, event.getUserId()))
                .doOnError(error-> log.error("failed", error))
                .subscribe();
                };

}
