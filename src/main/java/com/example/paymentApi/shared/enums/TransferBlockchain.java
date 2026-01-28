package com.example.paymentApi.shared.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransferBlockchain {
    @JsonProperty("MATIC-AMOY")
    MATIC_AMOY,
    MATIC,
    ETH
}
