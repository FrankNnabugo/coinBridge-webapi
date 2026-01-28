package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.user.UserCreationEvent;
import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.event.wallet.WalletCreationFailedEvent;
import com.example.paymentApi.event.wallet.WalletEventPublisher;
import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import com.example.paymentApi.shared.exception.InternalProcessingException;
import com.example.paymentApi.wallets.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCreationEventListener {

    private final CircleWalletService circleWalletService;
    private final WalletEventPublisher walletEventPublisher;


    @Async
    @EventListener
    public void handleUserCreatedEvent(UserCreationEvent event){
        try {

            circleWalletService.createCircleWallet(event.getUserId())

                    .doOnSuccess(CircleResponse -> {
                        WalletCreationEvent walletEvent = new WalletCreationEvent(CircleResponse, event.getUserId());
                        walletEventPublisher.publishWalletCreatedEvent(walletEvent);
                    }
                    )

                    .doOnError(error -> {
                        WalletCreationFailedEvent failedEvent = new WalletCreationFailedEvent(event.getUserId());
                        walletEventPublisher.publishWalletCreationFailed(failedEvent);
                    })
                    .subscribe();
        }
        catch (Exception e)
        {
          throw new ExternalServiceException("Error occurred", e);
        }
    }

}
