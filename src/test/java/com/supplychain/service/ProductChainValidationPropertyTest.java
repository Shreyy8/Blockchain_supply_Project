package com.supplychain.service;

import com.supplychain.blockchain.Block;
import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for product chain validation.
 * Feature: blockchain-supply-chain, Property 20: Product chain validation
 * 
 * Validates: Requirements 7.2
 * 
 * Property: For any product's transaction chain, authenticity verification should
 * validate that all transactions form a valid chain with proper cryptographic linkage.
 */
public class ProductChainValidationPropertyTest {
    
    /**
     * Property: For any product with multiple transactions, the blockchain should
     * maintain valid cryptographic linkage between all blocks containing those transactions.
     */
    @Property(tries = 100)
    @Label("Product transaction chain maintains valid cryptographic linkage")
    void productChainCryptographicLinkage(
            @ForAll("productIds") String productId,
            @ForAll("transactionCounts") int transactionCount) {
        
        Assume.that(transactionCount >= 2); // Need multiple transactions to test chain
        
        // Create blockchain manager and verifier
        BlockchainManager blockchainManager = new BlockchainManager(2);
        AuthenticityVerifier verifier = new AuthenticityVerifier(blockchainManager);
        
        // Create product transactions
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
        
        // Verify blockchain integrity
        assertTrue(blockchainManager.isChainValid(), 
                "Blockchain should maintain valid chain with proper cryptographic linkage");
        
        // Verify product authenticity (which checks chain validity)
        VerificationResult result = verifier.verifyProductAuthenticity(productId);
        
        assertTrue(result.isAuthentic(), 
                "Product should be authentic when chain has valid cryptographic linkage");
        assertEquals("CONFIRMED", result.getStatus(), 
                "Status should confirm valid chain");
        
        // Verify all blocks are properly linked
        List<Block> chain = blockchainManager.getChain();
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            assertEquals(previousBlock.getHash(), currentBlock.getPreviousHash(),
                    "Block " + i + " should have previousHash matching previous block's hash");
            assertTrue(currentBlock.isHashValid(),
                    "Block " + i + " should have valid hash");
        }
    }
    
    /**
     * Property: For any product, if the blockchain is tampered with (previousHash modified),
     * the chain validation should detect it and reject authenticity.
     */
    @Property(tries = 100)
    @Label("Tampered blockchain chain is detected during product validation")
    void tamperedChainDetection(
            @ForAll("productIds") String productId,
            @ForAll("transactionCounts") int transactionCount) {
        
        Assume.that(transactionCount >= 2);
        
        // Create blockchain manager and verifier
        BlockchainManager blockchainManager = new BlockchainManager(2);
        AuthenticityVerifier verifier = new AuthenticityVerifier(blockchainManager);
        
        // Create product transactions
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                "Origin Location"
        );
        blockchainManager.addTransaction(creation);
        
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
        
        blockchainManager.minePendingTransactions();
        
        // Verify chain is initially valid
        assertTrue(blockchainManager.isChainValid(), 
                "Chain should be valid before tampering");
        
        // Tamper with a block by modifying its previousHash (breaks chain linkage)
        List<Block> chain = blockchainManager.getChain();
        if (chain.size() > 1) {
            Block blockToTamper = chain.get(1);
            // Modify the previousHash to break the chain linkage
            blockToTamper.setPreviousHash("TAMPERED_HASH_0000000000000000");
            
            // Verify chain detects tampering
            assertFalse(blockchainManager.isChainValid(), 
                    "Chain validation should detect tampering when previousHash is modified");
            
            // Verify product authenticity check also detects tampering
            VerificationResult result = verifier.verifyProductAuthenticity(productId);
            assertFalse(result.isAuthentic(), 
                    "Product should not be authentic when chain is tampered");
            assertEquals("REJECTED", result.getStatus(), 
                    "Status should reject tampered chain");
        }
    }
    
    /**
     * Property: For any valid product chain, all transactions should pass validation.
     */
    @Property(tries = 100)
    @Label("All transactions in valid product chain pass validation")
    void allTransactionsValidInChain(
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
        
        blockchainManager.minePendingTransactions();
        
        // Get product history
        List<Transaction> history = blockchainManager.getProductHistory(productId);
        
        // Verify all transactions are valid
        for (Transaction transaction : history) {
            assertTrue(transaction.validate(), 
                    "All transactions in valid chain should pass validation");
        }
        
        // Verify product authenticity
        VerificationResult result = verifier.verifyProductAuthenticity(productId);
        assertTrue(result.isAuthentic(), 
                "Product with all valid transactions should be authentic");
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
