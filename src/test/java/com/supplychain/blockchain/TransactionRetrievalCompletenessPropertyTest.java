package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Property-based test for transaction retrieval completeness.
 * Feature: blockchain-supply-chain, Property 1: Transaction retrieval completeness
 * Validates: Requirements 1.1
 */
public class TransactionRetrievalCompletenessPropertyTest {
    
    /**
     * Property 1: Transaction retrieval completeness
     * For any blockchain with a set of transactions, retrieving transaction history 
     * should return all transactions with complete details (timestamp, participant 
     * information, transaction data).
     */
    @Property(tries = 100)
    @Label("Transaction retrieval completeness")
    void transactionRetrievalCompleteness(
        @ForAll("transactionList") List<Transaction> transactions
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add all transactions
        for (Transaction tx : transactions) {
            blockchain.addTransaction(tx);
        }
        
        // Mine the pending transactions
        blockchain.minePendingTransactions();
        
        // Retrieve transaction history
        List<Transaction> history = blockchain.getTransactionHistory();
        
        // All transactions should be present
        Assertions.assertEquals(transactions.size(), history.size(),
            "Transaction history should contain all added transactions");
        
        // Check that all transactions have complete details
        for (Transaction tx : history) {
            // Check timestamp is present
            Assertions.assertNotNull(tx.getTimestamp(), 
                "Transaction should have a timestamp");
            
            // Check transaction ID is present
            Assertions.assertNotNull(tx.getTransactionId(), 
                "Transaction should have an ID");
            Assertions.assertFalse(tx.getTransactionId().trim().isEmpty(), 
                "Transaction ID should not be empty");
            
            // Check transaction type is present
            Assertions.assertNotNull(tx.getTransactionType(), 
                "Transaction should have a type");
            
            // Check transaction data is present
            Assertions.assertNotNull(tx.getTransactionData(), 
                "Transaction should have data");
            Assertions.assertFalse(tx.getTransactionData().isEmpty(), 
                "Transaction data should not be empty");
        }
        
        // Verify all original transaction IDs are in the history
        Set<String> originalIds = new HashSet<>();
        for (Transaction tx : transactions) {
            originalIds.add(tx.getTransactionId());
        }
        
        Set<String> historyIds = new HashSet<>();
        for (Transaction tx : history) {
            historyIds.add(tx.getTransactionId());
        }
        
        Assertions.assertEquals(originalIds, historyIds,
            "All original transaction IDs should be in the history");
    }
    
    /**
     * Provides arbitrary lists of valid transactions for testing.
     */
    @Provide
    Arbitrary<List<Transaction>> transactionList() {
        return validTransaction().list().ofMinSize(1).ofMaxSize(10);
    }
    
    /**
     * Provides arbitrary valid transactions for testing.
     */
    @Provide
    Arbitrary<Transaction> validTransaction() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20)
        ).as((txId, supplierId, productId, productName, productDesc, origin) -> 
            new ProductCreationTransaction(
                txId,
                supplierId,
                productId,
                productName,
                productDesc,
                origin
            )
        );
    }
}
