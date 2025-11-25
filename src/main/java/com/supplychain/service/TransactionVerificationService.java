package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.Transaction;

import java.util.List;
import java.util.Objects;

/**
 * Service for verifying transactions against blockchain records.
 * Provides methods to verify individual transactions and validate blockchain integrity.
 * 
 * Requirements: 5.1, 5.2, 5.3
 */
public class TransactionVerificationService {
    private BlockchainManager blockchainManager;
    
    /**
     * Constructor that initializes the service with a blockchain manager.
     * 
     * @param blockchainManager The blockchain manager to use for verification
     */
    public TransactionVerificationService(BlockchainManager blockchainManager) {
        if (blockchainManager == null) {
            throw new IllegalArgumentException("BlockchainManager cannot be null");
        }
        this.blockchainManager = blockchainManager;
    }
    
    /**
     * Verifies a transaction by comparing it with the blockchain record.
     * Requirements: 5.1 - Transaction verification round-trip
     * Requirements: 5.3 - Clear verification status
     * 
     * @param originalTransaction The original transaction to verify
     * @return TransactionVerificationResult containing verification status and details
     */
    public TransactionVerificationResult verifyTransaction(Transaction originalTransaction) {
        if (originalTransaction == null) {
            return new TransactionVerificationResult(false, "Transaction cannot be null");
        }
        
        String transactionId = originalTransaction.getTransactionId();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return new TransactionVerificationResult(false, "Transaction ID is required");
        }
        
        // Retrieve all transactions from blockchain
        List<Transaction> blockchainTransactions = blockchainManager.getTransactionHistory();
        
        // Find the transaction in the blockchain
        Transaction blockchainTransaction = null;
        for (Transaction t : blockchainTransactions) {
            if (transactionId.equals(t.getTransactionId())) {
                blockchainTransaction = t;
                break;
            }
        }
        
        // Check if transaction exists in blockchain
        if (blockchainTransaction == null) {
            return new TransactionVerificationResult(false, 
                "Transaction not found in blockchain: " + transactionId);
        }
        
        // Compare transaction details
        if (!compareTransactions(originalTransaction, blockchainTransaction)) {
            return new TransactionVerificationResult(false, 
                "Transaction data does not match blockchain record");
        }
        
        // Transaction verified successfully
        return new TransactionVerificationResult(true, 
            "Transaction verified successfully: data matches blockchain record");
    }
    
    /**
     * Validates the integrity of the entire blockchain.
     * Requirements: 5.2 - Hash validation detects tampering
     * Requirements: 5.3 - Clear verification status
     * 
     * @return TransactionVerificationResult containing validation status and details
     */
    public TransactionVerificationResult validateBlockchainIntegrity() {
        boolean isValid = blockchainManager.isChainValid();
        
        if (isValid) {
            return new TransactionVerificationResult(true, 
                "Blockchain integrity validated: all blocks have valid hashes and proper linkage");
        } else {
            return new TransactionVerificationResult(false, 
                "Blockchain integrity compromised: hash validation failed or improper block linkage detected");
        }
    }
    
    /**
     * Compares two transactions to check if they have the same data.
     * 
     * @param t1 First transaction
     * @param t2 Second transaction
     * @return true if transactions have matching data, false otherwise
     */
    private boolean compareTransactions(Transaction t1, Transaction t2) {
        // Compare transaction IDs
        if (!Objects.equals(t1.getTransactionId(), t2.getTransactionId())) {
            return false;
        }
        
        // Compare transaction types
        if (!Objects.equals(t1.getTransactionType(), t2.getTransactionType())) {
            return false;
        }
        
        // Compare timestamps (allowing for minor differences due to serialization)
        if (t1.getTimestamp() != null && t2.getTimestamp() != null) {
            // Timestamps should be equal or very close
            if (!t1.getTimestamp().equals(t2.getTimestamp())) {
                return false;
            }
        } else if (t1.getTimestamp() != t2.getTimestamp()) {
            return false;
        }
        
        // Compare transaction data
        if (!Objects.equals(t1.getTransactionData(), t2.getTransactionData())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the blockchain manager used by this service.
     * 
     * @return The blockchain manager
     */
    public BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
    
    /**
     * Sets the blockchain manager for this service.
     * 
     * @param blockchainManager The blockchain manager to use
     */
    public void setBlockchainManager(BlockchainManager blockchainManager) {
        if (blockchainManager == null) {
            throw new IllegalArgumentException("BlockchainManager cannot be null");
        }
        this.blockchainManager = blockchainManager;
    }
}
