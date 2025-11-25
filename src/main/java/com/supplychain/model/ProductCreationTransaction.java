package com.supplychain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a transaction for creating a new product in the supply chain.
 * This transaction type is used when a supplier creates a new product.
 * 
 * Requirements: 9.2, 9.4, 4.1, 4.4
 */
public class ProductCreationTransaction implements Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private String supplierId;
    private String productId;
    private String productName;
    private String productDescription;
    private String origin;
    
    /**
     * Default constructor
     */
    public ProductCreationTransaction() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param transactionId Unique identifier for the transaction
     * @param supplierId ID of the supplier creating the product
     * @param productId ID of the product being created
     * @param productName Name of the product
     * @param productDescription Description of the product
     * @param origin Origin location of the product
     */
    public ProductCreationTransaction(String transactionId, String supplierId, 
                                     String productId, String productName, 
                                     String productDescription, String origin) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.supplierId = supplierId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.origin = origin;
    }
    
    /**
     * Full constructor with timestamp
     */
    public ProductCreationTransaction(String transactionId, LocalDateTime timestamp,
                                     String supplierId, String productId, 
                                     String productName, String productDescription, 
                                     String origin) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.supplierId = supplierId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.origin = origin;
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    
    @Override
    public String getTransactionType() {
        return "PRODUCT_CREATION";
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public Map<String, Object> getTransactionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("supplierId", supplierId);
        data.put("productId", productId);
        data.put("productName", productName);
        data.put("productDescription", productDescription);
        data.put("origin", origin);
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
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return false;
        }
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }
        if (origin == null || origin.trim().isEmpty()) {
            return false;
        }
        // productDescription can be empty but not null
        if (productDescription == null) {
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
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCreationTransaction that = (ProductCreationTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        return "ProductCreationTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", supplierId='" + supplierId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }
}
