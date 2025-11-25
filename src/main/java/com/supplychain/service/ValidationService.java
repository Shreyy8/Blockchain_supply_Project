package com.supplychain.service;

import com.supplychain.exception.InvalidTransactionException;
import com.supplychain.model.Product;
import com.supplychain.model.Transaction;

/**
 * Service for validating input data and providing specific error messages.
 * Requirements: 8.1 - Invalid input error messages
 */
public class ValidationService {
    
    /**
     * Validates a transaction and throws an exception with a specific error message if invalid.
     * 
     * @param transaction The transaction to validate
     * @throws InvalidTransactionException if the transaction is invalid, with a specific error message
     */
    public void validateTransaction(Transaction transaction) throws InvalidTransactionException {
        if (transaction == null) {
            throw new InvalidTransactionException("Transaction cannot be null");
        }
        
        if (transaction.getTransactionId() == null || transaction.getTransactionId().trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction ID is required and cannot be empty");
        }
        
        if (transaction.getTimestamp() == null) {
            throw new InvalidTransactionException("Transaction timestamp is required");
        }
        
        if (transaction.getTransactionType() == null || transaction.getTransactionType().trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction type is required and cannot be empty");
        }
        
        if (!transaction.validate()) {
            throw new InvalidTransactionException("Transaction validation failed: missing or invalid required fields");
        }
    }
    
    /**
     * Validates a product and throws an exception with a specific error message if invalid.
     * 
     * @param product The product to validate
     * @throws InvalidTransactionException if the product is invalid, with a specific error message
     */
    public void validateProduct(Product product) throws InvalidTransactionException {
        if (product == null) {
            throw new InvalidTransactionException("Product cannot be null");
        }
        
        if (product.getProductId() == null || product.getProductId().trim().isEmpty()) {
            throw new InvalidTransactionException("Product ID is required and cannot be empty");
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidTransactionException("Product name is required and cannot be empty");
        }
        
        if (product.getOrigin() == null || product.getOrigin().trim().isEmpty()) {
            throw new InvalidTransactionException("Product origin is required and cannot be empty");
        }
        
        if (product.getStatus() == null) {
            throw new InvalidTransactionException("Product status is required");
        }
    }
    
    /**
     * Validates a string field and throws an exception with a specific error message if invalid.
     * 
     * @param fieldValue The field value to validate
     * @param fieldName The name of the field (for error message)
     * @throws InvalidTransactionException if the field is invalid, with a specific error message
     */
    public void validateRequiredField(String fieldValue, String fieldName) throws InvalidTransactionException {
        if (fieldValue == null) {
            throw new InvalidTransactionException(fieldName + " is required and cannot be null");
        }
        
        if (fieldValue.trim().isEmpty()) {
            throw new InvalidTransactionException(fieldName + " is required and cannot be empty");
        }
    }
}
