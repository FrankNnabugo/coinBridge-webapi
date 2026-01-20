package com.example.paymentApi.shared.mapper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class TypeMapper {

    public static BigDecimal convertToBigDecimal(String[] amounts) {
        if (amounts == null || amounts.length == 0) {
            throw new IllegalArgumentException("Amounts cannot be null or empty");
        }

        return new BigDecimal(amounts[0]);
    }
}
