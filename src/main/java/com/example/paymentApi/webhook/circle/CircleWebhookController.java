package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.walletToWallet.inbound.InboundTransferService;
import com.example.paymentApi.walletToWallet.outbound.OutBoundTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/circle")
@Slf4j
public class CircleWebhookController {

    private final CircleWebhookService circleWebhookService;
    private final InboundTransferService inboundTransferService;
    private final OutBoundTransferService outBoundTransferService;

    public CircleWebhookController(CircleWebhookService circleWebhookService, InboundTransferService inboundTransferService,
                                   OutBoundTransferService outBoundTransferService) {
        this.circleWebhookService = circleWebhookService;
        this.inboundTransferService = inboundTransferService;
        this.outBoundTransferService = outBoundTransferService;
    }


    @PostMapping("/pay-in")
    public ResponseEntity<CircleInboundWebhookResponse> handleInboundTransfer(@RequestBody String rawPayload,

                                                                              @RequestHeader("X-Circle-Signature") String signatureBase64)
    {
        log.info("Received webhook response from circle and verifying signature");

       if(!circleWebhookService.verifySignature(rawPayload, signatureBase64)){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
       };
        log.info("Signature successfully verified, processing webhook response for USDC wallet deposit {}", rawPayload);
        try {
           inboundTransferService.processInboundTransfer(rawPayload);
        }
        catch (Exception e) {
           log.error("Inbound transfer processing failed", e);
        }

        //log.info("Processed webhook response and returning 200 to provider");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/finalize/transfer")
    public ResponseEntity<CircleOutBoundWebhookResponse> handleOutboundTransfer(@RequestBody String rawPayload,
                                                                                @RequestHeader("X-Circle-Signature") String signatureBase64
                                                                                ){
        if(!circleWebhookService.verifySignature(rawPayload, signatureBase64)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };

        log.info("Received webhook response from circle for USDC wallet to wallet transfer {}", rawPayload);

        outBoundTransferService.finalizeTransfer(rawPayload);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //TODO: define other circle webHooks
}
