package com.ewallet.model;

import java.util.Objects;

public class TransactionStatus {

    private Long transactionStatusId;
    private String statusName;

    public TransactionStatus() {
    }

    // Insert Constructor
    public TransactionStatus(String statusName) {
        this.statusName = statusName;
    }

    // Full Constructor
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