package com.ewallet.model;

import java.util.Objects;

/**
 * Maps to the {@code transaction_status} table. Lookup entity describing the
 * lifecycle state of a transaction (e.g. pending, succeeded or failed).
 */
public class TransactionStatus {

    // Lookup fields
    private Long transactionStatusId;
    private String statusName;

    public TransactionStatus() {
    }

    /**
     * Creates a new transaction status (INSERT) from its display name.
     */
    public TransactionStatus(String statusName) {
        this.statusName = statusName;
    }

    /**
     * Full constructor used when a transaction status row is read back from
     * the database.
     */
    public TransactionStatus(Long transactionStatusId, String statusName) {
        this.transactionStatusId = transactionStatusId;
        this.statusName = statusName;
    }

    public Long getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(Long transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "TransactionStatus{" +
                "transactionStatusId=" + transactionStatusId +
                ", statusName='" + statusName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionStatus)) return false;
        TransactionStatus that = (TransactionStatus) o;
        return Objects.equals(transactionStatusId, that.transactionStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionStatusId);
    }
}