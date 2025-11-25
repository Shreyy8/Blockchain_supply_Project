package com.supplychain.exception;

/**
 * Exception thrown when blockchain integrity check fails.
 * This occurs when the chain's cryptographic linkage is broken,
 * blocks are out of order, or the chain has been tampered with.
 */
public class ChainValidationException extends BlockchainException {
    
    /**
     * Constructs a new ChainValidationException with the specified detail message.
     * 
     * @param message the detail message explaining the chain validation failure
     */
    public ChainValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ChainValidationException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the chain validation failure
     * @param cause the cause of this exception
     */
    public ChainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
