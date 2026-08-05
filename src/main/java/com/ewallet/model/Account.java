package com.ewallet.model;

import java.util.Objects;

public class Account {

    private Long accountId;
    private Long accountTypeId;
    private Long referenceId;
    private Integer status;

    public Account() {
    }

    // Insert Constructor
    public Account(Long accountTypeId, Long referenceId, Integer status) {
        this.accountTypeId = accountTypeId;
        this.referenceId = referenceId;
        this.status = status;
    }

    // Full Constructor
    public Account(Long accountId, Long accountTypeId,
                   Long referenceId, Integer status) {
        this.accountId = accountId;
        this.accountTypeId = accountTypeId;
        this.referenceId = referenceId;
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", accountTypeId=" + accountTypeId +
                ", referenceId=" + referenceId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}