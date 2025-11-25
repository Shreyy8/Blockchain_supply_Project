package com.supplychain.model;

/**
 * Enum representing the different user roles in the supply chain system.
 * Each role has specific permissions and access to different functionalities.
 */
public enum UserRole {
    /**
     * Supply Chain Manager - oversees operations and monitors blockchain records
     */
    MANAGER,
    
    /**
     * Supplier - records transactions and verifies records in the supply chain
     */
    SUPPLIER,
    
    /**
     * Retailer - accesses blockchain data to verify product authenticity and traceability
     */
    RETAILER
}
