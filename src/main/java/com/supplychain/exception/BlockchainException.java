package com.supplychain.exception;

/**
 * Base exception class for all blockchain-related errors.
 * This exception serves as the parent for specific blockchain operation failures.
 */
public class BlockchainException extends Exception {
    
    /**
     * Constructs a new BlockchainException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public BlockchainException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new BlockchainException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception
     */
    public BlockchainException(String message, Throwable cause) {
        super(message, cause);
    }
}
