package com.supplychain.blockchain;

import com.supplychain.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the blockchain and transaction processing.
 * Provides methods to add transactions, mine blocks, validate the chain,
 * and retrieve transaction history.
 * 
 * Requirements: 1.1, 1.2, 1.3, 1.4, 4.2, 4.3, 5.1, 6.1
 */
public class BlockchainManager {
    private List<Block> chain;
    private List<Transaction> pendingTransactions;
    private int difficulty;
    
    /**
     * Constructor that initializes the blockchain with a genesis block.
     * 
     * @param difficulty The mining difficulty (number of leading zeros required in hash)
     */
    public BlockchainManager(int difficulty) {
        this.chain = new ArrayList<>();
        this.pendingTransactions = new ArrayList<>();
        this.difficulty = difficulty;
        
        // Create genesis block
        createGenesisBlock();
    }
    
    /**
     * Creates the first block in the blockchain (genesis block).
     */
    private void createGenesisBlock() {
        List<Transaction> emptyTransactions = new ArrayList<>();
        Block genesisBlock = new Block(0, emptyTransactions, "0");
        genesisBlock.mineBlock(difficulty);
        chain.add(genesisBlock);
    }
    
    /**
     * Adds a transaction to the pending transactions list after validation.
     * Requirements: 1.2 - Transaction persistence and availability
     * Requirements: 4.1 - Transaction validation
     * 
     * @param transaction The transaction to add
     * @throws IllegalArgumentException if the transaction is invalid
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        if (!transaction.validate()) {
            throw new IllegalArgumentException("Invalid transaction: validation failed for transaction " 
                + transaction.getTransactionId());
        }
        
        pendingTransactions.add(transaction);
    }
    
    /**
     * Mines all pending transactions into a new block and adds it to the chain.
     * Requirements: 4.2 - Block creation with cryptographic hash
     * Requirements: 4.3 - Block linkage using cryptographic hashing
     * 
     * @return The newly created block, or null if there are no pending transactions
     */
    public Block minePendingTransactions() {
        if (pendingTransactions.isEmpty()) {
            return null;
        }
        
        // Create new block with pending transactions
        Block latestBlock = getLatestBlock();
        Block newBlock = new Block(
            chain.size(),
            new ArrayList<>(pendingTransactions),
            latestBlock.getHash()
        );
        
        // Mine the block
        newBlock.mineBlock(difficulty);
        
        // Add to chain
        chain.add(newBlock);
        
        // Clear pending transactions
        pendingTransactions.clear();
        
        return newBlock;
    }
    
    /**
     * Validates the integrity of the entire blockchain.
     * Checks that all blocks have valid hashes and proper linkage.
     * Requirements: 1.4 - Blockchain hash linkage verification
     * Requirements: 5.2 - Hash validation detects tampering
     * 
     * @return true if the chain is valid, false if tampering is detected
     */
    public boolean isChainValid() {
        // Genesis block is always valid
        if (chain.size() <= 1) {
            return true;
        }
        
        // Check each block starting from index 1
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            
            // Check if current block's hash is valid
            if (!currentBlock.isHashValid()) {
                return false;
            }
            
            // Check if current block's previousHash matches previous block's hash
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Retrieves all transactions from the blockchain in chronological order.
     * Requirements: 1.1 - Transaction retrieval with complete details
     * Requirements: 1.3 - Chronological ordering of transactions
     * 
     * @return List of all transactions in the blockchain
     */
    public List<Transaction> getTransactionHistory() {
        List<Transaction> allTransactions = new ArrayList<>();
        
        // Iterate through all blocks and collect transactions
        for (Block block : chain) {
            allTransactions.addAll(block.getTransactions());
        }
        
        // Transactions are already in chronological order since blocks are ordered
        return allTransactions;
    }
    
    /**
     * Retrieves all transactions associated with a specific product.
     * Requirements: 6.1 - Product history retrieval
     * 
     * @param productId The ID of the product to retrieve history for
     * @return List of transactions involving the specified product
     */
    public List<Transaction> getProductHistory(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        List<Transaction> productTransactions = new ArrayList<>();
        
        // Get all transactions
        List<Transaction> allTransactions = getTransactionHistory();
        
        // Filter transactions that involve the specified product
        for (Transaction transaction : allTransactions) {
            Object productIdData = transaction.getTransactionData().get("productId");
            if (productIdData != null && productId.equals(productIdData.toString())) {
                productTransactions.add(transaction);
            }
        }
        
        return productTransactions;
    }
    
    /**
     * Gets the latest block in the chain.
     * 
     * @return The most recent block
     */
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    
    /**
     * Gets the entire blockchain.
     * 
     * @return List of all blocks in the chain
     */
    public List<Block> getChain() {
        return new ArrayList<>(chain);
    }
    
    /**
     * Gets the list of pending transactions.
     * 
     * @return List of pending transactions
     */
    public List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }
    
    /**
     * Gets the mining difficulty.
     * 
     * @return The difficulty level
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    /**
     * Sets the mining difficulty.
     * 
     * @param difficulty The new difficulty level
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
