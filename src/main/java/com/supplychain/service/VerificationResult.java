package com.supplychain.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a product authenticity verification.
 * Contains the verification status and detailed reasons.
 * 
 * Requirements: 7.3 - Clear confirmation or rejection status
 */
public class VerificationResult {
    private String productId;
    private boolean authentic;
    private String status; // "CONFIRMED" or "REJECTED"
    private List<String> reasons;
    
    /**
     * Constructor that initializes a verification result for a specific product.
     * 
     * @param productId The unique identifier of the product
     */
    public VerificationResult(String productId) {
        this.productId = productId;
        this.authentic = false;
        this.status = "PENDING";
        this.reasons = new ArrayList<>();
    }
    
    /**
     * Adds a reason for the verification result.
     * 
     * @param reason Description of why the product was confirmed or rejected
     */
    public void addReason(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            this.reasons.add(reason);
        }
    }
    
    // Getters and Setters
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public boolean isAuthentic() {
        return authentic;
    }
    
    public void setAuthentic(boolean authentic) {
        this.authentic = authentic;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getReasons() {
        return new ArrayList<>(reasons);
    }
    
    public void setReasons(List<String> reasons) {
        this.reasons = new ArrayList<>(reasons);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VerificationResult{");
        sb.append("productId='").append(productId).append('\'');
        sb.append(", authentic=").append(authentic);
        sb.append(", status='").append(status).append('\'');
        if (!reasons.isEmpty()) {
            sb.append(", reasons=").append(reasons);
        }
        sb.append('}');
        return sb.toString();
    }
}
