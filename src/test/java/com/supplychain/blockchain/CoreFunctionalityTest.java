package com.supplychain.blockchain;

import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import com.supplychain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for core blockchain functionality.
 * Tests block hash calculation, blockchain operations, and edge cases.
 */
class CoreFunctionalityTest {
    
    private BlockchainManager blockchain;
    
    @BeforeEach
    void setUp() {
        blockchain = new BlockchainManager(1);
    }
    
    // Block Hash Calculation Tests
    
    @Test
    @DisplayName("Test block hash calculation with known inputs")
    void testHashCalculationWithKnownInputs() {
        List<Transaction> transactions = new ArrayList<>();
        Block block = new Block(0, transactions, "0");
        
        String hash = block.calculateHash();
        
        // Hash should be 64 characters (SHA-256)
        assertEquals(64, hash.length(), "SHA-256 hash should be 64 characters");
        assertTrue(hash.matches("[0-9a-f]+"), "Hash should only contain hex characters");
        
        // Recalculating should produce same result
        assertEquals(hash, block.calculateHash(), "Hash calculation should be deterministic");
    }
    
    @Test
    @DisplayName("Test different blocks produce different hashes")
    void testDifferentBlocksDifferentHashes() {
        List<Transaction> transactions = new ArrayList<>();
        Block block1 = new Block(0, transactions, "0");
        Block block2 = new Block(1, transactions, "0");
        
        assertNotEquals(block1.getHash(), block2.getHash());
    }
    
    @Test
    @DisplayName("Test hash validation detects tampering")
    void testHashValidationDetectsTampering() {
        List<Transaction> transactions = new ArrayList<>();
        Block block = new Block(1, transactions, "previoushash");
        
        assertTrue(block.isHashValid(), "Hash should be valid initially");
        
        // Tamper with the block
        block.setIndex(999);
        
        assertFalse(block.isHashValid(), "Hash validation should detect tampering");
    }
    
    // Empty Blockchain Tests
    
    @Test
    @DisplayName("Test empty blockchain initialization")
    void testEmptyBlockchainInitialization() {
        assertEquals(1, blockchain.getChain().size(), "New blockchain should have genesis block");
        assertEquals(0, blockchain.getPendingTransactions().size(), "No pending transactions initially");
        assertTrue(blockchain.isChainValid(), "Genesis block should be valid");
    }
    
    @Test
    @DisplayName("Test genesis block properties")
    void testGenesisBlockProperties() {
        Block genesisBlock = blockchain.getChain().get(0);
        
        assertEquals(0, genesisBlock.getIndex(), "Genesis block should have index 0");
        assertEquals("0", genesisBlock.getPreviousHash(), "Genesis block previous hash should be '0'");
        assertEquals(0, genesisBlock.getTransactions().size(), "Genesis block should have no transactions");
        assertNotNull(genesisBlock.getHash(), "Genesis block should have a hash");
    }
    
    // Single Block Tests
    
    @Test
    @DisplayName("Test single block blockchain")
    void testSingleBlockBlockchain() {
        assertEquals(1, blockchain.getChain().size());
        assertTrue(blockchain.isChainValid());
        
        Block latestBlock = blockchain.getLatestBlock();
        assertEquals(0, latestBlock.getIndex());
    }
    
    // Missing Fields Tests
    
    @Test
    @DisplayName("Test adding null transaction throws exception")
    void testAddNullTransaction() {
        assertThrows(IllegalArgumentException.class, () -> {
            blockchain.addTransaction(null);
        }, "Adding null transaction should throw IllegalArgumentException");
    }
    
