package com.core.banking.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class TransactionDto implements Serializable {

    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final String currency;

    public TransactionDto(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String currency) {
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "sourceAccountId field is null or missing");
        this.targetAccountId = Objects.requireNonNull(targetAccountId, "targetAccountId field is null or missing");
        this.amount = Objects.requireNonNull(amount, "amount field is null or missing");
        this.currency = Objects.requireNonNull(currency, "currency field is null or missing");
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

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
