package com.supplychain.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Interface representing a transaction in the blockchain supply chain system.
 * Demonstrates polymorphism - different transaction types can be processed uniformly
 * through this interface.
 * 
 * Requirements: 9.2, 9.4, 4.1, 4.4
 */
public interface Transaction {
    /**
     * Gets the unique identifier for this transaction.
     * 
     * @return The transaction ID
     */
    String getTransactionId();
    
    /**
     * Gets the type of this transaction (e.g., "PRODUCT_CREATION", "PRODUCT_TRANSFER").
     * 
     * @return The transaction type
     */
    String getTransactionType();
    
    /**
     * Gets the timestamp when this transaction was created.
     * 
     * @return The transaction timestamp
     */
    LocalDateTime getTimestamp();
    
    /**
     * Gets the transaction data as a map of key-value pairs.
     * This allows different transaction types to store different data.
     * 
     * @return Map containing transaction-specific data
     */
    Map<String, Object> getTransactionData();
    
    /**
     * Validates that this transaction has all required fields and valid data.
     * Each transaction type implements its own validation logic.
     * 
     * Requirements: 4.1 - Transaction validation
     * 
     * @return true if the transaction is valid, false otherwise
     */
    boolean validate();
}