    @Test
    @DisplayName("Test get product history with null product ID")
    void testGetProductHistoryWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            blockchain.getProductHistory(null);
        }, "Null product ID should throw IllegalArgumentException");
    }
    
    @Test
    @DisplayName("Test get product history with empty product ID")
    void testGetProductHistoryWithEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            blockchain.getProductHistory("");
        }, "Empty product ID should throw IllegalArgumentException");
    }
    
    // Transaction Tests with Valid Data
    
    @Test
    @DisplayName("Test adding valid transaction")
    void testAddValidTransaction() {
        Transaction transaction = new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        );
        
        blockchain.addTransaction(transaction);
        
        assertEquals(1, blockchain.getPendingTransactions().size());
    }
    
    @Test
    @DisplayName("Test mining pending transactions")
    void testMinePendingTransactions() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN002", "SUP002", "PROD002", "Tea Leaves", "Green tea", "China"
        ));
        
        assertEquals(2, blockchain.getPendingTransactions().size());
        
        Block newBlock = blockchain.minePendingTransactions();
        
        assertNotNull(newBlock, "Mining should return new block");
        assertEquals(2, newBlock.getTransactions().size(), "New block should contain 2 transactions");
        assertEquals(0, blockchain.getPendingTransactions().size(), "Pending transactions should be cleared");
        assertEquals(2, blockchain.getChain().size(), "Chain should have 2 blocks");
    }
    
    @Test
    @DisplayName("Test mining with no pending transactions")
    void testMineWithNoPendingTransactions() {
        Block result = blockchain.minePendingTransactions();
        
        assertNull(result, "Mining with no pending transactions should return null");
        assertEquals(1, blockchain.getChain().size(), "Chain should still have only genesis block");
    }
    
    @Test
    @DisplayName("Test blockchain validation")
    void testBlockchainValidation() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.minePendingTransactions();
        
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN002", "SUP002", "PROD002", "Tea Leaves", "Green tea", "China"
        ));
        blockchain.minePendingTransactions();
        
        assertTrue(blockchain.isChainValid(), "Blockchain should be valid");
    }
    
    @Test
    @DisplayName("Test blockchain detects tampering")
    void testBlockchainDetectsTampering() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.minePendingTransactions();
        
        // Tamper with a block
        Block block = blockchain.getChain().get(1);
        block.setIndex(999);
        
        assertFalse(blockchain.isChainValid(), "Blockchain should detect tampering");
    }
    
    @Test
    @DisplayName("Test get transaction history")
    void testGetTransactionHistory() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.minePendingTransactions();
        
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN002", "SUP002", "PROD002", "Tea Leaves", "Green tea", "China"
        ));
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN003", "SUP003", "PROD003", "Cocoa Beans", "Dark cocoa", "Ghana"
        ));
        blockchain.minePendingTransactions();
        
        List<Transaction> history = blockchain.getTransactionHistory();
        
        assertEquals(3, history.size(), "Should retrieve all 3 transactions");
    }
    
    @Test
    @DisplayName("Test get product history")
    void testGetProductHistory() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.addTransaction(new ProductTransferTransaction(
            "TXN002", "SUP001", "RET001", "PROD001", "Warehouse A", "Store B", ProductStatus.IN_TRANSIT
        ));
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN003", "SUP002", "PROD002", "Tea Leaves", "Green tea", "China"
        ));
        blockchain.minePendingTransactions();
        
        List<Transaction> prod001History = blockchain.getProductHistory("PROD001");
        
        assertEquals(2, prod001History.size(), "PROD001 should have 2 transactions");
        
        List<Transaction> prod002History = blockchain.getProductHistory("PROD002");
        
        assertEquals(1, prod002History.size(), "PROD002 should have 1 transaction");
    }
    
    @Test
    @DisplayName("Test get product history for non-existent product")
    void testGetProductHistoryForNonExistentProduct() {
        blockchain.addTransaction(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        blockchain.minePendingTransactions();
        
        List<Transaction> history = blockchain.getProductHistory("PROD999");
        
        assertEquals(0, history.size(), "Non-existent product should have empty history");
    }
    
    @Test
    @DisplayName("Test block with empty transactions")
    void testBlockWithEmptyTransactions() {
        List<Transaction> emptyTransactions = new ArrayList<>();
        Block block = new Block(0, emptyTransactions, "0");
        
        assertNotNull(block.getHash());
        assertEquals(64, block.getHash().length());
        assertEquals(0, block.getTransactions().size());
    }
    
    @Test
    @DisplayName("Test block with single transaction")
    void testBlockWithSingleTransaction() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new ProductCreationTransaction(
            "TXN001", "SUP001", "PROD001", "Coffee Beans", "Premium beans", "Colombia"
        ));
        
        Block block = new Block(1, transactions, "previoushash");
        
        assertNotNull(block.getHash());
        assertEquals(1, block.getTransactions().size());
        assertTrue(block.isHashValid());
    }
    
    @Test
    @DisplayName("Test multiple blocks maintain proper linkage")
    void testMultipleBlocksLinkage() {
        for (int i = 1; i <= 5; i++) {
            blockchain.addTransaction(new ProductCreationTransaction(
                "TXN" + i, "SUP" + i, "PROD" + i, "Product " + i, "Description " + i, "Origin " + i
            ));
            blockchain.minePendingTransactions();
        }
        
        assertEquals(6, blockchain.getChain().size());
        
        List<Block> chain = blockchain.getChain();
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            assertEquals(previousBlock.getHash(), currentBlock.getPreviousHash(),
                "Block " + i + " should link to previous block");
        }
        
        assertTrue(blockchain.isChainValid());
    }
}
