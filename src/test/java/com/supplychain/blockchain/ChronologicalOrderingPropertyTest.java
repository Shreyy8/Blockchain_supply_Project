package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Property-based test for chronological ordering of transactions.
 * Feature: blockchain-supply-chain, Property 3: Chronological ordering of transactions
 * Validates: Requirements 1.3
 */
public class ChronologicalOrderingPropertyTest {
    
    /**
     * Property 3: Chronological ordering of transactions
     * For any set of transactions with different timestamps, retrieving transaction 
     * history should return them ordered chronologically from earliest to latest.
     */
    @Property(tries = 100)
    @Label("Chronological ordering of transactions")
    void chronologicalOrderingOfTransactions(
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
        
        // Verify chronological ordering
        for (int i = 1; i < history.size(); i++) {
            LocalDateTime previousTimestamp = history.get(i - 1).getTimestamp();
            LocalDateTime currentTimestamp = history.get(i).getTimestamp();
            
            // Current timestamp should be equal to or after previous timestamp
            Assertions.assertFalse(currentTimestamp.isBefore(previousTimestamp),
                "Transaction at index " + i + " should not have a timestamp before transaction at index " + (i-1) +
                ". Previous: " + previousTimestamp + ", Current: " + currentTimestamp);
        }
    }
    
    /**
     * Provides arbitrary lists of valid transactions for testing.
     */
    @Provide
    Arbitrary<List<Transaction>> transactionList() {
        return validTransaction().list().ofMinSize(2).ofMaxSize(10);
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
