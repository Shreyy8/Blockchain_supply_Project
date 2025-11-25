package com.supplychain.blockchain;

import com.supplychain.model.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a block in the blockchain.
 * Each block contains a list of transactions, a hash of the previous block,
 * and its own hash calculated using SHA-256.
 * 
 * Requirements: 4.2, 4.3, 5.2
 */
public class Block {
    private int index;
    private LocalDateTime timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;
    private long nonce;
    
    /**
     * Constructor for creating a new block.
     * 
     * @param index The position of this block in the chain
     * @param transactions List of transactions to include in this block
     * @param previousHash Hash of the previous block in the chain
     */
    public Block(int index, List<Transaction> transactions, String previousHash) {
        this.index = index;
        this.timestamp = LocalDateTime.now();
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }
    
    /**
     * Full constructor with all fields (useful for loading from database).
     * 
     * @param index The position of this block in the chain
     * @param timestamp The timestamp when the block was created
     * @param transactions List of transactions in this block
     * @param previousHash Hash of the previous block
     * @param hash The hash of this block
     * @param nonce The nonce value used for mining
     */
    public Block(int index, LocalDateTime timestamp, List<Transaction> transactions, 
                 String previousHash, String hash, long nonce) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
    }
    
    /**
     * Calculates the SHA-256 hash of this block.
     * The hash is calculated from the block's index, timestamp, transactions,
     * previous hash, and nonce.
     * 
     * Requirements: 4.2 - Hash calculation using SHA-256
     * Requirements: 5.2 - Hash validation for tampering detection
     * 
     * @return The calculated hash as a hexadecimal string
     */
    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Build the data string to hash
            StringBuilder data = new StringBuilder();
            data.append(index);
            data.append(timestamp.toString());
            data.append(previousHash);
            data.append(nonce);
            
            // Add transaction data
            for (Transaction transaction : transactions) {
                data.append(transaction.getTransactionId());
                data.append(transaction.getTransactionType());
                data.append(transaction.getTimestamp().toString());
            }
            
            // Calculate hash
            byte[] hashBytes = digest.digest(data.toString().getBytes(StandardCharsets.UTF_8));
            
            // Convert to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Mines this block using proof-of-work.
     * Increments the nonce until a hash is found that meets the difficulty requirement
     * (hash starts with a certain number of zeros).
     * 
     * Requirements: 4.2 - Proof-of-work mining
     * 
     * @param difficulty The number of leading zeros required in the hash
     */
    public void mineBlock(int difficulty) {
        // Create target string with required number of leading zeros
        String target = new String(new char[difficulty]).replace('\0', '0');
        
        // Keep incrementing nonce until hash meets difficulty requirement
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }
    
    /**
     * Validates that the block's stored hash matches its calculated hash.
     * This detects if the block data has been tampered with.
     * 
     * Requirements: 5.2 - Hash validation detects tampering
     * 
     * @return true if the hash is valid, false if tampering is detected
     */
    public boolean isHashValid() {
        return hash.equals(calculateHash());
    }
    
    // Getters and Setters
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    public String getHash() {
        return hash;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public long getNonce() {
        return nonce;
    }
    
    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index && Objects.equals(hash, block.hash);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(index, hash);
    }
    
    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", transactionCount=" + transactions.size() +
                ", previousHash='" + previousHash + '\'' +
                ", hash='" + hash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}
