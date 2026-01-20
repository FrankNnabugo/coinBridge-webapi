package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getTransaction(@PathVariable("id") String id){
        TransactionResponse response = transactionService.getTransaction(id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @GetMapping
    public ApiResponse<TransactionResponse> getAllTransactions(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "5") int pageSize,
                                                  @RequestParam(required = false) String field ){
        TransactionResponse response = transactionService.getAllTransactions(offset, pageSize, field);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);

    }
}
