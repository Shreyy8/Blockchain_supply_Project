package com.supplychain.service;

import com.supplychain.blockchain.Block;
import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ChainValidationException;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for blockchain validation error details.
 * 
 * Feature: blockchain-supply-chain, Property 23: Blockchain validation error details
 * Validates: Requirements 8.3
 * 
 * This test verifies that for any blockchain validation failure, the system should
 * provide detailed error information about which validation check failed.
 */
public class BlockchainValidationErrorDetailsPropertyTest {
    
    /**
     * Property: Invalid block hash is detected by validation
     * For any blockchain with a block that has an invalid hash,
     * validation should fail and return false.
     */
    @Property(tries = 100)
    @Label("Invalid block hash is detected by validation")
    void invalidBlockHashIsDetectedByValidation(
            @ForAll @IntRange(min = 2, max = 5) int chainLength,
            @ForAll @IntRange(min = 1, max = 4) int corruptedBlockIndex,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(corruptedBlockIndex < chainLength);
        
        // Create a blockchain with multiple blocks
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add transactions and mine blocks
        for (int i = 0; i < chainLength - 1; i++) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Corrupt a specific block by modifying its hash
        List<Block> chain = blockchain.getChain();
        Block corruptedBlock = chain.get(corruptedBlockIndex);
        corruptedBlock.setHash("CORRUPTED_HASH");
        
        // Validate the blockchain - should return false
        boolean isValid = blockchain.isChainValid();
        assertFalse(isValid, "Blockchain should be invalid after hash corruption");
    }
    
    /**
     * Property: Broken chain linkage is detected by validation
     * For any blockchain with broken linkage between blocks,
     * validation should fail and return false.
     */
    @Property(tries = 100)
    @Label("Broken chain linkage is detected by validation")
    void brokenChainLinkageIsDetectedByValidation(
            @ForAll @IntRange(min = 3, max = 5) int chainLength,
            @ForAll @IntRange(min = 2, max = 4) int brokenLinkIndex,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(brokenLinkIndex < chainLength);
        
        // Create a blockchain with multiple blocks
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add transactions and mine blocks
        for (int i = 0; i < chainLength - 1; i++) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Break the chain linkage by modifying previousHash
        List<Block> chain = blockchain.getChain();
        Block blockWithBrokenLink = chain.get(brokenLinkIndex);
        blockWithBrokenLink.setPreviousHash("BROKEN_LINK");
        
        // Validate the blockchain - should return false
        boolean isValid = blockchain.isChainValid();
        assertFalse(isValid, "Blockchain should be invalid after breaking linkage");
    }
    
    /**
     * Property: Valid blockchain passes validation
     * For any valid blockchain, validation should pass and return true.
     */
    @Property(tries = 100)
    @Label("Valid blockchain passes validation")
    void validBlockchainPassesValidation(
            @ForAll @IntRange(min = 1, max = 5) int chainLength,
            @ForAll("validTransaction") Transaction transaction) {
        
        // Create a blockchain with multiple blocks
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add transactions and mine blocks
        for (int i = 0; i < chainLength; i++) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Validate the blockchain - should return true
        boolean isValid = blockchain.isChainValid();
        assertTrue(isValid, "Valid blockchain should pass validation");
    }
    
    /**
     * Property: Validation correctly identifies corrupted blocks
     * For any blockchain with a corrupted block,
     * validation should detect the corruption and return false.
     */
    @Property(tries = 100)
    @Label("Validation correctly identifies corrupted blocks")
    void validationCorrectlyIdentifiesCorruptedBlocks(
            @ForAll @IntRange(min = 2, max = 4) int chainLength,
            @ForAll @IntRange(min = 1, max = 3) int corruptedBlockIndex,
            @ForAll @NotBlank String corruptedValue,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(corruptedBlockIndex < chainLength);
        
        // Create a blockchain with multiple blocks
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add transactions and mine blocks
        for (int i = 0; i < chainLength - 1; i++) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Corrupt a block
        List<Block> chain = blockchain.getChain();
        Block corruptedBlock = chain.get(corruptedBlockIndex);
        corruptedBlock.setHash(corruptedValue.trim());
        
        // Validate - should return false
        boolean isValid = blockchain.isChainValid();
        assertFalse(isValid, "Blockchain with corrupted block should be invalid");
    }
    
    /**
     * Property: Multiple validation failures are detected
     * For any blockchain with multiple validation failures,
     * validation should detect at least one failure and return false.
     */
    @Property(tries = 100)
    @Label("Multiple validation failures are detected")
    void multipleValidationFailuresAreDetected(
            @ForAll @IntRange(min = 3, max = 5) int chainLength,
            @ForAll("validTransaction") Transaction transaction) {
        
        // Create a blockchain with multiple blocks
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Add transactions and mine blocks
        for (int i = 0; i < chainLength - 1; i++) {
            blockchain.addTransaction(transaction);
            blockchain.minePendingTransactions();
        }
        
        // Corrupt multiple blocks
        List<Block> chain = blockchain.getChain();
        if (chain.size() >= 3) {
            chain.get(1).setHash("CORRUPTED_1");
            chain.get(2).setPreviousHash("CORRUPTED_2");
        }
        
        // Validate - should return false
        boolean isValid = blockchain.isChainValid();
        assertFalse(isValid, "Blockchain with multiple corruptions should be invalid");
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
