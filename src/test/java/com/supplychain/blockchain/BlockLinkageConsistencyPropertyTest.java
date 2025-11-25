package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Property-based test for block linkage consistency.
 * Feature: blockchain-supply-chain, Property 11: Block linkage consistency
 * Validates: Requirements 4.3
 */
public class BlockLinkageConsistencyPropertyTest {
    
    /**
     * Property 11: Block linkage consistency
     * For any sequence of blocks created, each new block should have a previousHash 
     * that matches the hash of the block created immediately before it.
     */
    @Property(tries = 100)
    @Label("Block linkage consistency")
    void blockLinkageConsistency(
        @ForAll("numberOfBlocks") int numBlocks
    ) {
        // Create a new blockchain
        BlockchainManager blockchain = new BlockchainManager(2);
        
        // Track the hash of each block as we create them
        String previousHash = blockchain.getLatestBlock().getHash();
        
        // Create multiple blocks sequentially
        for (int i = 0; i < numBlocks; i++) {
            // Create a valid transaction
            Transaction tx = new ProductCreationTransaction(
                "tx-" + i,
                "supplier-" + i,
                "product-" + i,
                "Product " + i,
                "Description " + i,
                "Origin " + i
            );
            
            // Add transaction and mine block
            blockchain.addTransaction(tx);
            Block newBlock = blockchain.minePendingTransactions();
            
            // Verify the new block's previousHash matches the previous block's hash
            Assertions.assertEquals(previousHash, newBlock.getPreviousHash(),
                "Block " + newBlock.getIndex() + " previousHash should match previous block's hash");
            
            // Update previousHash for next iteration
            previousHash = newBlock.getHash();
        }
        
        // Final verification: check the entire chain is valid
        Assertions.assertTrue(blockchain.isChainValid(),
            "The entire blockchain should be valid after sequential block creation");
    }
    
    /**
     * Provides arbitrary number of blocks to create (between 1 and 10).
     */
    @Provide
    Arbitrary<Integer> numberOfBlocks() {
        return Arbitraries.integers().between(1, 10);
    }
}
