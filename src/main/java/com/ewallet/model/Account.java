package com.ewallet.model;

import java.util.Objects;

/**
 * Maps to the {@code accounts} table. A polymorphic account: the
 * {@code accountTypeId} discriminates the kind of account (e.g. wallet or ATM)
 * and {@code referenceId} points to the owning entity's primary key.
 */
public class Account {

    // Identity / classification fields
    private Long accountId;
    private int accountTypeId;
    private Long referenceId;
    private Integer status;

    public Account() {
    }

    /**
     * Creates a new account row (INSERT): the account type and the referenced
     * entity (wallet or ATM id) are supplied; status defaults in the database.
     */
    public Account(int accountTypeId, Long referenceId) {
        this.accountTypeId = accountTypeId;
        this.referenceId = referenceId;
    }

    /**
     * Full constructor used when an account row is read back from the
     * database, including its status.
     */
    public Account(Long accountId, int accountTypeId,
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

    public int getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(int accountTypeId) {
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