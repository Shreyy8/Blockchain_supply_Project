package com.supplychain.service;

import com.supplychain.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a traceability report for a product.
 * Contains origin information, transaction history, current status,
 * and indicators for missing information.
 * 
 * Requirements: 6.2 - Traceability report completeness
 */
public class TraceabilityReport {
    private String productId;
    private String origin;
    private String currentLocation;
    private String currentStatus;
    private List<Transaction> transactions;
    private boolean complete;
    private List<String> missingInformation;
    
    /**
     * Constructor that initializes a report for a specific product.
     * 
     * @param productId The unique identifier of the product
     */
    public TraceabilityReport(String productId) {
        this.productId = productId;
        this.transactions = new ArrayList<>();
        this.missingInformation = new ArrayList<>();
        this.complete = true; // Assume complete until proven otherwise
    }
    
    /**
     * Adds a transaction to the report.
     * 
     * @param transaction The transaction to add
     */
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            this.transactions.add(transaction);
        }
    }
    
    /**
     * Adds information about missing data.
     * 
     * @param info Description of what information is missing
     */
    public void addMissingInformation(String info) {
        if (info != null && !info.trim().isEmpty()) {
            this.missingInformation.add(info);
        }
    }
    
    // Getters and Setters
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public String getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }
    
    public boolean isComplete() {
        return complete;
    }
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    public List<String> getMissingInformation() {
        return new ArrayList<>(missingInformation);
    }
    
    public void setMissingInformation(List<String> missingInformation) {
        this.missingInformation = new ArrayList<>(missingInformation);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TraceabilityReport{");
        sb.append("productId='").append(productId).append('\'');
        sb.append(", origin='").append(origin).append('\'');
        sb.append(", currentLocation='").append(currentLocation).append('\'');
        sb.append(", currentStatus='").append(currentStatus).append('\'');
        sb.append(", transactionCount=").append(transactions.size());
        sb.append(", complete=").append(complete);
        if (!missingInformation.isEmpty()) {
            sb.append(", missingInformation=").append(missingInformation);
        }
        sb.append('}');
        return sb.toString();
    }
}
