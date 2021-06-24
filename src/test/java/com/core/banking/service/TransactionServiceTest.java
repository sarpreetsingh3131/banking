package com.core.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.core.banking.dto.TransactionDto;
import com.core.banking.exception.AccountNotFoundException;
import com.core.banking.exception.InsufficientBalanceException;
import com.core.banking.exception.InvalidAmountException;
import com.core.banking.exception.InvalidCurrencyException;
import com.core.banking.exception.SameAccountException;
import com.core.banking.model.Account;
import com.core.banking.model.Transaction;

class TransactionServiceTest {

    Map<UUID, Transaction> transactions = new HashMap<>();

    AccountService accountService = Mockito.mock(AccountService.class);

    MockedCurrencyConversionService mockedCurrencyConversionService = Mockito.mock(MockedCurrencyConversionService.class);

    TransactionService sut;

    @BeforeEach
    void setUp() {
        sut = new TransactionService(transactions, accountService, mockedCurrencyConversionService);
    }

    @Test
    void shouldTransferSuccessfully() throws InvalidAmountException, InvalidCurrencyException, InsufficientBalanceException, AccountNotFoundException, SameAccountException {
        // given
        Account source = new Account(UUID.randomUUID(), new BigDecimal(100), "GBP", Timestamp.from(Instant.now()));
        Account target = new Account(UUID.randomUUID(), new BigDecimal(200), "GBP", Timestamp.from(Instant.now()));
        TransactionDto dto = new TransactionDto(source.getId(), target.getId(), new BigDecimal(100), "GBP");

        Mockito.when(accountService.findById(source.getId())).thenReturn(Optional.of(source));
        Mockito.when(accountService.findById(target.getId())).thenReturn(Optional.of(target));
        Mockito.when(mockedCurrencyConversionService.convert(dto.getAmount(), dto.getCurrency(), source.getCurrency())).thenReturn(dto.getAmount());
        Mockito.when(mockedCurrencyConversionService.convert(dto.getAmount(), dto.getCurrency(), target.getCurrency())).thenReturn(dto.getAmount());

        // when
        sut.transfer(dto);

        // then
        assertEquals(new BigDecimal(0), source.getBalance());
        assertEquals(new BigDecimal(300), target.getBalance());
        Optional<Transaction> transaction = transactions.values().stream().findFirst();
        assertTrue(transaction.isPresent());
        assertEquals(dto.getSourceAccountId(), transaction.get().getSourceAccountId());
        assertEquals(dto.getTargetAccountId(), transaction.get().getTargetAccountId());
        assertEquals(dto.getAmount(), transaction.get().getAmount());
        assertEquals(dto.getCurrency(), transaction.get().getCurrency());
        assertEquals(Transaction.Status.SUCCESS, transaction.get().getStatus());
        assertNotNull(transaction.get().getCreatedAt());
        assertNotNull(transaction.get().getId());
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountNotFound() {
        // given
        UUID sourceAccountId = UUID.randomUUID();
        Account target = new Account(UUID.randomUUID(), new BigDecimal(200), "GBP", Timestamp.from(Instant.now()));
        TransactionDto dto = new TransactionDto(sourceAccountId, target.getId(), new BigDecimal(100), "GBP");

        Mockito.when(accountService.findById(sourceAccountId)).thenReturn(Optional.empty());
        Mockito.when(accountService.findById(target.getId())).thenReturn(Optional.of(target));

        // when
        assertThrows(AccountNotFoundException.class, () -> sut.transfer(dto));

        // then
        assertEquals(new BigDecimal(200), target.getBalance());
        Optional<Transaction> transaction = transactions.values().stream().findFirst();
        assertTrue(transaction.isPresent());
        assertEquals(dto.getSourceAccountId(), transaction.get().getSourceAccountId());
        assertEquals(dto.getTargetAccountId(), transaction.get().getTargetAccountId());
        assertEquals(dto.getAmount(), transaction.get().getAmount());
        assertEquals(dto.getCurrency(), transaction.get().getCurrency());
        assertEquals(Transaction.Status.FAIL, transaction.get().getStatus());
        assertNotNull(transaction.get().getCreatedAt());
        assertNotNull(transaction.get().getId());
    }

    @Test
    void shouldThrowExceptionWhenTargetAccountNotFound() {
        // given
        Account source = new Account(UUID.randomUUID(), new BigDecimal(200), "GBP", Timestamp.from(Instant.now()));
        UUID targetAccountId = UUID.randomUUID();
        TransactionDto dto = new TransactionDto(source.getId(), targetAccountId, new BigDecimal(100), "GBP");

        Mockito.when(accountService.findById(source.getId())).thenReturn(Optional.of(source));
        Mockito.when(accountService.findById(targetAccountId)).thenReturn(Optional.empty());

        // when
        assertThrows(AccountNotFoundException.class, () -> sut.transfer(dto));

        // then
        assertEquals(new BigDecimal(200), source.getBalance());
        Optional<Transaction> transaction = transactions.values().stream().findFirst();
        assertTrue(transaction.isPresent());
        assertEquals(dto.getSourceAccountId(), transaction.get().getSourceAccountId());
        assertEquals(dto.getTargetAccountId(), transaction.get().getTargetAccountId());
        assertEquals(dto.getAmount(), transaction.get().getAmount());
        assertEquals(dto.getCurrency(), transaction.get().getCurrency());
        assertEquals(Transaction.Status.FAIL, transaction.get().getStatus());
        assertNotNull(transaction.get().getCreatedAt());
        assertNotNull(transaction.get().getId());
    }

    @Test
    void shouldThrowExceptionWhenAccountsAreSame() {
        // given
        Account source = new Account(UUID.randomUUID(), new BigDecimal(200), "GBP", Timestamp.from(Instant.now()));
        TransactionDto dto = new TransactionDto(source.getId(), source.getId(), new BigDecimal(100), "GBP");

        Mockito.when(accountService.findById(source.getId())).thenReturn(Optional.of(source));

        // when
        assertThrows(SameAccountException.class, () -> sut.transfer(dto));

        // then
        assertEquals(new BigDecimal(200), source.getBalance());
        Optional<Transaction> transaction = transactions.values().stream().findFirst();
        assertTrue(transaction.isPresent());
        assertEquals(dto.getSourceAccountId(), transaction.get().getSourceAccountId());
        assertEquals(dto.getTargetAccountId(), transaction.get().getTargetAccountId());
        assertEquals(dto.getAmount(), transaction.get().getAmount());
        assertEquals(dto.getCurrency(), transaction.get().getCurrency());
        assertEquals(Transaction.Status.FAIL, transaction.get().getStatus());
        assertNotNull(transaction.get().getCreatedAt());
        assertNotNull(transaction.get().getId());
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountHasInsufficientBalance() {
        // given
        Account source = new Account(UUID.randomUUID(), new BigDecimal(100), "GBP", Timestamp.from(Instant.now()));
        Account target = new Account(UUID.randomUUID(), new BigDecimal(200), "GBP", Timestamp.from(Instant.now()));
        TransactionDto dto = new TransactionDto(source.getId(), target.getId(), new BigDecimal(500), "GBP");

        Mockito.when(accountService.findById(source.getId())).thenReturn(Optional.of(source));
        Mockito.when(accountService.findById(target.getId())).thenReturn(Optional.of(target));
        Mockito.when(mockedCurrencyConversionService.convert(dto.getAmount(), dto.getCurrency(), source.getCurrency())).thenReturn(dto.getAmount());
        Mockito.when(mockedCurrencyConversionService.convert(dto.getAmount(), dto.getCurrency(), target.getCurrency())).thenReturn(dto.getAmount());

        // when
        assertThrows(InsufficientBalanceException.class, () -> sut.transfer(dto));

        // then
        assertEquals(new BigDecimal(100), source.getBalance());
        assertEquals(new BigDecimal(200), target.getBalance());
        Optional<Transaction> transaction = transactions.values().stream().findFirst();
        assertTrue(transaction.isPresent());
        assertEquals(dto.getSourceAccountId(), transaction.get().getSourceAccountId());
        assertEquals(dto.getTargetAccountId(), transaction.get().getTargetAccountId());
        assertEquals(dto.getAmount(), transaction.get().getAmount());
        assertEquals(dto.getCurrency(), transaction.get().getCurrency());
        assertEquals(Transaction.Status.FAIL, transaction.get().getStatus());
        assertNotNull(transaction.get().getCreatedAt());
        assertNotNull(transaction.get().getId());
    }

    @Test
    void shouldFindAndReturnAllTransactions() throws InvalidAmountException, InvalidCurrencyException {
        // given
        Transaction transaction1 = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(50), "GBP", Timestamp.from(Instant.now()));
        Transaction transaction2 = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(20), "GBP", Timestamp.from(Instant.now()));
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);
        Collection<Transaction> expected = transactions.values();

        // when
        Collection<Transaction> actual = sut.findAll();

        // then
        assertEquals(expected, actual);
    }
}