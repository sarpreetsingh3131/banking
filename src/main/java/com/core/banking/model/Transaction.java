package com.core.banking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

import com.core.banking.exception.InvalidAmountException;
import com.core.banking.exception.InvalidCurrencyException;

public final class Transaction {

    private final UUID id;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private BigDecimal amount;
    private String currency;
    public enum Status {SUCCESS, FAIL}
    private Status status;
    private final Timestamp createdAt;

    public Transaction(UUID id, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String currency, Timestamp createdAt) throws InvalidAmountException, InvalidCurrencyException {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        setAmount(amount);
        setCurrency(currency);
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getTargetAccountId() {
        return targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return Objects.isNull(status) ? Status.FAIL : status;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    private void setAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(String.format("Transaction amount (%s) is invalid", amount));
        }
        this.amount = amount;
    }

    private void setCurrency(String currency) throws InvalidCurrencyException {
        try {
            this.currency = Currency.getInstance(currency).getCurrencyCode();
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyException(String.format("Transaction currency (%s) is invalid", currency));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
