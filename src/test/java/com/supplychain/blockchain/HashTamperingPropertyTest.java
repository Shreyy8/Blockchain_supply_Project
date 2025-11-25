package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for hash tampering detection.
 * 
 * Feature: blockchain-supply-chain, Property 14: Hash validation detects tampering
 * Validates: Requirements 5.2
 * 
 * This test verifies that for any block in the blockchain, if its data is modified,
 * hash validation should detect the tampering and fail validation.
 */
public class HashTamperingPropertyTest {
    
    /**
     * Property: Modifying block index invalidates hash
     * For any block, if the index is modified after creation,
     * hash validation should detect the tampering.
     */
    @Property(tries = 100)
    @Label("Modifying block index invalidates hash")
    void modifyingBlockIndexInvalidatesHash(
            @ForAll @IntRange(min = 0, max = 10000) int originalIndex,
            @ForAll @IntRange(min = 0, max = 10000) int modifiedIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(originalIndex != modifiedIndex);
        
        // Create a block
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(originalIndex, transactions, previousHash.trim());
        
        // Verify hash is initially valid
        assertTrue(block.isHashValid(), "Block hash should be valid initially");
        
        // Tamper with the block by changing the index
        block.setIndex(modifiedIndex);
        
        // Verify hash validation detects tampering
        assertFalse(block.isHashValid(), 
            "Hash validation should detect tampering when index is modified");
    }
    
    /**
     * Property: Modifying previous hash invalidates hash
     * For any block, if the previous hash is modified after creation,
     * hash validation should detect the tampering.
     */
    @Property(tries = 100)
    @Label("Modifying previous hash invalidates hash")
    void modifyingPreviousHashInvalidatesHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String originalPreviousHash,
            @ForAll @NotBlank String modifiedPreviousHash,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(!originalPreviousHash.equals(modifiedPreviousHash));
        
        // Create a block
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(blockIndex, transactions, originalPreviousHash.trim());
        
        // Verify hash is initially valid
        assertTrue(block.isHashValid(), "Block hash should be valid initially");
        
        // Tamper with the block by changing the previous hash
        block.setPreviousHash(modifiedPreviousHash.trim());
        
        // Verify hash validation detects tampering
        assertFalse(block.isHashValid(), 
            "Hash validation should detect tampering when previous hash is modified");
    }
    
    /**
     * Property: Modifying timestamp invalidates hash
     * For any block, if the timestamp is modified after creation,
     * hash validation should detect the tampering.
     */
    @Property(tries = 100)
    @Label("Modifying timestamp invalidates hash")
    void modifyingTimestampInvalidatesHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("validTransaction") Transaction transaction) {
        
        // Create a block
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Verify hash is initially valid
        assertTrue(block.isHashValid(), "Block hash should be valid initially");
        
        // Tamper with the block by changing the timestamp
        LocalDateTime modifiedTimestamp = block.getTimestamp().plusDays(1);
        block.setTimestamp(modifiedTimestamp);
        
        // Verify hash validation detects tampering
        assertFalse(block.isHashValid(), 
            "Hash validation should detect tampering when timestamp is modified");
    }
    
    /**
     * Property: Modifying nonce invalidates hash
     * For any block, if the nonce is modified after creation,
     * hash validation should detect the tampering.
     */
    @Property(tries = 100)
    @Label("Modifying nonce invalidates hash")
    void modifyingNonceInvalidatesHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("validTransaction") Transaction transaction,
            @ForAll long modifiedNonce) {
        
        // Create a block
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        long originalNonce = block.getNonce();
        Assume.that(originalNonce != modifiedNonce);
        
        // Verify hash is initially valid
        assertTrue(block.isHashValid(), "Block hash should be valid initially");
        
        // Tamper with the block by changing the nonce
        block.setNonce(modifiedNonce);
        
        // Verify hash validation detects tampering
        assertFalse(block.isHashValid(), 
            "Hash validation should detect tampering when nonce is modified");
    }
    
    /**
     * Property: Modifying transactions invalidates hash
     * For any block, if the transactions are modified after creation,
     * hash validation should detect the tampering.
     */
    @Property(tries = 100)
    @Label("Modifying transactions invalidates hash")
    void modifyingTransactionsInvalidatesHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("validTransaction") Transaction originalTransaction,
            @ForAll("validTransaction") Transaction additionalTransaction) {
        
        // Create a block with one transaction
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(originalTransaction);
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Verify hash is initially valid
        assertTrue(block.isHashValid(), "Block hash should be valid initially");
        
        // Tamper with the block by adding another transaction
        List<Transaction> modifiedTransactions = new ArrayList<>(block.getTransactions());
        modifiedTransactions.add(additionalTransaction);
        block.setTransactions(modifiedTransactions);
        
        // Verify hash validation detects tampering
        assertFalse(block.isHashValid(), 
            "Hash validation should detect tampering when transactions are modified");
    }
    
    /**
     * Property: Unmodified block always has valid hash
     * For any block that hasn't been modified, hash validation should always pass.
     */
    @Property(tries = 100)
    @Label("Unmodified block always has valid hash")
    void unmodifiedBlockAlwaysHasValidHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("transactionList") List<Transaction> transactions) {
        
        Assume.that(!transactions.isEmpty());
        
        // Create a block
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Verify hash is valid
        assertTrue(block.isHashValid(), 
            "Unmodified block should always have valid hash");
        
        // Verify multiple times to ensure consistency
        assertTrue(block.isHashValid(), 
            "Hash validation should be consistent");
        assertTrue(block.isHashValid(), 
            "Hash validation should be consistent");
    }
    
    /**
     * Property: Manually setting correct hash makes validation pass
     * For any block, if we recalculate and set the correct hash after modification,
     * validation should pass again.
     */
    @Property(tries = 100)
    @Label("Manually setting correct hash makes validation pass")
    void manuallySettingCorrectHashMakesValidationPass(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @IntRange(min = 0, max = 10000) int modifiedIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("validTransaction") Transaction transaction) {
        
        Assume.that(blockIndex != modifiedIndex);
        
        // Create a block
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Tamper with the block
        block.setIndex(modifiedIndex);
        assertFalse(block.isHashValid(), "Hash should be invalid after tampering");
        
        // Recalculate and set the correct hash
        String newHash = block.calculateHash();
        block.setHash(newHash);
        
        // Verify hash is now valid
        assertTrue(block.isHashValid(), 
            "Hash validation should pass after recalculating and setting correct hash");
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
    
    /**
     * Provides a list of valid transactions for testing
     */
    @Provide
    Arbitrary<List<Transaction>> transactionList() {
        return Arbitraries.integers().between(1, 5).flatMap(size -> {
            List<Arbitrary<Transaction>> transactionArbitraries = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                transactionArbitraries.add(validTransaction());
            }
            return Combinators.combine(transactionArbitraries).as(list -> list);
        });
    }
}
