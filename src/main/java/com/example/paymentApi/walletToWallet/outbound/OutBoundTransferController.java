package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfer/initiate")
@Slf4j
@RequiredArgsConstructor
public class OutBoundTransferController{

    private final OutBoundTransferService outBoundTransferService;


    @PostMapping("/{userId}")
    public ApiResponse<String> initiateTransfer(@RequestBody @Valid OutBoundRequest outBoundRequest,
                                                @PathVariable("userId") String userId){
        log.info("Received request with body {}", outBoundRequest);

        String response = outBoundTransferService.initiateTransfer(outBoundRequest, userId);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
}
