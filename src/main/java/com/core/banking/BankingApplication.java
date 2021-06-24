package com.core.banking;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.core.banking.model.Account;
import com.core.banking.model.Transaction;

@SpringBootApplication
public class BankingApplication {

    public static final UUID SOURCE_ACCOUNT_ID = UUID.fromString("2a91ddbe-8d9a-4029-8192-400ac0ca066d");
    public static final UUID TARGET_ACCOUNT_ID = UUID.fromString("0ff18d2e-db78-4555-8352-fbea3a2004d1");

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @Bean
    public Map<UUID, Account> getAccounts() {
        return Stream.of(
                new Account(SOURCE_ACCOUNT_ID, BigDecimal.valueOf(100), "GBP", Timestamp.from(Instant.now())),
                new Account(TARGET_ACCOUNT_ID, BigDecimal.valueOf(200), "GBP", Timestamp.from(Instant.now())))
                .peek(System.out::println)
                .collect(Collectors.toMap(Account::getId, account -> account));
    }

    @Bean
    public Map<UUID, Transaction> getTransactions() {
        return new HashMap<>();
    }
}
