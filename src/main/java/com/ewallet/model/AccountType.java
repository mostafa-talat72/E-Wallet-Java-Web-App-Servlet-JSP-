package com.ewallet.model;


import java.util.Objects;

public class AccountType {

    private Long accountTypeId;
    private String typeName;

    public AccountType() {
    }

    // Insert Constructor
    public AccountType(String typeName) {
        this.typeName = typeName;
    }

    // Full Constructor
    public AccountType(Long accountTypeId, String typeName) {
        this.accountTypeId = accountTypeId;
        this.typeName = typeName;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "AccountType{" +
                "accountTypeId=" + accountTypeId +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountType)) return false;
        AccountType that = (AccountType) o;
        return Objects.equals(accountTypeId, that.accountTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountTypeId);
    }
}