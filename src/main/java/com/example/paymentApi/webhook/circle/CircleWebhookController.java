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
                                                                              @RequestHeader("X-Circle-Signature") String signatureBase64) {
        circleWebhookService.verifySignature(rawPayload, signatureBase64);
        inboundTransferService.processInboundTransfer(rawPayload);

        log.info("Received webhook response from circle for USDC wallet deposit {}", rawPayload);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/finalize/transfer")
    public ResponseEntity<CircleOutBoundWebhookResponse> handleOutboundTransfer(@RequestBody String rawPayload,
                                                                                @RequestHeader("X-Circle-Signature") String signatureBase64
                                                                                ){
        circleWebhookService.verifySignature(rawPayload, signatureBase64);
        outBoundTransferService.finalizeTransfer(rawPayload);
        log.info("Received webhook response from circle for USDC wallet to wallet transfer {}", rawPayload);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //TODO: define other circle webHooks
}
