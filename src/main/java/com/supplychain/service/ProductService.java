package com.supplychain.service;

import com.supplychain.dao.ProductDAO;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.exception.ServiceException;
import com.supplychain.exception.ValidationException;
import com.supplychain.model.Product;
import com.supplychain.model.ProductStatus;
import com.supplychain.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for product-related business operations.
 */
public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final ProductDAO productDAO;

    public ProductService() throws DatabaseException {
        try {
            this.productDAO = new ProductDAO();
        } catch (ConnectionException e) {
            throw new DatabaseException("Failed to initialize ProductService", e);
        }
    }

    /**
     * Creates a new product in the system with comprehensive validation.
     */
    public Product createProduct(String name, String description, String origin, String createdBy) 
            throws ServiceException, ValidationException {
        
        // Input validation
        ValidationUtil.ValidationResult nameValidation = ValidationUtil.validateProductName(name);
        if (!nameValidation.isValid()) {
            throw new ValidationException(nameValidation.getErrorMessage());
        }
        
        ValidationUtil.ValidationResult descValidation = ValidationUtil.validateProductDescription(description);
        if (!descValidation.isValid()) {
            throw new ValidationException(descValidation.getErrorMessage());
        }
        
        ValidationUtil.ValidationResult originValidation = ValidationUtil.validateLocation(origin);
        if (!originValidation.isValid()) {
            throw new ValidationException(originValidation.getErrorMessage());
        }
        
        try {
            // Sanitize inputs
            String sanitizedName = ValidationUtil.sanitizeInput(name);
            String sanitizedDescription = ValidationUtil.sanitizeInput(description);
            String sanitizedOrigin = ValidationUtil.sanitizeInput(origin);
            
            // Generate unique product ID
            String productId = "PROD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Check for duplicate product names
            List<Product> existingProducts = productDAO.findAll();
            boolean nameExists = existingProducts.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(sanitizedName));
            
            if (nameExists) {
                throw new ValidationException("A product with this name already exists");
            }
            
            Product product = new Product(productId, sanitizedName, sanitizedDescription, 
                                        sanitizedOrigin, sanitizedOrigin, 
                                        ProductStatus.CREATED, LocalDateTime.now());
            productDAO.save(product);
            
            LOGGER.info("Product created successfully: " + productId + " by user: " + createdBy);
            return product;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error creating product", e);
            throw new ServiceException("Failed to create product due to database error", e);
        }
    }

    /**
     * Gets a product by its ID.
     */
    public Product getProductById(String productId) throws DatabaseException {
        try {
            return productDAO.findById(productId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get product by ID", e);
        }
    }

    /**
     * Gets all products in the system.
     */
    public List<Product> getAllProducts() throws DatabaseException {
        try {
            return productDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get all products", e);
        }
    }

    /**
     * Gets recent products (limited number).
     */
    public List<Product> getRecentProducts(int limit) throws DatabaseException {
        try {
            List<Product> allProducts = productDAO.findAll();
            if (limit > 0 && allProducts.size() > limit) {
                return allProducts.subList(Math.max(0, allProducts.size() - limit), allProducts.size());
            }
            return allProducts;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get recent products", e);
        }
    }

    /**
     * Gets products created by a specific user.
     */
    public List<Product> getProductsByUser(String userId, int limit) throws DatabaseException {
        // For now, return recent products since we don't track creator in the current schema
        return getRecentProducts(limit > 0 ? limit : 10);
    }

    /**
     * Gets products received by a specific user (for retailers).
     */
    public List<Product> getProductsReceivedByUser(String userId, int limit) throws DatabaseException {
        // For now, return recent products since we don't track ownership transfers in current schema
        return getRecentProducts(limit > 0 ? limit : 10);
    }

    /**
     * Gets the total count of products in the system.
     */
    public int getTotalProductCount() throws DatabaseException {
        try {
            return productDAO.findAll().size();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get product count", e);
        }
    }

    /**
     * Updates a product's status.
     */
    public void updateProductStatus(String productId, ProductStatus status) throws DatabaseException {
        try {
            Product product = productDAO.findById(productId);
            if (product != null) {
                product.setStatus(status);
                productDAO.update(product);
                LOGGER.info("Product status updated: " + productId + " -> " + status);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update product status", e);
        }
    }

    /**
     * Updates a product's location.
     */
    public void updateProductLocation(String productId, String newLocation) throws DatabaseException {
        try {
            Product product = productDAO.findById(productId);
            if (product != null) {
                product.setCurrentLocation(newLocation);
                productDAO.update(product);
                LOGGER.info("Product location updated: " + productId + " -> " + newLocation);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update product location", e);
        }
    }
}