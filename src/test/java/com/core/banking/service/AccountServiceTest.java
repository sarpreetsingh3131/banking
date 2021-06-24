package com.core.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.core.banking.model.Account;

class AccountServiceTest {


    Map<UUID, Account> accounts = new HashMap<>();

    AccountService sut;

    @BeforeEach
    void setUp() {
        sut = new AccountService(accounts);
    }

    @Test
    void shouldFindAndReturnAccount() {
        // given
        UUID id = UUID.randomUUID();
        Account expected = new Account(id, new BigDecimal(10), "GBP", Timestamp.from(Instant.now()));
        accounts.put(id, expected);

        // when
        Optional<Account> actual = sut.findById(id);

        //then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void shouldNotFindAndReturnEmptyOptional() {
        // given
        UUID id = UUID.randomUUID();

        // when
        Optional<Account> actual = sut.findById(id);

        //then
        assertFalse(actual.isPresent());
    }
}