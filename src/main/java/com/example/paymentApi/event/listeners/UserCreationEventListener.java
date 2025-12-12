package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.user.UserCreationEvent;
import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.event.wallet.WalletCreationFailedEvent;
import com.example.paymentApi.event.wallet.WalletEventPublisher;
import com.example.paymentApi.circle.CircleWalletService;
import com.example.paymentApi.shared.exception.GeneralAppException;
import com.example.paymentApi.users.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCreationEventListener {

    private final CircleWalletService circleWalletService;
    private final WalletEventPublisher walletEventPublisher;

    public UserCreationEventListener(CircleWalletService circleWalletService,
                                     WalletEventPublisher walletEventPublisher){
        this.circleWalletService = circleWalletService;
        this.walletEventPublisher = walletEventPublisher;


    }


    @Async
    @EventListener
    public void handleUserCreatedEvent(UserCreationEvent event){
        try {

            circleWalletService.createCircleWallet(event.getUserId())

                    .doOnSuccess(CircleResponse -> {
                        WalletCreationEvent walletEvent = new WalletCreationEvent(
                                CircleResponse, event.getUserId());
                        walletEventPublisher.publishWalletCreatedEvent(walletEvent);
                    })


                    .doOnError(error -> {
                        WalletCreationFailedEvent failedEvent = new WalletCreationFailedEvent(event.getUserId());
                        walletEventPublisher.publishWalletCreationFailed(failedEvent);
                    })
                    .subscribe();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
