package com.ewallet.model;


import java.util.Objects;

/**
 * Maps to the {@code account_types} table. Lookup entity that classifies
 * accounts into kinds (e.g. wallet account, ATM account); its id is stored on
 * each {@link Account} row as the type discriminator.
 */
public class AccountType {

    // Lookup fields
    private Long accountTypeId;
    private String typeName;

    public AccountType() {
    }

    /**
     * Creates a new account type (INSERT) from its display name.
     */
    public AccountType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Full constructor used when an account type row is read back from the
     * database.
     */
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