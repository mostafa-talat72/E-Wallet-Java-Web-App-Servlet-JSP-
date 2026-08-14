package com.ewallet.model;


import java.util.Objects;

/**
 * Maps to the {@code transaction_types} table. Lookup entity describing what
 * kind of operation a transaction performs (e.g. send money, ATM deposit or
 * ATM withdrawal).
 */
public class TransactionType {

    // Lookup fields
    private Long transactionTypeId;
    private String typeName;

    public TransactionType() {
    }

    /**
     * Creates a new transaction type (INSERT) from its display name.
     */
    public TransactionType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Full constructor used when a transaction type row is read back from the
     * database.
     */
    public TransactionType(Long transactionTypeId, String typeName) {
        this.transactionTypeId = transactionTypeId;
        this.typeName = typeName;
    }

    public Long getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TransactionType{" +
                "transactionTypeId=" + transactionTypeId +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionType)) return false;
        TransactionType that = (TransactionType) o;
        return Objects.equals(transactionTypeId, that.transactionTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionTypeId);
    }
}