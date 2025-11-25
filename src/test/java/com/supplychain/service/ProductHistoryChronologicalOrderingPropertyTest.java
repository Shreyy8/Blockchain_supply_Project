package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for product history chronological ordering.
 * Feature: blockchain-supply-chain, Property 18: Product history chronological ordering
 * 
 * Validates: Requirements 6.3
 * 
 * Property: For any product with multiple transactions, the history should be
 * presented in chronological order from creation to current state.
 */
public class ProductHistoryChronologicalOrderingPropertyTest {
    
    /**
     * Property: For any product with multiple transactions, retrieving its history
     * should return transactions in chronological order from earliest to latest.
     */
    @Property(tries = 100)
    @Label("Product history is ordered chronologically from origin to current")
    void productHistoryChronologicalOrdering(
            @ForAll("productIds") String productId,
            @ForAll("transactionCounts") int transactionCount) {
        
        Assume.that(transactionCount >= 2); // Need at least 2 transactions to test ordering
        
        // Create blockchain manager and service
        BlockchainManager blockchainManager = new BlockchainManager(2);
        ProductTraceabilityService service = new ProductTraceabilityService(blockchainManager);
        
        // Create transactions with deliberate time delays to ensure ordering
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                LocalDateTime.now().minusHours(transactionCount),
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        blockchainManager.addTransaction(creation);
        
        // Create transfer transactions with increasing timestamps
        for (int i = 1; i < transactionCount; i++) {
            ProductTransferTransaction transfer = new ProductTransferTransaction(
                    "txn-" + productId + "-" + i,
                    LocalDateTime.now().minusHours(transactionCount - i),
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
        
        // Retrieve product history
        List<Transaction> history = service.getProductHistory(productId);
        
        // Verify transactions are in chronological order
        assertNotNull(history, "History should not be null");
        assertEquals(transactionCount, history.size(), "History should contain all transactions");
        
        // Check that each transaction's timestamp is not before the previous one
        LocalDateTime previousTimestamp = null;
        for (int i = 0; i < history.size(); i++) {
            Transaction transaction = history.get(i);
            LocalDateTime currentTimestamp = transaction.getTimestamp();
            
            assertNotNull(currentTimestamp, "Transaction should have a timestamp");
            
            if (previousTimestamp != null) {
                assertFalse(currentTimestamp.isBefore(previousTimestamp),
                        "Transaction at index " + i + " should not be before previous transaction. " +
                        "Expected chronological order from origin to current.");
            }
            
            previousTimestamp = currentTimestamp;
        }
        
        // Verify first transaction is the creation
        assertEquals("PRODUCT_CREATION", history.get(0).getTransactionType(),
                "First transaction should be product creation (origin)");
        
        // Verify subsequent transactions are transfers
        for (int i = 1; i < history.size(); i++) {
            assertEquals("PRODUCT_TRANSFER", history.get(i).getTransactionType(),
                    "Subsequent transactions should be transfers");
        }
    }
    
    /**
     * Property: For any product with a single transaction (creation only),
     * the history should contain just that transaction.
     */
    @Property(tries = 100)
    @Label("Product history with single transaction maintains order")
    void singleTransactionProductHistory(
            @ForAll("productIds") String productId) {
        
        // Create blockchain manager and service
        BlockchainManager blockchainManager = new BlockchainManager(2);
        ProductTraceabilityService service = new ProductTraceabilityService(blockchainManager);
        
        // Create only a creation transaction
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        blockchainManager.addTransaction(creation);
        blockchainManager.minePendingTransactions();
        
        // Retrieve product history
        List<Transaction> history = service.getProductHistory(productId);
        
        // Verify single transaction
        assertNotNull(history, "History should not be null");
        assertEquals(1, history.size(), "History should contain exactly one transaction");
        assertEquals("PRODUCT_CREATION", history.get(0).getTransactionType(),
                "Single transaction should be product creation");
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
        return Arbitraries.integers().between(2, 10);
    }
}
