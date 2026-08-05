package com.ewallet.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Wallet {

    private Long walletId;
    private String phoneNumber;
    private String nationalId;
    private String pinHash;
    private String salt;
    private Integer status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Wallet() {
    }

    // Insert Constructor
    public Wallet(String phoneNumber, String nationalId,
                  String pinHash, String salt, Integer status) {
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.pinHash = pinHash;
        this.salt = salt;
        this.status = status;
    }

    // Full Constructor
    public Wallet(Long walletId, String phoneNumber, String nationalId,
                  String pinHash, String salt, Integer status,
                  Timestamp createdAt, Timestamp updatedAt) {
        this.walletId = walletId;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.pinHash = pinHash;
        this.salt = salt;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wallet)) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(walletId, wallet.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }
}