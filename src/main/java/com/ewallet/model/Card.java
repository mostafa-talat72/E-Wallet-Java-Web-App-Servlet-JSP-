package com.ewallet.model;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Maps to the {@code cards} table. Represents a bank card linked to a wallet;
 * it is used to fund cash operations (e.g. ATM withdrawals). The card number
 * and CVV are stored as text so they can be masked on the UI.
 */
public class Card {

    // Identity fields
    private Long cardId;
    private Long walletId;

    // Card details
    private String cardNumber;
    private String cardName;
    private String cardHolderName;
    private String bankName;

    // Expiry and card security code
    private Date expireDate;
    private String cvv;

    // Status and lifecycle fields
    private Integer status;
    private Timestamp createdAt;

    public Card() {
    }

    /**
     * Creates a new card for the add-card flow (INSERT). The card is linked to
     * its owner wallet via {@code walletId}; status and creation timestamp are
     * set by the database.
     */
    public Card(Long walletId, String cardNumber, String cardName,
                String bankName, String cardHolderName, Date expireDate,
                String cvv) {
        this.walletId = walletId;
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.bankName = bankName;
        this.cardHolderName = cardHolderName;
        this.expireDate = expireDate;
        this.cvv = cvv;
    }

    /**
     * Full constructor used when a card row is read back from the database,
     * including its status and creation timestamp.
     */
    public Card(Long cardId, Long walletId, String cardNumber,
                String cardName, String cardHolderName, String bankName,
                Date expireDate, String cvv,
                Integer status, Timestamp createdAt) {
        this.cardId = cardId;
        this.walletId = walletId;
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.cardHolderName = cardHolderName;
        this.bankName = bankName;
        this.expireDate = expireDate;
        this.cvv = cvv;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardName) {
        this.cardName = cardName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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
    
    

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", walletId=" + walletId +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardName='" + cardName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", expireDate=" + expireDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(cardId, card.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }
}