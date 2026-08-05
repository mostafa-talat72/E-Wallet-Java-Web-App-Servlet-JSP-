package com.ewallet.model;


import java.util.Objects;

public class TransactionType {

    private Long transactionTypeId;
    private String typeName;

    public TransactionType() {
    }

    // Insert Constructor
    public TransactionType(String typeName) {
        this.typeName = typeName;
    }

    // Full Constructor
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