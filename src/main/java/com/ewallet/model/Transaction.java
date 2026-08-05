package com.ewallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class Transaction {

    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long transactionTypeId;
    private Long transactionStatusId;

    private BigDecimal amount;
    private BigDecimal fees;

    private String referenceNumber;
    private String description;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Transaction() {
    }

    // Insert Constructor
    public Transaction(Long fromAccountId, Long toAccountId,
                       Long transactionTypeId, Long transactionStatusId,
                       BigDecimal amount, BigDecimal fees,
                       String referenceNumber, String description) {

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transactionTypeId = transactionTypeId;
        this.transactionStatusId = transactionStatusId;
        this.amount = amount;
        this.fees = fees;
        this.referenceNumber = referenceNumber;
        this.description = description;
    }

    // Full Constructor
    public Transaction(Long transactionId, Long fromAccountId,
                       Long toAccountId, Long transactionTypeId,
                       Long transactionStatusId, BigDecimal amount,
                       BigDecimal fees, String referenceNumber,
                       String description, Timestamp createdAt,
                       Timestamp updatedAt) {

        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transactionTypeId = transactionTypeId;
        this.transactionStatusId = transactionStatusId;
        this.amount = amount;
        this.fees = fees;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public Long getTransactionTypeId() { return transactionTypeId; }
    public void setTransactionTypeId(Long transactionTypeId) { this.transactionTypeId = transactionTypeId; }

    public Long getTransactionStatusId() { return transactionStatusId; }
    public void setTransactionStatusId(Long transactionStatusId) { this.transactionStatusId = transactionStatusId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFees() { return fees; }
    public void setFees(BigDecimal fees) { this.fees = fees; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", amount=" + amount +
                ", fees=" + fees +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}