package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for product history retrieval completeness.
 * Feature: blockchain-supply-chain, Property 16: Product history retrieval completeness
 * 
 * Validates: Requirements 6.1
 * 
 * Property: For any blockchain with a set of transactions for a product,
 * retrieving product history should return all transactions associated with that product.
 */
public class ProductHistoryRetrievalCompletenessPropertyTest {
    
    /**
     * Property: For any product with associated transactions, retrieving its history
     * should return all transactions involving that product.
     */
    @Property(tries = 100)
    @Label("Product history retrieval returns all transactions for a product")
    void productHistoryRetrievalCompleteness(
            @ForAll("productIds") String productId,
            @ForAll("transactionCounts") int transactionCount) {
        
        // Create blockchain manager
        BlockchainManager blockchainManager = new BlockchainManager(2);
        ProductTraceabilityService service = new ProductTraceabilityService(blockchainManager);
        
        // Create transactions for the product
        List<Transaction> expectedTransactions = new ArrayList<>();
        
        // First transaction: product creation
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        expectedTransactions.add(creation);
        blockchainManager.addTransaction(creation);
        
        // Additional transactions: transfers
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
            expectedTransactions.add(transfer);
            blockchainManager.addTransaction(transfer);
        }
        
        // Mine the transactions into blocks
        blockchainManager.minePendingTransactions();
        
        // Retrieve product history
        List<Transaction> actualHistory = service.getProductHistory(productId);
        
        // Verify all transactions are returned
        assertEquals(expectedTransactions.size(), actualHistory.size(),
                "Product history should contain all transactions for the product");
        
        // Verify all expected transactions are present
        for (Transaction expected : expectedTransactions) {
            boolean found = false;
            for (Transaction actual : actualHistory) {
                if (expected.getTransactionId().equals(actual.getTransactionId())) {
                    found = true;
                    // Verify transaction details are complete
                    assertEquals(expected.getTransactionType(), actual.getTransactionType(),
                            "Transaction type should match");
                    assertNotNull(actual.getTimestamp(),
                            "Transaction should have timestamp");
                    assertNotNull(actual.getTransactionData(),
                            "Transaction should have data");
                    break;
                }
            }
            assertTrue(found, 
                    "Expected transaction " + expected.getTransactionId() + " should be in history");
        }
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
