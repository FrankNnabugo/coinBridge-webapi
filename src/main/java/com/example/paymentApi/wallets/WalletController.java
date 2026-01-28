package com.example.paymentApi.wallets;

import com.example.paymentApi.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "wallet", description = "Read Wallet")
public class WalletController {

    private final WalletService walletService;


    @Operation(summary = "fetch wallet")
    @GetMapping("/{id}")
    public ApiResponse<WalletResponse> getWallet(@PathVariable("id") String id){
        WalletResponse response = walletService.getWallet(id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }

    @Operation(summary = "fetch all wallets")
    @GetMapping
    public ApiResponse<WalletListResponse> getAllWallets(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size){
        WalletListResponse responses = walletService.getAllWallets(page, size);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), responses);
    }

    @Operation(summary = "get wallet balance")
    @GetMapping("/balance/{id}")
    public ApiResponse<WalletBalanceResponse> getWalletBalance(@PathVariable("id")String id){
        WalletBalanceResponse response = walletService.getWalletBalance(id);
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), response);
    }
}
