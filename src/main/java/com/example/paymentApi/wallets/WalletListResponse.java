package com.example.paymentApi.wallets;

import java.util.List;

public class WalletListResponse {
    private List<WalletResponse> wallets;

    public List<WalletResponse> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletResponse> wallets) {
        this.wallets = wallets;
    }
}
