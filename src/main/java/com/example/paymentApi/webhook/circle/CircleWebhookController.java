package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.settlement.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/circle")
@RequiredArgsConstructor
@Slf4j
public class CircleWebhookController {

    private final CircleWebhookService circleWebhookService;
    private final SettlementService settlementService;


    @PostMapping("/inbound/transaction")
    public ResponseEntity<CircleInboundWebhookResponse> handleInboundTransactions(@RequestBody String payload,

                                                                                  @RequestHeader("X-Circle-Signature") String signatureBase64) {
        log.info("Received webhook response from circle for Inbound transaction with payload {}", payload);

        if (!circleWebhookService.verifySignature(payload, signatureBase64)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Signature successfully verified for inbound transaction");
            settlementService.settleInboundTransactions(payload);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/outbound/transaction")
    public ResponseEntity<CircleOutboundWebhookResponse> handleOutboundTransactions(@RequestBody String payload,
                                                                                    @RequestHeader("X-Circle-Signature") String signatureBase64)
    {
        log.info("Received webhook response from circle for Outbound transaction with payload {}", payload);
        if(!circleWebhookService.verifySignature(payload, signatureBase64)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Signature successfully verified for outbound transaction");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
