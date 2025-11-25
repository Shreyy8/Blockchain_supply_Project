package com.supplychain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a product in the supply chain system.
 * Products are tracked through the blockchain as they move through the supply chain.
 * 
 * Requirements: 6.1, 6.2, 7.1
 */
public class Product {
    private String productId;
    private String name;
    private String description;
    private String origin;
    private String currentLocation;
    private ProductStatus status;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.status = ProductStatus.CREATED;
    }
    
    /**
     * Constructor with all required fields
     * 
     * @param productId Unique identifier for the product
     * @param name Name of the product
     * @param description Description of the product
     * @param origin Origin location of the product
     */
    public Product(String productId, String name, String description, String origin) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.origin = origin;
        this.currentLocation = origin;
        this.status = ProductStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Full constructor with all fields
     */
    public Product(String productId, String name, String description, String origin, 
                   String currentLocation, ProductStatus status, LocalDateTime createdAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.origin = origin;
        this.currentLocation = currentLocation;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public ProductStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Updates the product location and status
     * 
     * @param newLocation The new location of the product
     * @param newStatus The new status of the product
     */
    public void updateLocation(String newLocation, ProductStatus newStatus) {
        this.currentLocation = newLocation;
        this.status = newStatus;
    }
    
    /**
     * Validates that the product has all required fields
     * 
     * @return true if the product is valid, false otherwise
     */
    public boolean isValid() {
        return productId != null && !productId.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               origin != null && !origin.trim().isEmpty() &&
               status != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", origin='" + origin + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
