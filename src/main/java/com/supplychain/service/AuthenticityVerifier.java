package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.Transaction;

import java.util.List;

/**
 * Service for verifying product authenticity using blockchain records.
 * Validates that products are genuine by checking blockchain integrity
 * and transaction chain validity.
 * 
 * Requirements: 7.1, 7.2, 7.3
 */
public class AuthenticityVerifier {
    private BlockchainManager blockchainManager;
    
    /**
     * Constructor that initializes the verifier with a blockchain manager.
     * 
     * @param blockchainManager The blockchain manager to use for verification
     */
    public AuthenticityVerifier(BlockchainManager blockchainManager) {
        if (blockchainManager == null) {
            throw new IllegalArgumentException("BlockchainManager cannot be null");
        }
        this.blockchainManager = blockchainManager;
    }
    
    /**
     * Verifies the authenticity of a product using blockchain records.
     * Validates the product identifier against blockchain records and checks
     * that all transaction records form a valid chain with proper cryptographic linkage.
     * 
     * Requirements: 7.1 - Product identifier validation against blockchain records
     * Requirements: 7.2 - Transaction chain validation with cryptographic linkage
     * Requirements: 7.3 - Clear confirmation or rejection status
     * 
     * @param productId The unique identifier of the product to verify
     * @return VerificationResult containing the verification status and details
     * @throws IllegalArgumentException if productId is null or empty
     */
    public VerificationResult verifyProductAuthenticity(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        VerificationResult result = new VerificationResult(productId);
        
        // Step 1: Check if product exists in blockchain
        List<Transaction> productHistory = blockchainManager.getProductHistory(productId);
        
        if (productHistory.isEmpty()) {
            result.setAuthentic(false);
            result.setStatus("REJECTED");
            result.addReason("Product not found in blockchain records");
            return result;
        }
        
        // Step 2: Validate blockchain integrity
        boolean chainValid = blockchainManager.isChainValid();
        
        if (!chainValid) {
            result.setAuthentic(false);
            result.setStatus("REJECTED");
            result.addReason("Blockchain integrity check failed - tampering detected");
            return result;
        }
        
        // Step 3: Validate that all transactions in the product chain are valid
        for (Transaction transaction : productHistory) {
            if (!transaction.validate()) {
                result.setAuthentic(false);
                result.setStatus("REJECTED");
                result.addReason("Invalid transaction found in product history: " + transaction.getTransactionId());
                return result;
            }
        }
        
        // All checks passed
        result.setAuthentic(true);
        result.setStatus("CONFIRMED");
        result.addReason("Product verified successfully with " + productHistory.size() + " valid transactions");
        
        return result;
    }
    
    /**
     * Gets the blockchain manager used by this verifier.
     * 
     * @return The blockchain manager
     */
    public BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
}
