package com.example.paymentApi.walletTransaction;

import com.example.paymentApi.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;


    @PostMapping("initiate/{userId}")
    public ApiResponse<TransferResponse> initiateTransfer(@RequestBody @Valid TransferRequest transferRequest,
                                                          @PathVariable("userId") String userId){
        log.info("Received request with body {}", transferRequest);

        TransferResponse response = transferService.initiateTransfer(transferRequest, userId);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
}
