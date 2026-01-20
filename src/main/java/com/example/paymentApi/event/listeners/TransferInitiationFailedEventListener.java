package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.transfer.TransferInitiationFailedEvent;
import com.example.paymentApi.event.transfer.TransferInitiationFailedPublisher;
import com.example.paymentApi.walletToWallet.outbound.OutBoundRequest;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryService;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferInitiationFailedEventListener {

    private final TransferInitiationFailedPublisher transferInitiationFailedPublisher;
    private final OutboundRetryService outboundRetryService;
    private final OutboundRetryWorker outboundRetryWorker;

    @EventListener
    public void handleTransferInitiationFailedEvent(TransferInitiationFailedEvent event){

        try{
            outboundRetryService.createPaymentRetryRecord(event.getUserId());
            outboundRetryWorker.retryPaymentInitiation(event.getOutBoundRequest(), event.getUserId());
        }
        catch (Exception e) {
           log.error("Error occurred {}", e.getMessage());
        }

    }

    public void handleTransferInitiationPermanentlyFailed(){
        
    }
}
