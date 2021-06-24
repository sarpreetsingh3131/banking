package com.core.banking.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class MockedCurrencyConversionService {

    public BigDecimal convert(BigDecimal amount, String currencyFrom, String currencyTo) {
        return amount;
    }
}
