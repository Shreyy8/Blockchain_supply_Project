package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for tracking product history and generating traceability reports.
 * Provides methods to retrieve complete product history from the blockchain
 * and generate comprehensive traceability reports.
 * 
 * Requirements: 6.1, 6.2, 6.3
 */
public class ProductTraceabilityService {
    private BlockchainManager blockchainManager;
    
    /**
     * Constructor that initializes the service with a blockchain manager.
     * 
     * @param blockchainManager The blockchain manager to use for retrieving transaction data
     */
    public ProductTraceabilityService(BlockchainManager blockchainManager) {
        if (blockchainManager == null) {
            throw new IllegalArgumentException("BlockchainManager cannot be null");
        }
        this.blockchainManager = blockchainManager;
    }
    
    /**
     * Retrieves the complete history of a product from the blockchain.
     * Returns all transactions associated with the specified product in chronological order.
     * 
     * Requirements: 6.1 - Product history retrieval with all blockchain records
     * Requirements: 6.3 - Chronological ordering from origin to current location
     * 
     * @param productId The unique identifier of the product
     * @return List of transactions involving the product, ordered chronologically
     * @throws IllegalArgumentException if productId is null or empty
     */
    public List<Transaction> getProductHistory(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        // Use BlockchainManager to retrieve product history
        // The transactions are already in chronological order
        return blockchainManager.getProductHistory(productId);
    }
    
    /**
     * Generates a comprehensive traceability report for a product.
     * The report includes origin information, all intermediate transactions,
     * and current status.
     * 
     * Requirements: 6.2 - Traceability report with origin, transactions, and current status
     * Requirements: 6.3 - Chronological ordering of data
     * 
     * @param productId The unique identifier of the product
     * @return TraceabilityReport containing complete product information
     * @throws IllegalArgumentException if productId is null or empty
     */
    public TraceabilityReport generateTraceabilityReport(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        // Get product history
        List<Transaction> history = getProductHistory(productId);
        
        // Create report
        TraceabilityReport report = new TraceabilityReport(productId);
        
        if (history.isEmpty()) {
            // No transactions found - mark as incomplete
            report.setComplete(false);
            report.addMissingInformation("No transaction history found for product");
            return report;
        }
        
        // Extract information from transactions
        String origin = null;
        String currentLocation = null;
        String currentStatus = null;
        
        for (Transaction transaction : history) {
            Map<String, Object> data = transaction.getTransactionData();
            
            // Add transaction to report
            report.addTransaction(transaction);
            
            // Extract origin from first transaction (creation)
            if (origin == null && "PRODUCT_CREATION".equals(transaction.getTransactionType())) {
                origin = (String) data.get("origin");
            }
            
            // Update current location and status from latest transaction
            if ("PRODUCT_TRANSFER".equals(transaction.getTransactionType())) {
                currentLocation = (String) data.get("toLocation");
                Object statusObj = data.get("newStatus");
                currentStatus = statusObj != null ? statusObj.toString() : null;
            } else if ("PRODUCT_CREATION".equals(transaction.getTransactionType())) {
                currentLocation = (String) data.get("origin");
                currentStatus = "CREATED";
            }
        }
        
        // Set report fields
        report.setOrigin(origin);
        report.setCurrentLocation(currentLocation);
        report.setCurrentStatus(currentStatus);
        
        // Check for missing information
        if (origin == null) {
            report.setComplete(false);
            report.addMissingInformation("Origin information not found");
        }
        if (currentLocation == null) {
            report.setComplete(false);
            report.addMissingInformation("Current location not found");
        }
        if (currentStatus == null) {
            report.setComplete(false);
            report.addMissingInformation("Current status not found");
        }
        
        return report;
    }
    
    /**
     * Gets the blockchain manager used by this service.
     * 
     * @return The blockchain manager
     */
    public BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
}
