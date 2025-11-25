package com.supplychain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a transaction for transferring a product between parties in the supply chain.
 * This transaction type is used when a product moves from one location/party to another.
 * 
 * Requirements: 9.2, 9.4, 4.1, 4.4
 */
public class ProductTransferTransaction implements Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private String fromParty;
    private String toParty;
    private String productId;
    private String fromLocation;
    private String toLocation;
    private ProductStatus newStatus;
    
    /**
     * Default constructor
     */
    public ProductTransferTransaction() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param transactionId Unique identifier for the transaction
     * @param fromParty ID of the party transferring the product
     * @param toParty ID of the party receiving the product
     * @param productId ID of the product being transferred
     * @param fromLocation Current location of the product
     * @param toLocation Destination location of the product
     * @param newStatus New status of the product after transfer
     */
    public ProductTransferTransaction(String transactionId, String fromParty, 
                                     String toParty, String productId, 
                                     String fromLocation, String toLocation,
                                     ProductStatus newStatus) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.fromParty = fromParty;
        this.toParty = toParty;
        this.productId = productId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.newStatus = newStatus;
    }
    
    /**
     * Full constructor with timestamp
     */
    public ProductTransferTransaction(String transactionId, LocalDateTime timestamp,
                                     String fromParty, String toParty, 
                                     String productId, String fromLocation, 
                                     String toLocation, ProductStatus newStatus) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.fromParty = fromParty;
        this.toParty = toParty;
        this.productId = productId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.newStatus = newStatus;
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    
    @Override
    public String getTransactionType() {
        return "PRODUCT_TRANSFER";
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public Map<String, Object> getTransactionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("fromParty", fromParty);
        data.put("toParty", toParty);
        data.put("productId", productId);
        data.put("fromLocation", fromLocation);
        data.put("toLocation", toLocation);
        data.put("newStatus", newStatus != null ? newStatus.toString() : null);
        return data;
    }
    
    /**
     * Validates that the transaction has all required fields.
     * Requirements: 4.1, 4.4 - Transaction validation and completeness
     * 
     * @return true if all required fields are present and valid, false otherwise
     */
    @Override
    public boolean validate() {
        // Check that all required fields are non-null and non-empty
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return false;
        }
        if (timestamp == null) {
            return false;
        }
        if (fromParty == null || fromParty.trim().isEmpty()) {
            return false;
        }
        if (toParty == null || toParty.trim().isEmpty()) {
            return false;
        }
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            return false;
        }
        if (toLocation == null || toLocation.trim().isEmpty()) {
            return false;
        }
        if (newStatus == null) {
            return false;
        }
        return true;
    }
    
    // Getters and Setters
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFromParty() {
        return fromParty;
    }
    
    public void setFromParty(String fromParty) {
        this.fromParty = fromParty;
    }
    
    public String getToParty() {
        return toParty;
    }
    
    public void setToParty(String toParty) {
        this.toParty = toParty;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getFromLocation() {
        return fromLocation;
    }
    
    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }
    
    public String getToLocation() {
        return toLocation;
    }
    
    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }
    
    public ProductStatus getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(ProductStatus newStatus) {
        this.newStatus = newStatus;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductTransferTransaction that = (ProductTransferTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        return "ProductTransferTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", fromParty='" + fromParty + '\'' +
                ", toParty='" + toParty + '\'' +
                ", productId='" + productId + '\'' +
                ", fromLocation='" + fromLocation + '\'' +
                ", toLocation='" + toLocation + '\'' +
                ", newStatus=" + newStatus +
                '}';
    }
}
