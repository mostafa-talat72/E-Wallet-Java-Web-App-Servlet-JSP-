package com.ewallet.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Wallet {

    private Long walletId;
    private String phoneNumber;
    private String nationalId;
    private String fullName;
    private String pinHash;
    private String salt;
    private Integer status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Wallet() {
    }
    
    // login Constructor
    public Wallet(String phoneNumber, String pinHash) {
    		this.phoneNumber = phoneNumber;
    		this.pinHash = pinHash;
    }

    // Insert Constructor
    public Wallet(String phoneNumber, String nationalId,String fullName,
				  String pinHash, String salt) {
		this.phoneNumber = phoneNumber;
		this.nationalId = nationalId;
        this.fullName = fullName;
		this.pinHash = pinHash;
		this.salt = salt;
	}
    
    // Update pinHash Constructor
    public Wallet(Long walletId, String fullName,  String pinHash, String salt) {
		this.walletId = walletId;
		this.fullName = fullName;
		this.pinHash = pinHash;
		this.salt = salt;
	}
    
    
    
    // Full Constructor
    public Wallet(Long walletId, String phoneNumber, String nationalId,String fullName,
                  String pinHash, String salt, Integer status, Timestamp created_at, Timestamp updatedAt) {
        this.walletId = walletId;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.fullName = fullName;
        this.pinHash = pinHash;
        this.salt = salt;
        this.status = status;
        this.createdAt = created_at;
        this.updatedAt = updatedAt;
    }

    public Long getWalletId() {
        return walletId;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

 

    public String getNationalId() {
        return nationalId;
    }


    public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
    public String getPinHash() {
        return pinHash;
    }

    public String getSalt() {
        return salt;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +                
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