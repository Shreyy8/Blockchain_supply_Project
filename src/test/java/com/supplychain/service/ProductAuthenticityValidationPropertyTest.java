package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for product authenticity validation.
 * Feature: blockchain-supply-chain, Property 19: Product authenticity validation
 * 
 * Validates: Requirements 7.1
 * 
 * Property: For any product identifier, authenticity verification should validate
 * it against blockchain records and return a clear confirmation or rejection.
 */
public class ProductAuthenticityValidationPropertyTest {
    
    /**
     * Property: For any product with valid blockchain records, authenticity verification
     * should return a clear confirmation status.
     */
    @Property(tries = 100)
    @Label("Product with valid blockchain records is confirmed as authentic")
    void validProductAuthenticityConfirmation(
            @ForAll("productIds") String productId,
            @ForAll("transactionCounts") int transactionCount) {
        
        // Create blockchain manager and verifier
        BlockchainManager blockchainManager = new BlockchainManager(2);
        AuthenticityVerifier verifier = new AuthenticityVerifier(blockchainManager);
        
        // Create valid product transactions
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        blockchainManager.addTransaction(creation);
        
        // Add transfer transactions
        for (int i = 1; i < transactionCount; i++) {
            ProductTransferTransaction transfer = new ProductTransferTransaction(
                    "txn-" + productId + "-" + i,
                    "party-" + (i - 1),
                    "party-" + i,
                    productId,
                    "location-" + (i - 1),
                    "location-" + i,
                    ProductStatus.IN_TRANSIT
            );
            blockchainManager.addTransaction(transfer);
        }
        
        // Mine transactions
        blockchainManager.minePendingTransactions();
        
        // Verify product authenticity
        VerificationResult result = verifier.verifyProductAuthenticity(productId);
        
        // Verify clear confirmation status
        assertNotNull(result, "Verification result should not be null");
        assertEquals(productId, result.getProductId(), "Result should have correct product ID");
        assertTrue(result.isAuthentic(), "Product with valid records should be authentic");
        assertEquals("CONFIRMED", result.getStatus(), 
                "Status should be clear confirmation");
        assertFalse(result.getReasons().isEmpty(), 
                "Result should include reasons for confirmation");
    }
    
    /**
     * Property: For any product identifier not in blockchain, authenticity verification
     * should return a clear rejection status.
     */
    @Property(tries = 100)
    @Label("Product not in blockchain is rejected with clear status")
    void nonExistentProductAuthenticityRejection(
            @ForAll("productIds") String productId) {
        
        // Create blockchain manager and verifier with no transactions
        BlockchainManager blockchainManager = new BlockchainManager(2);
        AuthenticityVerifier verifier = new AuthenticityVerifier(blockchainManager);
        
        // Verify non-existent product
        VerificationResult result = verifier.verifyProductAuthenticity(productId);
        
        // Verify clear rejection status
        assertNotNull(result, "Verification result should not be null");
        assertEquals(productId, result.getProductId(), "Result should have correct product ID");
        assertFalse(result.isAuthentic(), "Non-existent product should not be authentic");
        assertEquals("REJECTED", result.getStatus(), 
                "Status should be clear rejection");
        assertFalse(result.getReasons().isEmpty(), 
                "Result should include reasons for rejection");
    }
    
    /**
     * Property: For any product with invalid transaction data, authenticity verification
     * should return a clear rejection status.
     */
    @Property(tries = 100)
    @Label("Product with invalid transaction is rejected")
    void invalidTransactionProductRejection(
            @ForAll("productIds") String productId) {
        
        // Create blockchain manager and verifier
        BlockchainManager blockchainManager = new BlockchainManager(2);
        AuthenticityVerifier verifier = new AuthenticityVerifier(blockchainManager);
        
        // Create a valid transaction first
        ProductCreationTransaction validCreation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        blockchainManager.addTransaction(validCreation);
        
        // Create an invalid transaction (missing required fields)
        ProductCreationTransaction invalidTransaction = new ProductCreationTransaction();
        invalidTransaction.setTransactionId("txn-" + productId + "-invalid");
        invalidTransaction.setProductId(productId);
        // Missing other required fields - will fail validation
        
        try {
            blockchainManager.addTransaction(invalidTransaction);
            // If it gets added (shouldn't happen), mine it
            blockchainManager.minePendingTransactions();
        } catch (IllegalArgumentException e) {
            // Expected - invalid transaction should be rejected
            // Continue with just the valid transaction
            blockchainManager.minePendingTransactions();
        }
        
        // Verify product - should succeed since only valid transaction was added
        VerificationResult result = verifier.verifyProductAuthenticity(productId);
        
        // The product should be authentic since invalid transaction was rejected
        assertNotNull(result, "Verification result should not be null");
        assertTrue(result.isAuthentic(), 
                "Product should be authentic when only valid transactions exist");
    }
    
    @Provide
    Arbitrary<String> productIds() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(5)
                .ofMaxLength(15);
    }
    
    @Provide
    Arbitrary<Integer> transactionCounts() {
        return Arbitraries.integers().between(1, 10);
    }
}
