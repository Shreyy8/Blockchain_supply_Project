package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

/**
 * Property-based test for transaction verification round-trip.
 * Feature: blockchain-supply-chain, Property 13: Transaction verification round-trip
 * Validates: Requirements 5.1
 */
public class TransactionVerificationRoundTripPropertyTest {
    
    /**
     * Property 13: Transaction verification round-trip
     * For any transaction submitted to the blockchain, retrieving and verifying it 
     * should return data that matches the original submission.
     */
    @Property(tries = 100)
    @Label("Transaction verification round-trip")
    void transactionVerificationRoundTrip(
        @ForAll("validTransaction") Transaction transaction
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add the transaction to the blockchain
        blockchain.addTransaction(transaction);
        
        // Mine the pending transactions to add them to a block
        blockchain.minePendingTransactions();
        
        // Create verification service
        TransactionVerificationService verificationService = 
            new TransactionVerificationService(blockchain);
        
        // Verify the transaction
        TransactionVerificationResult result = verificationService.verifyTransaction(transaction);
        
        // The verification should succeed
        Assertions.assertTrue(result.isVerified(), 
            "Transaction verification should succeed for transaction " + transaction.getTransactionId() 
            + ". Message: " + result.getMessage());
        
        // The message should indicate success
        Assertions.assertTrue(result.getMessage().contains("verified successfully") 
            || result.getMessage().contains("matches blockchain record"),
            "Verification message should indicate success");
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
