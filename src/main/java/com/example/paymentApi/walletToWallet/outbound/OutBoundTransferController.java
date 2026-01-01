package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/initiate/transfer")
@RequiredArgsConstructor
public class OutBoundTransferController{

    private final OutBoundTransferService outBoundTransferService;

    @PostMapping
    public ResponseEntity<String> initiateTransfer(@Valid @RequestBody OutBoundRequest outBoundRequest,
                                                   @PathVariable("id") String id,
                                                    OutboundTransferInitiationResponse response
                                                  ){
        return ResponseEntity.ok(outBoundTransferService.initiateTransfer(outBoundRequest, id, response));
    }
}
