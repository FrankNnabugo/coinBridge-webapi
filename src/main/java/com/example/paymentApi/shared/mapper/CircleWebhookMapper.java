package com.example.paymentApi.shared.mapper;

import com.example.paymentApi.shared.enums.CircleTransactionType;
import com.example.paymentApi.shared.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CircleWebhookMapper {

    public static TransactionType mapCircleTransactionType(CircleTransactionType type) {
        return switch (type) {
            case INBOUND -> TransactionType.INBOUND_TRANSFER;
            case OUTBOUND -> TransactionType.OUTBOUND_TRANSFER;
        };
    }

    public static BigDecimal mapCircleAmountType(List<BigDecimal> amounts) {
        return amounts.get(0);
    }

}
