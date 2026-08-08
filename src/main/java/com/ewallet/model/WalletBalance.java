package com.ewallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class WalletBalance {

    private Long walletId;
    private BigDecimal availableBalance;
    private BigDecimal heldBalance;
    private Timestamp updatedAt;

    public WalletBalance() {
    }

    // Insert Constructor
    public WalletBalance(Long walletId) {
		this.walletId = walletId;
	}
    
    public WalletBalance(Long walletId, BigDecimal availableBalance, BigDecimal heldBalance) {
        this.walletId = walletId;
        this.availableBalance = availableBalance;
        this.heldBalance = heldBalance;
    }

    // Full Constructor
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