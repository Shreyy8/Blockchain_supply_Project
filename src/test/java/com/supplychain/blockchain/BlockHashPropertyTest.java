package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for block hash calculation.
 * 
 * Feature: blockchain-supply-chain, Property 10: Valid transaction creates proper block
 * Validates: Requirements 4.2
 * 
 * This test verifies that for any valid transaction, creating a block should result
 * in a block containing the transaction details and a valid cryptographic hash.
 */
public class BlockHashPropertyTest {
    
    /**
     * Property: Valid transaction creates proper block with valid hash
     * For any valid transaction, creating a block should produce a block
     * that contains the transaction and has a valid SHA-256 hash.
     */
    @Property(tries = 100)
    @Label("Valid transaction creates proper block with valid hash")
    void validTransactionCreatesProperBlock(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String productName,
            @ForAll String productDescription,
            @ForAll @NotBlank String origin) {
        
        // Create a valid transaction
        Transaction transaction = new ProductCreationTransaction(
            transactionId.trim(),
            supplierId.trim(),
            productId.trim(),
            productName.trim(),
            productDescription != null ? productDescription : "",
            origin.trim()
        );
        
        // Create a block with the transaction
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Verify the block contains the transaction
        assertNotNull(block.getTransactions(), "Block should have transactions list");
        assertEquals(1, block.getTransactions().size(), 
            "Block should contain exactly one transaction");
        assertEquals(transactionId.trim(), block.getTransactions().get(0).getTransactionId(),
            "Block should contain the correct transaction");
        
        // Verify the block has a valid hash
        assertNotNull(block.getHash(), "Block should have a hash");
        assertEquals(64, block.getHash().length(), 
            "SHA-256 hash should be 64 characters (256 bits in hex)");
        assertTrue(block.getHash().matches("[0-9a-f]{64}"),
            "Hash should be a valid hexadecimal string");
        
        // Verify the hash is correctly calculated
        String calculatedHash = block.calculateHash();
        assertEquals(block.getHash(), calculatedHash,
            "Block's stored hash should match calculated hash");
        
        // Verify block properties
        assertEquals(blockIndex, block.getIndex(), "Block should have correct index");
        assertEquals(previousHash.trim(), block.getPreviousHash(), 
            "Block should have correct previous hash");
        assertNotNull(block.getTimestamp(), "Block should have a timestamp");
    }
    
    /**
     * Property: Block with multiple transactions creates valid hash
     * For any set of valid transactions, creating a block should produce
     * a valid hash that incorporates all transaction data.
     */
    @Property(tries = 100)
    @Label("Block with multiple transactions creates valid hash")
    void blockWithMultipleTransactionsCreatesValidHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("transactionList") List<Transaction> transactions) {
        
        Assume.that(!transactions.isEmpty());
        
        // Create a block with multiple transactions
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        // Verify the block contains all transactions
        assertEquals(transactions.size(), block.getTransactions().size(),
            "Block should contain all transactions");
        
        // Verify the block has a valid hash
        assertNotNull(block.getHash(), "Block should have a hash");
        assertEquals(64, block.getHash().length(), 
            "SHA-256 hash should be 64 characters");
        assertTrue(block.getHash().matches("[0-9a-f]{64}"),
            "Hash should be a valid hexadecimal string");
        
        // Verify hash is valid
        assertTrue(block.isHashValid(), "Block hash should be valid");
    }
    
    /**
     * Property: Recalculating hash produces same result
     * For any block, recalculating its hash should produce the same result
     * as long as the block data hasn't changed.
     */
    @Property(tries = 100)
    @Label("Recalculating hash produces same result")
    void recalculatingHashProducesSameResult(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex,
            @ForAll @NotBlank String previousHash,
            @ForAll("transactionList") List<Transaction> transactions) {
        
        Assume.that(!transactions.isEmpty());
        
        Block block = new Block(blockIndex, transactions, previousHash.trim());
        
        String originalHash = block.getHash();
        String recalculatedHash = block.calculateHash();
        
        assertEquals(originalHash, recalculatedHash,
            "Recalculating hash should produce the same result");
    }
    
    /**
     * Property: Different block data produces different hash
     * For any two blocks with different data, their hashes should be different.
     */
    @Property(tries = 100)
    @Label("Different block data produces different hash")
    void differentBlockDataProducesDifferentHash(
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex1,
            @ForAll @IntRange(min = 0, max = 10000) int blockIndex2,
            @ForAll @NotBlank String previousHash,
            @ForAll("transactionList") List<Transaction> transactions) {
        
        Assume.that(!transactions.isEmpty());
        Assume.that(blockIndex1 != blockIndex2);
        
        Block block1 = new Block(blockIndex1, transactions, previousHash.trim());
        Block block2 = new Block(blockIndex2, transactions, previousHash.trim());
        
        assertNotEquals(block1.getHash(), block2.getHash(),
            "Blocks with different indices should have different hashes");
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
