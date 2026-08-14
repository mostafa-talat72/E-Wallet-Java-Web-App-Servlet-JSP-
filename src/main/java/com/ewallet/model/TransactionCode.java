package com.ewallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class TransactionCode {

    private Long codeId;
    private Long walletId;
    private String code;
    private BigDecimal amount;
    private Timestamp createdAt;
    private Timestamp expiresAt;
    private Integer attempts;
    private Integer isUsed;
    private Integer isExpire;


    public TransactionCode() {
    }

    // Insert Constructor
    public TransactionCode(Long walletId, String code,
                           BigDecimal amount) {
        this.walletId = walletId;
        this.code = code;
        this.amount = amount;
    }

    // Full Constructor
    public TransactionCode(Long codeId, Long walletId,
                           String code, BigDecimal amount,
                           Timestamp createdAt,
                           Timestamp expiresAt,
                           Integer attempts,
                           Integer isUsed,
                           Integer isExpire) {

        this.codeId = codeId;
        this.walletId = walletId;
        this.code = code;
        this.amount = amount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.attempts = attempts;
        this.isUsed = isUsed;
        this.isExpire = isExpire;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }
    
    public Integer getIsExpire() {
        return isExpire;
    }

    public void setIsExpire(Integer isExpire) {
        this.isExpire = isExpire;
    }

    @Override
    public String toString() {
        return "TransactionCode{" +
                "codeId=" + codeId +
                ", walletId=" + walletId +
                ", code='" + code + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", attempts=" + attempts +
                ", isUsed=" + isUsed +
                ", isExpire=" + isExpire +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionCode)) return false;
        TransactionCode that = (TransactionCode) o;
        return Objects.equals(codeId, that.codeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeId);
    }
}