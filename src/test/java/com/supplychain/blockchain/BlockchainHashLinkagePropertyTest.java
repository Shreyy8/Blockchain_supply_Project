package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.Transaction;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * Property-based test for blockchain hash linkage integrity.
 * Feature: blockchain-supply-chain, Property 4: Blockchain hash linkage integrity
 * Validates: Requirements 1.4
 */
public class BlockchainHashLinkagePropertyTest {
    
    /**
     * Property 4: Blockchain hash linkage integrity
     * For any blockchain with multiple blocks, each block's previousHash field 
     * should equal the hash of the preceding block, forming a valid chain.
     */
    @Property(tries = 100)
    @Label("Blockchain hash linkage integrity")
    void blockchainHashLinkageIntegrity(
        @ForAll("blockchainWithMultipleBlocks") BlockchainManager blockchain
    ) {
        List<Block> chain = blockchain.getChain();
        
        // For blockchains with multiple blocks
        if (chain.size() > 1) {
            // Check each block starting from index 1
            for (int i = 1; i < chain.size(); i++) {
                Block currentBlock = chain.get(i);
                Block previousBlock = chain.get(i - 1);
                
                // Each block's previousHash should equal the previous block's hash
                Assertions.assertEquals(
                    previousBlock.getHash(),
                    currentBlock.getPreviousHash(),
                    "Block " + i + " previousHash should match block " + (i-1) + " hash"
                );
            }
        }
    }
    
    /**
     * Provides arbitrary blockchains with multiple blocks for testing.
     */
    @Provide
    Arbitrary<BlockchainManager> blockchainWithMultipleBlocks() {
        return Arbitraries.integers().between(2, 10).flatMap(numBlocks -> {
            // Create blockchain with difficulty 2 for faster testing
            BlockchainManager blockchain = new BlockchainManager(2);
            
            // Add random number of blocks
            for (int i = 0; i < numBlocks; i++) {
                // Generate random transactions for each block
                Arbitrary<List<Transaction>> transactionsArb = validTransactions()
                    .list().ofMinSize(1).ofMaxSize(5);
                
                List<Transaction> transactions = transactionsArb.sample();
                
                // Add transactions and mine block
                for (Transaction tx : transactions) {
                    blockchain.addTransaction(tx);
                }
                blockchain.minePendingTransactions();
            }
            
            return Arbitraries.just(blockchain);
        });
    }
    
    /**
     * Provides arbitrary valid transactions for testing.
     */
    @Provide
    Arbitrary<Transaction> validTransactions() {
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
