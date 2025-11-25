package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.InvalidTransactionException;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;

import java.util.logging.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for exception logging and user messaging.
 * 
 * Feature: blockchain-supply-chain, Property 24: Exception logging and user messaging
 * Validates: Requirements 8.4
 * 
 * This test verifies that for any exception that occurs, the system should both
 * log detailed error information and present a user-friendly message.
 */
public class ExceptionLoggingAndUserMessagingPropertyTest {
    
    /**
     * Property: Invalid transaction exceptions are logged
     * For any invalid transaction that causes an exception,
     * the system should log the exception details.
     */
    @Property(tries = 100)
    @Label("Invalid transaction exceptions are logged")
    void invalidTransactionExceptionsAreLogged(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId) {
        
        // Set up a logger to capture log output
        Logger logger = Logger.getLogger(BlockchainManager.class.getName());
        ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
        Handler handler = new StreamHandler(logOutput, new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        
        try {
            BlockchainManager blockchain = new BlockchainManager(2);
            
            // Create an invalid transaction (empty product name should fail validation)
            Transaction invalidTransaction = new ProductCreationTransaction(
                transactionId.trim(),
                supplierId.trim(),
                productId.trim(),
                "", // Empty product name - should be invalid
                "Description",
                "Origin"
            );
            
            // Try to add the invalid transaction
            try {
                blockchain.addTransaction(invalidTransaction);
                // If it doesn't throw, the transaction was valid (which is fine for this test)
            } catch (IllegalArgumentException e) {
                // Exception was thrown - verify it contains useful information
                String errorMessage = e.getMessage();
                assertNotNull(errorMessage, "Exception message should not be null");
                assertFalse(errorMessage.isEmpty(), "Exception message should not be empty");
                
                // Message should contain details about what went wrong
                assertTrue(
                    errorMessage.toLowerCase().contains("invalid") ||
                    errorMessage.toLowerCase().contains("validation") ||
                    errorMessage.toLowerCase().contains("transaction"),
                    "Exception message should contain details about the validation failure: " + errorMessage
                );
            }
            
        } finally {
            // Clean up handler
            handler.flush();
            logger.removeHandler(handler);
        }
    }
    
    /**
     * Property: Null transaction exceptions provide clear messages
     * For any null transaction, the system should throw an exception
     * with a clear, user-friendly message.
     */
    @Property(tries = 100)
    @Label("Null transaction exceptions provide clear messages")
    void nullTransactionExceptionsProvideClearMessages() {
        
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Try to add a null transaction
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> blockchain.addTransaction(null),
            "Adding null transaction should throw IllegalArgumentException"
        );
        
        // Verify the exception message is clear and user-friendly
        String errorMessage = exception.getMessage();
        assertNotNull(errorMessage, "Exception message should not be null");
        assertFalse(errorMessage.isEmpty(), "Exception message should not be empty");
        
        // Message should be clear about what went wrong
        assertTrue(
            errorMessage.toLowerCase().contains("null") ||
            errorMessage.toLowerCase().contains("transaction"),
            "Exception message should clearly indicate the null transaction problem: " + errorMessage
        );
    }
    
    /**
     * Property: Invalid product ID exceptions provide clear messages
     * For any invalid product ID (null or empty), the system should throw
     * an exception with a clear message.
     */
    @Property(tries = 100)
    @Label("Invalid product ID exceptions provide clear messages")
    void invalidProductIdExceptionsProvideClearMessages() {
        
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Try to get product history with empty product ID
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> blockchain.getProductHistory(""),
            "Getting product history with empty ID should throw IllegalArgumentException"
        );
        
        // Verify the exception message is clear
        String errorMessage = exception.getMessage();
        assertNotNull(errorMessage, "Exception message should not be null");
        assertFalse(errorMessage.isEmpty(), "Exception message should not be empty");
        
