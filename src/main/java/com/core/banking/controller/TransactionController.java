package com.core.banking.controller;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.banking.dto.TransactionDto;
import com.core.banking.exception.AccountNotFoundException;
import com.core.banking.exception.InsufficientBalanceException;
import com.core.banking.exception.InvalidAmountException;
import com.core.banking.exception.InvalidCurrencyException;
import com.core.banking.exception.SameAccountException;
import com.core.banking.model.Transaction;
import com.core.banking.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Map.Entry<String, String> transfer(@RequestBody TransactionDto dto) throws InvalidAmountException, AccountNotFoundException, InsufficientBalanceException, InvalidCurrencyException, SameAccountException {
        transactionService.transfer(dto);
        return new AbstractMap.SimpleEntry<>("message", "transaction succeeded");
    }

    @GetMapping
    public Collection<Transaction> findAll() {
        return transactionService.findAll();
    }

    @ExceptionHandler(value = {InvalidAmountException.class, InvalidCurrencyException.class, HttpMessageNotReadableException.class, NullPointerException.class, InsufficientBalanceException.class, SameAccountException.class})
    public ResponseEntity<Object> handleClientException(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AbstractMap.SimpleEntry<>("error", exception.getMessage()));
    }

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AbstractMap.SimpleEntry<>("error", exception.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleServerException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AbstractMap.SimpleEntry<>("error", exception.getMessage()));
    }
}
