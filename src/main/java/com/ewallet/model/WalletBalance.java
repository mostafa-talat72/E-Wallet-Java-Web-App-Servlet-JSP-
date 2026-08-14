package com.ewallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Maps to the {@code wallet_balances} table (one row per wallet). Holds the
 * wallet funds split into an available balance that can be spent and a held
 * balance that is temporarily reserved for pending transactions.
 */
public class WalletBalance {

    // Funds fields: available (spendable) and held (reserved) amounts
    private Long walletId;
    private BigDecimal availableBalance;
    private BigDecimal heldBalance;
    private Timestamp updatedAt;

    /** Default no-arg constructor. */
    public WalletBalance() {
    }

    /**
     * Creates a balance row for a new wallet before the opening amounts are
     * known; zero balances are supplied by the persistence layer.
     */
    public WalletBalance(Long walletId) {
		this.walletId = walletId;
	}
    
    /**
     * Creates a wallet balance from its two fund buckets, e.g. when the
     * balance is initialized or refreshed after a transaction.
     */
    public WalletBalance(Long walletId, BigDecimal availableBalance, BigDecimal heldBalance) {
        this.walletId = walletId;
        this.availableBalance = availableBalance;
        this.heldBalance = heldBalance;
    }

    /**
     * Full constructor used when a complete balance row is read back from the
     * database, including the last-updated timestamp.
     */
    public WalletBalance(Long walletId, BigDecimal availableBalance,
                         BigDecimal heldBalance, Timestamp updatedAt) {
        this.walletId = walletId;
        this.availableBalance = availableBalance;
        this.heldBalance = heldBalance;
        this.updatedAt = updatedAt;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getHeldBalance() {
        return heldBalance;
    }

    public void setHeldBalance(BigDecimal heldBalance) {
        this.heldBalance = heldBalance;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WalletBalance{" +
                "walletId=" + walletId +
                ", availableBalance=" + availableBalance +
                ", heldBalance=" + heldBalance +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletBalance)) return false;
        WalletBalance that = (WalletBalance) o;
        return Objects.equals(walletId, that.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }
}