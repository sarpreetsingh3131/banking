package com.core.banking.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.banking.dto.TransactionDto;
import com.core.banking.exception.AccountNotFoundException;
import com.core.banking.exception.InvalidAmountException;
import com.core.banking.exception.InvalidCurrencyException;
import com.core.banking.exception.InsufficientBalanceException;
import com.core.banking.exception.SameAccountException;
import com.core.banking.model.Account;
import com.core.banking.model.Transaction;

@Service
public class TransactionService {

    private final Map<UUID, Transaction> transactions;
    private final AccountService accountService;
    private final MockedCurrencyConversionService mockedCurrencyConversionService;

    @Autowired
    public TransactionService(Map<UUID, Transaction> transactions, AccountService accountService, MockedCurrencyConversionService mockedCurrencyConversionService) {
        this.transactions = transactions;
        this.accountService = accountService;
        this.mockedCurrencyConversionService = mockedCurrencyConversionService;
    }

    public Collection<Transaction> findAll() {
        return transactions.values();
    }

    public void transfer(TransactionDto dto) throws AccountNotFoundException, InsufficientBalanceException, InvalidAmountException, InvalidCurrencyException, SameAccountException {
        Transaction transaction = new Transaction(UUID.randomUUID(), dto.getSourceAccountId(), dto.getTargetAccountId(), dto.getAmount(), dto.getCurrency(), Timestamp.from(Instant.now()));
        transactions.put(transaction.getId(), transaction);

        Account source = accountService.findById(transaction.getSourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException("source account not found"));

        Account target = accountService.findById(transaction.getTargetAccountId())
                .orElseThrow(() -> new AccountNotFoundException("target account not found"));

        if (source.getId().equals(target.getId())) {
            throw new SameAccountException("source and target accounts are same");
        }

        BigDecimal sourceConversionAmount = mockedCurrencyConversionService.convert(transaction.getAmount(), transaction.getCurrency(), source.getCurrency());

        if (source.getBalance().compareTo(sourceConversionAmount) < 0) {
            throw new InsufficientBalanceException("insufficient balance in source account");
        }

        source.setBalance(source.getBalance().subtract(sourceConversionAmount));

        BigDecimal targetConversionAmount = mockedCurrencyConversionService.convert(transaction.getAmount(), transaction.getCurrency(), target.getCurrency());
        target.setBalance(target.getBalance().add(targetConversionAmount));

        transaction.setStatus(Transaction.Status.SUCCESS);
    }
}
