package com.supplychain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a transaction for verifying a product's authenticity in the supply chain.
 * This transaction type is used when a retailer or other party verifies a product.
 * 
 * Requirements: 9.2, 9.4, 4.1, 4.4
 */
public class ProductVerificationTransaction implements Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private String verifierId;
    private String productId;
    private boolean verificationResult;
    private String verificationNotes;
    
    /**
     * Default constructor
     */
    public ProductVerificationTransaction() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param transactionId Unique identifier for the transaction
     * @param verifierId ID of the party performing the verification
     * @param productId ID of the product being verified
     * @param verificationResult Result of the verification (true = authentic, false = not authentic)
     * @param verificationNotes Additional notes about the verification
     */
    public ProductVerificationTransaction(String transactionId, String verifierId, 
                                         String productId, boolean verificationResult,
                                         String verificationNotes) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.verifierId = verifierId;
        this.productId = productId;
        this.verificationResult = verificationResult;
        this.verificationNotes = verificationNotes;
    }
    
    /**
     * Full constructor with timestamp
     */
    public ProductVerificationTransaction(String transactionId, LocalDateTime timestamp,
                                         String verifierId, String productId, 
                                         boolean verificationResult, String verificationNotes) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.verifierId = verifierId;
        this.productId = productId;
        this.verificationResult = verificationResult;
        this.verificationNotes = verificationNotes;
    }
    
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    
    @Override
    public String getTransactionType() {
        return "PRODUCT_VERIFICATION";
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public Map<String, Object> getTransactionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("verifierId", verifierId);
        data.put("productId", productId);
        data.put("verificationResult", verificationResult);
        data.put("verificationNotes", verificationNotes);
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
        if (verifierId == null || verifierId.trim().isEmpty()) {
            return false;
        }
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }
        // verificationNotes can be empty but not null
        if (verificationNotes == null) {
            return false;
        }
        // verificationResult is a boolean, so it's always valid
        return true;
    }
    
    // Getters and Setters
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getVerifierId() {
        return verifierId;
    }
    
    public void setVerifierId(String verifierId) {
        this.verifierId = verifierId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public boolean isVerificationResult() {
        return verificationResult;
    }
    
    public void setVerificationResult(boolean verificationResult) {
        this.verificationResult = verificationResult;
    }
    
    public String getVerificationNotes() {
        return verificationNotes;
    }
    
    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVerificationTransaction that = (ProductVerificationTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        return "ProductVerificationTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", verifierId='" + verifierId + '\'' +
                ", productId='" + productId + '\'' +
                ", verificationResult=" + verificationResult +
                ", verificationNotes='" + verificationNotes + '\'' +
                '}';
    }
}