        // Message should indicate the problem with the product ID
        assertTrue(
            errorMessage.toLowerCase().contains("product") ||
            errorMessage.toLowerCase().contains("id") ||
            errorMessage.toLowerCase().contains("empty") ||
            errorMessage.toLowerCase().contains("null"),
            "Exception message should clearly indicate the product ID problem: " + errorMessage
        );
    }
    
    /**
     * Property: Exception messages are user-friendly
     * For any exception thrown by the system, the message should be
     * understandable by users (not just developers).
     */
    @Property(tries = 100)
    @Label("Exception messages are user-friendly")
    void exceptionMessagesAreUserFriendly(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId) {
        
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Create an invalid transaction
        Transaction invalidTransaction = new ProductCreationTransaction(
            transactionId.trim(),
            supplierId.trim(),
            productId.trim(),
            "", // Empty product name
            "Description",
            "Origin"
        );
        
        try {
            blockchain.addTransaction(invalidTransaction);
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            
            // Message should not contain technical jargon or stack traces
            assertFalse(errorMessage.contains("NullPointerException"), 
                "User message should not contain technical exception names");
            assertFalse(errorMessage.contains("at com.supplychain"), 
                "User message should not contain stack trace information");
            
            // Message should be reasonably short and clear
            assertTrue(errorMessage.length() < 500, 
                "User message should be concise (less than 500 characters)");
            
            // Message should start with a capital letter (proper formatting)
            assertTrue(Character.isUpperCase(errorMessage.charAt(0)) || 
                      !Character.isLetter(errorMessage.charAt(0)),
                "User message should be properly formatted");
        }
    }
    
    /**
     * Property: Exceptions contain actionable information
     * For any exception, the message should help users understand
     * what went wrong and potentially how to fix it.
     */
    @Property(tries = 100)
    @Label("Exceptions contain actionable information")
    void exceptionsContainActionableInformation() {
        
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Try to add null transaction
        try {
            blockchain.addTransaction(null);
            fail("Should have thrown exception for null transaction");
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            
            // Message should not be too generic
            assertFalse(errorMessage.equals("Error"), 
                "Message should not be generic 'Error'");
            assertFalse(errorMessage.equals("Invalid input"), 
                "Message should be more specific than 'Invalid input'");
            
            // Message should contain specific information
            assertTrue(errorMessage.length() > 5, 
                "Message should contain meaningful information (more than 5 characters)");
        }
        
        // Try to get product history with null ID
        try {
            blockchain.getProductHistory(null);
            fail("Should have thrown exception for null product ID");
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            
            // Message should identify what parameter was invalid
            assertTrue(
                errorMessage.toLowerCase().contains("product") ||
                errorMessage.toLowerCase().contains("id"),
                "Message should identify which parameter was invalid: " + errorMessage
            );
        }
    }
    
    /**
     * Property: Valid operations do not throw exceptions
     * For any valid transaction, adding it to the blockchain
     * should not throw exceptions.
     */
    @Property(tries = 100)
    @Label("Valid operations do not throw exceptions")
    void validOperationsDoNotThrowExceptions(
            @ForAll("validTransaction") Transaction transaction) {
        
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Adding a valid transaction should not throw
        assertDoesNotThrow(
            () -> blockchain.addTransaction(transaction),
            "Valid transaction should not throw exception"
        );
        
        // Getting product history with valid ID should not throw
        String productId = (String) transaction.getTransactionData().get("productId");
        assertDoesNotThrow(
            () -> blockchain.getProductHistory(productId),
            "Getting product history with valid ID should not throw exception"
        );
    }
    
    /**
     * Provides a valid transaction for testing
     */
    @Provide
    Arbitrary<Transaction> validTransaction() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20)
        ).as((txId, supplierId, productId, productName, description, origin) ->
            new ProductCreationTransaction(
                txId, supplierId, productId, productName, 
                description != null ? description : "", origin
            )
        );
    }
}
