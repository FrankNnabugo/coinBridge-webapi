package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/legders")
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/{id}")
    public ApiResponse<LedgerResponse> getLedger(@PathVariable("id") String id){
        LedgerResponse response = ledgerService.getLedger(id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }
}
