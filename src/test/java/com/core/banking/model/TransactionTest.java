package com.core.banking.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.core.banking.exception.InvalidAmountException;
import com.core.banking.exception.InvalidCurrencyException;

class TransactionTest {

    Transaction sut;

    @Test
    void shouldSetAndReturnAmount() throws InvalidAmountException, InvalidCurrencyException {
        // given
        BigDecimal amount = new BigDecimal(50);

        // when
        sut = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), amount, "GBP", Timestamp.from(Instant.now()));

        // then
        assertEquals(amount, sut.getAmount());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        // given, when, then
        assertThrows(InvalidAmountException.class,
                () -> sut = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(0), "GBP", Timestamp.from(Instant.now())));
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        // given, when, then
        assertThrows(InvalidAmountException.class,
                () -> sut = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(-5), "GBP", Timestamp.from(Instant.now())));
    }

    @Test
    void shouldSetAndReturnCurrency() throws InvalidAmountException, InvalidCurrencyException {
        // given
        String currency = "GBP";

        // when
        sut = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(50), currency, Timestamp.from(Instant.now()));

        // then
        assertEquals(currency, sut.getCurrency());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsInvalid() {
        // given, when, then
        assertThrows(InvalidCurrencyException.class,
                () -> sut = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(10), "GB", Timestamp.from(Instant.now())));
    }
}