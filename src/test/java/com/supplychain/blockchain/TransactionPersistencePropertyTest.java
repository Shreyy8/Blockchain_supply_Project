package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Property-based test for transaction persistence and availability.
 * Feature: blockchain-supply-chain, Property 2: Transaction persistence and availability
 * Validates: Requirements 1.2
 */
public class TransactionPersistencePropertyTest {
    
    /**
     * Property 2: Transaction persistence and availability
     * For any valid transaction added to the blockchain, subsequent queries 
     * for transaction history should include that transaction.
     */
    @Property(tries = 100)
    @Label("Transaction persistence and availability")
    void transactionPersistenceAndAvailability(
        @ForAll("validTransaction") Transaction transaction
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add the transaction
        blockchain.addTransaction(transaction);
        
        // Mine the pending transactions
        blockchain.minePendingTransactions();
        
        // Retrieve transaction history
        List<Transaction> history = blockchain.getTransactionHistory();
        
        // The transaction should be in the history
        boolean found = false;
        for (Transaction tx : history) {
            if (tx.getTransactionId().equals(transaction.getTransactionId())) {
                found = true;
                break;
            }
        }
        
        Assertions.assertTrue(found, 
            "Transaction " + transaction.getTransactionId() + " should be in transaction history");
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
