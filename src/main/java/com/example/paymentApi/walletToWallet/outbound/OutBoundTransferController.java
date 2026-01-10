package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/initiate/transfer")
@RequiredArgsConstructor
public class OutBoundTransferController{

    private final OutBoundTransferService outBoundTransferService;


    @PostMapping("/{id}")
    public ApiResponse<String> initiateTransfer(@RequestBody @Valid OutBoundRequest outBoundRequest,
                                                @PathVariable("id") String id){
        log.info("Received request with body {}", outBoundRequest);

        String response = outBoundTransferService.initiateTransfer(outBoundRequest, id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
}
