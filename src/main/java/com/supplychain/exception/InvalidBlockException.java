package com.supplychain.exception;

/**
 * Exception thrown when block validation fails.
 * This occurs when a block does not meet the required validation criteria,
 * such as invalid hash, incorrect linkage, or malformed data.
 */
public class InvalidBlockException extends BlockchainException {
    
    /**
     * Constructs a new InvalidBlockException with the specified detail message.
     * 
     * @param message the detail message explaining why the block is invalid
     */
    public InvalidBlockException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidBlockException with the specified detail message and cause.
     * 
     * @param message the detail message explaining why the block is invalid
     * @param cause the cause of this exception
     */
    public InvalidBlockException(String message, Throwable cause) {
        super(message, cause);
    }
}
