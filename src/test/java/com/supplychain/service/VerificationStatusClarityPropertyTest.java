package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

/**
 * Property-based test for verification status clarity.
 * Feature: blockchain-supply-chain, Property 15: Verification status clarity
 * Validates: Requirements 5.3
 */
public class VerificationStatusClarityPropertyTest {
    
    /**
     * Property 15: Verification status clarity
     * For any verification request, the system should return a clear boolean 
     * or status indicator showing whether records match.
     */
    @Property(tries = 100)
    @Label("Verification status clarity")
    void verificationStatusClarity(
        @ForAll("validTransaction") Transaction transaction,
        @ForAll boolean shouldBeInBlockchain
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Conditionally add the transaction to the blockchain
        if (shouldBeInBlockchain) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Create verification service
        TransactionVerificationService verificationService = 
            new TransactionVerificationService(blockchain);
        
        // Verify the transaction
        TransactionVerificationResult result = verificationService.verifyTransaction(transaction);
        
        // The result should have a clear boolean status
        Assertions.assertNotNull(result, "Verification result should not be null");
        
        // The verification status should match whether the transaction is in the blockchain
        if (shouldBeInBlockchain) {
            Assertions.assertTrue(result.isVerified(), 
                "Verification should succeed when transaction is in blockchain. Message: " + result.getMessage());
        } else {
            Assertions.assertFalse(result.isVerified(), 
                "Verification should fail when transaction is not in blockchain. Message: " + result.getMessage());
        }
        
        // The message should be clear and non-empty
        Assertions.assertNotNull(result.getMessage(), "Verification message should not be null");
        Assertions.assertFalse(result.getMessage().trim().isEmpty(), 
            "Verification message should not be empty");
        
        // The message should provide meaningful information
        if (shouldBeInBlockchain) {
            Assertions.assertTrue(
                result.getMessage().toLowerCase().contains("verified") 
                || result.getMessage().toLowerCase().contains("success")
                || result.getMessage().toLowerCase().contains("match"),
                "Success message should indicate verification succeeded"
            );
        } else {
            Assertions.assertTrue(
                result.getMessage().toLowerCase().contains("not found") 
                || result.getMessage().toLowerCase().contains("fail")
                || result.getMessage().toLowerCase().contains("does not match"),
                "Failure message should indicate why verification failed"
            );
        }
    }
    
    /**
     * Property test for blockchain integrity validation clarity.
     * The validateBlockchainIntegrity method should return a clear status.
     */
    @Property(tries = 100)
    @Label("Blockchain integrity validation status clarity")
    void blockchainIntegrityValidationClarity(
        @ForAll("validTransaction") Transaction transaction
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add a transaction and mine it
        blockchain.addTransaction(transaction);
        blockchain.minePendingTransactions();
        
        // Create verification service
        TransactionVerificationService verificationService = 
            new TransactionVerificationService(blockchain);
        
        // Validate blockchain integrity
        TransactionVerificationResult result = verificationService.validateBlockchainIntegrity();
        
        // The result should have a clear boolean status
        Assertions.assertNotNull(result, "Validation result should not be null");
        
        // For a properly constructed blockchain, validation should succeed
        Assertions.assertTrue(result.isVerified(), 
            "Blockchain integrity validation should succeed for valid blockchain. Message: " + result.getMessage());
        
        // The message should be clear and non-empty
        Assertions.assertNotNull(result.getMessage(), "Validation message should not be null");
        Assertions.assertFalse(result.getMessage().trim().isEmpty(), 
            "Validation message should not be empty");
        
        // The message should indicate success
        Assertions.assertTrue(
            result.getMessage().toLowerCase().contains("validated") 
            || result.getMessage().toLowerCase().contains("valid")
            || result.getMessage().toLowerCase().contains("integrity"),
            "Validation message should indicate blockchain integrity is valid"
        );
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
