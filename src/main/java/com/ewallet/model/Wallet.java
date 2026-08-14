package com.ewallet.model;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Maps to the {@code wallets} table. This is the primary user entity of the
 * E-Wallet domain: a wallet is identified by its phone number and national ID,
 * owns its balance, linked cards and ATM accounts, and is authenticated
 * against a salted PIN hash.
 */
public class Wallet {

    // Identity fields
    private Long walletId;
    private String phoneNumber;
    private String nationalId;
    private String fullName;

    // Security fields: salted PIN hash used to authenticate the wallet owner
    private String pinHash;
    private String salt;

    // Status and lifecycle fields
    private Integer status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /** Default no-arg constructor. */
    public Wallet() {
    }
    
    /**
     * Creates a wallet for the login flow: {@code pinHash} temporarily holds
     * the raw PIN typed by the user and is hashed during authentication.
     */
    public Wallet(String phoneNumber, String pinHash) {
    		this.phoneNumber = phoneNumber;
    		this.pinHash = pinHash;
    }

    /**
     * Creates a new wallet for the signup flow (INSERT): the caller hashes the
     * PIN with the freshly generated salt before persisting this object.
     */
    public Wallet(String phoneNumber, String nationalId,String fullName,
				  String pinHash, String salt) {
		this.phoneNumber = phoneNumber;
		this.nationalId = nationalId;
        this.fullName = fullName;
		this.pinHash = pinHash;
		this.salt = salt;
	}
    
    /**
     * Creates a wallet carrying only the fields needed for the change-PIN
     * flow: a brand new salt is generated so the stored PIN hash is replaced.
     */
    public Wallet(Long walletId, String fullName,  String pinHash, String salt) {
		this.walletId = walletId;
		this.fullName = fullName;
		this.pinHash = pinHash;
		this.salt = salt;
	}
    
    
    
    /**
     * Full constructor used when a complete wallet row is read back from the
     * database, including its status and lifecycle timestamps.
     */
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