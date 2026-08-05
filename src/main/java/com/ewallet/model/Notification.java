package com.ewallet.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Notification {

    private Long notificationId;
    private Long walletId;
    private String title;
    private String message;
    private Integer isRead;
    private Timestamp createdAt;

    public Notification() {
    }

    // Insert Constructor
    public Notification(Long walletId,
                        String title,
                        String message) {

        this.walletId = walletId;
        this.title = title;
        this.message = message;
    }

    // Full Constructor
    public Notification(Long notificationId,
                        Long walletId,
                        String title,
                        String message,
                        Integer isRead,
                        Timestamp createdAt) {

        this.notificationId = notificationId;
        this.walletId = walletId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", walletId=" + walletId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }
}