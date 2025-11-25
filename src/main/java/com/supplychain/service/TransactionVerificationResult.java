package com.supplychain.service;

/**
 * Represents the result of a transaction verification.
 * Contains a clear boolean status and descriptive message.
 * 
 * Requirements: 5.3 - Clear verification status
 */
public class TransactionVerificationResult {
    private boolean verified;
    private String message;
    
    /**
     * Constructor that initializes a verification result.
     * 
     * @param verified Whether the verification was successful
     * @param message Descriptive message about the verification result
     */
    public TransactionVerificationResult(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }
    
    /**
     * Gets the verification status.
     * 
     * @return true if verification succeeded, false otherwise
     */
    public boolean isVerified() {
        return verified;
    }
    
    /**
     * Sets the verification status.
     * 
     * @param verified The verification status
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    /**
     * Gets the descriptive message about the verification result.
     * 
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the descriptive message.
     * 
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "TransactionVerificationResult{" +
                "verified=" + verified +
                ", message='" + message + '\'' +
                '}';
    }
}
