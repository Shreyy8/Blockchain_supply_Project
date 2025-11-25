package com.supplychain.model;

/**
 * Enum representing the different states a product can be in throughout the supply chain.
 * Products transition through these states as they move through the supply chain.
 */
public enum ProductStatus {
    /**
     * Product has been created and registered in the system
     */
    CREATED,
    
    /**
     * Product is currently being transported between locations
     */
    IN_TRANSIT,
    
    /**
     * Product has been delivered to its destination
     */
    DELIVERED,
    
    /**
     * Product has been verified for authenticity and compliance
     */
    VERIFIED
}
