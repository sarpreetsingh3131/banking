package com.core.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

public final class Account {

    private final UUID id;
    private BigDecimal balance;
    private final String currency;
    private final Timestamp createdAt;

    public Account(UUID id, BigDecimal balance, String currency, Timestamp createdAt) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public Timestamp getCreatedAt() {
        return getCreatedAt(ZoneId.systemDefault());
    }

    public Timestamp getCreatedAt(ZoneId zoneId) {
        return Timestamp.valueOf(createdAt.toLocalDateTime().atZone(zoneId).toLocalDateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
