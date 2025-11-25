package com.supplychain.service;

import com.supplychain.exception.InvalidTransactionException;
import com.supplychain.model.Product;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature: blockchain-supply-chain, Property 22: Invalid input error messages
 * 
 * Property: For any invalid data submitted to the system, it should be rejected 
 * with specific error messages indicating what is invalid.
 * 
 * Validates: Requirements 8.1
 */
public class InvalidInputErrorMessagesPropertyTest {
    
    private final ValidationService validationService = new ValidationService();
    
    /**
     * Property: Null transactions are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Null transaction produces specific error message")
    void nullTransactionProducesSpecificErrorMessage() {
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateTransaction(null)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(exception.getMessage().contains("null"), 
            "Error message should mention 'null'");
    }
    
    /**
     * Property: Transactions with null or empty transaction IDs are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Transaction with null/empty ID produces specific error message")
    void transactionWithInvalidIdProducesSpecificErrorMessage(
        @ForAll("invalidStrings") String invalidId,
        @ForAll("validStrings") String supplierId,
        @ForAll("validStrings") String productId,
        @ForAll("validStrings") String productName,
        @ForAll("validStrings") String origin
    ) {
        Transaction transaction = new ProductCreationTransaction(
            invalidId, supplierId, productId, productName, "", origin
        );
        
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateTransaction(transaction)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().toLowerCase().contains("transaction id") ||
            exception.getMessage().toLowerCase().contains("transactionid"),
            "Error message should mention transaction ID: " + exception.getMessage()
        );
    }
    
    /**
     * Property: Transactions with null timestamp are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Transaction with null timestamp produces specific error message")
    void transactionWithNullTimestampProducesSpecificErrorMessage(
        @ForAll("validStrings") String transactionId,
        @ForAll("validStrings") String supplierId,
        @ForAll("validStrings") String productId,
        @ForAll("validStrings") String productName,
        @ForAll("validStrings") String origin
    ) {
        ProductCreationTransaction transaction = new ProductCreationTransaction(
            transactionId, null, supplierId, productId, productName, "", origin
        );
        
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateTransaction(transaction)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().toLowerCase().contains("timestamp"),
            "Error message should mention timestamp: " + exception.getMessage()
        );
    }
    
    /**
     * Property: Products with null or empty product IDs are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Product with null/empty ID produces specific error message")
    void productWithInvalidIdProducesSpecificErrorMessage(
        @ForAll("invalidStrings") String invalidId,
        @ForAll("validStrings") String name,
        @ForAll("validStrings") String origin
    ) {
        Product product = new Product(invalidId, name, "", origin);
        
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateProduct(product)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().toLowerCase().contains("product id") ||
            exception.getMessage().toLowerCase().contains("productid"),
            "Error message should mention product ID: " + exception.getMessage()
        );
    }
    
    /**
     * Property: Products with null or empty names are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Product with null/empty name produces specific error message")
    void productWithInvalidNameProducesSpecificErrorMessage(
        @ForAll("validStrings") String productId,
        @ForAll("invalidStrings") String invalidName,
        @ForAll("validStrings") String origin
    ) {
        Product product = new Product(productId, invalidName, "", origin);
        
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateProduct(product)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().toLowerCase().contains("name"),
            "Error message should mention name: " + exception.getMessage()
        );
    }
    
    /**
     * Property: Products with null or empty origin are rejected with specific error message
     */
    @Property(tries = 100)
    @Label("Product with null/empty origin produces specific error message")
    void productWithInvalidOriginProducesSpecificErrorMessage(
        @ForAll("validStrings") String productId,
        @ForAll("validStrings") String name,
        @ForAll("invalidStrings") String invalidOrigin
    ) {
        Product product = new Product(productId, name, "", invalidOrigin);
        
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateProduct(product)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().toLowerCase().contains("origin"),
            "Error message should mention origin: " + exception.getMessage()
        );
    }
    
    /**
     * Property: Required field validation produces specific error messages mentioning the field name
     */
    @Property(tries = 100)
    @Label("Required field validation produces specific error message with field name")
    void requiredFieldValidationProducesSpecificErrorMessage(
        @ForAll("invalidStrings") String invalidValue,
        @ForAll("fieldNames") String fieldName
    ) {
        InvalidTransactionException exception = assertThrows(
            InvalidTransactionException.class,
            () -> validationService.validateRequiredField(invalidValue, fieldName)
        );
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(
            exception.getMessage().contains(fieldName),
            "Error message should mention the field name '" + fieldName + "': " + exception.getMessage()
        );
        assertTrue(
            exception.getMessage().toLowerCase().contains("required") ||
            exception.getMessage().toLowerCase().contains("empty") ||
            exception.getMessage().toLowerCase().contains("null"),
            "Error message should indicate the validation issue: " + exception.getMessage()
        );
    }
    
    // Arbitraries (generators)
    
    @Provide
    Arbitrary<String> validStrings() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .ofMinLength(1)
            .ofMaxLength(50);
    }
    
    @Provide
    Arbitrary<String> invalidStrings() {
        return Arbitraries.oneOf(
            Arbitraries.just(null),
            Arbitraries.just(""),
            Arbitraries.just("   "),
            Arbitraries.just("\t"),
            Arbitraries.just("\n")
        );
    }
    
    @Provide
    Arbitrary<String> fieldNames() {
        return Arbitraries.of(
            "productId",
            "productName",
            "supplierId",
            "transactionId",
            "origin",
            "location",
            "description"
        );
    }
}
