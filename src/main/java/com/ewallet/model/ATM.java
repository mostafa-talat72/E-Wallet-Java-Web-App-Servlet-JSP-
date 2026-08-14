package com.ewallet.model;


import java.sql.Timestamp;
import java.util.Objects;

/**
 * Maps to the {@code atms} table. Represents a cash machine where wallet users
 * can perform deposit/withdrawal operations without logging in. {@code mapX}
 * and {@code mapY} place the ATM on the machine-locator map page.
 */
public class ATM {

    // Identity fields
    private Long atmId;
    private String atmName;
    private String atmLocation;

    // Status and lifecycle fields
    private Integer status;
    private Timestamp createdAt;

    // Map coordinates used by the ATM locator page
    private Double mapX;
    private Double mapY;

    public ATM() {
    }

    /**
     * Creates a new ATM (INSERT) from its name, location and initial status.
     */
    public ATM(String atmName, String atmLocation, Integer status) {
        this.atmName = atmName;
        this.atmLocation = atmLocation;
        this.status = status;
    }

    /**
     * Full constructor used when an ATM row is read back from the database,
     * including its map coordinates and creation timestamp.
     */
    public ATM(Long atmId, String atmName, String atmLocation,
               Integer status, Timestamp createdAt, Double mapX, Double mapY) {
        this.atmId = atmId;
        this.atmName = atmName;
        this.atmLocation = atmLocation;
        this.status = status;
        this.createdAt = createdAt;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public Double getMapX() {
        return mapX;
    }

    public void setMapX(Double mapX) {
        this.mapX = mapX;
    }

    public Double getMapY() {
        return mapY;
    }

    public void setMapY(Double mapY) {
        this.mapY = mapY;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public String getAtmName() {
        return atmName;
    }

    public void setAtmName(String atmName) {
        this.atmName = atmName;
    }

    public String getAtmLocation() {
        return atmLocation;
    }

    public void setAtmLocation(String atmLocation) {
        this.atmLocation = atmLocation;
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
        return "ATM{" +
                "atmId=" + atmId +
                ", atmName='" + atmName + '\'' +
                ", atmLocation='" + atmLocation + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ATM)) return false;
        ATM atm = (ATM) o;
        return Objects.equals(atmId, atm.atmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atmId);
    }
}