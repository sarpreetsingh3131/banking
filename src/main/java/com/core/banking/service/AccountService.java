package com.core.banking.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.core.banking.model.Account;

@Service
public class AccountService {

    private final Map<UUID, Account> accounts;

    public AccountService(Map<UUID, Account> accounts) {
        this.accounts = accounts;
    }

    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }
}
